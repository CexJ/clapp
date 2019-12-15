package com.cexj.clapp.builder;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.cexj.clapp.channels.IChannel;
import com.cexj.clapp.channels.IChannel_Opened;
import com.cexj.clapp.channels.IOChannel;
import com.cexj.clapp.channels.OChannel;
import com.cexj.clapp.context.ClappContext;
import com.cexj.clapp.utils.DefaultCurrent;
import com.cexj.clapp.utils.FunctionFromFuture;
import com.cexj.clapp.utils.either.Either;

final class IO<T, F extends FunctionFromFuture<T, ?>, G extends FunctionFromFuture<?, ?>, R, N> {

	private final Optional<IO<?, G, ?, R, ?>> optNextReader;
	private final DefaultCurrent<ClappContext, ClappContext> defaultCurrentClappContext;
	private final IOChannel<T> ioChannel;

	private IO(final IOChannel<T> ioChannel, final Optional<IO<?, G, ?, R, ?>> optReader,
			final DefaultCurrent<ClappContext, ClappContext> defaultCurrentClappContext) {
		this.defaultCurrentClappContext = defaultCurrentClappContext;
		this.ioChannel = ioChannel;
		this.optNextReader = optReader;
	}

	static <T, F extends FunctionFromFuture<T, ?>, G extends FunctionFromFuture<?, ?>, R, N> IO<T, F, G, R, N> of(
			IOChannel<T> ioChannel, final Optional<IO<?, G, ?, R, ?>> optReader,
			final DefaultCurrent<ClappContext, ClappContext> defaultCurrentClappContext) {
		return new IO<>(ioChannel, optReader, defaultCurrentClappContext);
	}

	<U> IO<U, FunctionFromFuture<U, F>, F, R, F> thenReadFrom(final IChannel<U> channel) {
		return IO.of(IOChannel.fromIChannel(channel), Optional.of(this), defaultCurrentClappContext.withDefault());
	}
	
	IO<T, F, G, R, N> orFrom(final IChannel<T> channel){
		var executor = defaultCurrentClappContext.getCurrentValue().getIExecutor();
		var newIOChannel = ioChannel.addIChannel(channel, executor);
		return IO.of(newIOChannel, optNextReader, defaultCurrentClappContext);
	}


	IO<T, F, G, R, N> andWriteItTo(final OChannel<T> channel) {
		var newIOChannel = ioChannel.addOChannel(channel);
		return IO.of(newIOChannel, optNextReader, defaultCurrentClappContext);
	}

	IO<T, F, G, R, N> withLocalContext(ClappContext currentContext) {
		var newDefaultCurrentClappContext = defaultCurrentClappContext.withNewCurrent(currentContext);
		return IO.of(ioChannel, optNextReader, newDefaultCurrentClappContext);
	}

	IO<T, F, G, R, N> withGlobalContext(ClappContext defaultContext) {
		var newDefaultCurrentClappContext = DefaultCurrent.fromDefault(defaultContext);
		return IO.of(ioChannel, optNextReader, newDefaultCurrentClappContext);
	}

	IO<Future<T>, FunctionFromFuture<Future<T>, N>, G, R, N> inParallel() {
		return IO.of(ioChannel.inParallel(defaultCurrentClappContext.getCurrentValue().getIExecutor(),
				defaultCurrentClappContext.getCurrentValue().getOExecutor(),
				defaultCurrentClappContext.getCurrentValue().getFutureExceptionHandler()),
				optNextReader, defaultCurrentClappContext);
	}

	@SuppressWarnings("unchecked")
	IChannel_Opened<R> execute(final F f) {
		return IChannel_Opened.fromSupplier(() -> {
			var iChannel  = ioChannel.openIChannel();
			try{
				var t = iChannel.pull().getRight();
				ioChannel.openOChannel().ifPresent(oChannel -> oChannel.pushAndClose(t));
				return optNextReader
						.map(r -> notLastApply(f, t, r))
						.orElse(Either.right((R) f.apply(t)));
			} catch (Exception ex) {
				return Either.left(ex);
			} finally {
				var iExecutor = defaultCurrentClappContext.getCurrentValue().getIExecutor();
				var oExecutor = defaultCurrentClappContext.getCurrentValue().getOExecutor();
				iExecutor.shutdown();
				oExecutor.shutdown();
				iChannel.close();	
			}
		});
	}

	@SuppressWarnings("unchecked")
	private Either<Exception, R> notLastApply(final F f, final T t, final IO<?, G, ?, R, ?> r) {
		try {
			var g = (G) f.apply(t);
			return r.execute(g).pull();
		} catch (InterruptedException | ExecutionException ex) {
			return Either.left(ex);
		}

	}

}

package com.cexj.clapp.builder;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.cexj.clapp.channels.IChannel;
import com.cexj.clapp.channels.IChannel_Open;
import com.cexj.clapp.channels.IOChannel;
import com.cexj.clapp.channels.OChannel;
import com.cexj.clapp.context.ClappContext;
import com.cexj.clapp.utils.DefaultCurrent;
import com.cexj.clapp.utils.FunctionFromFuture;

final class IO<T, F extends FunctionFromFuture<T, ?>, G extends FunctionFromFuture<?, ?>, R> {

	private final Optional<IO<?, G, ?, R>> optNextReader;
	private final DefaultCurrent<ClappContext, ClappContext> defaultCurrentClappContext;
	private final IOChannel<T> ioChannel;

	private IO(final IOChannel<T> ioChannel, final Optional<IO<?, G, ?, R>> optReader,
			final DefaultCurrent<ClappContext, ClappContext> defaultCurrentClappContext) {
		this.defaultCurrentClappContext = defaultCurrentClappContext;
		this.ioChannel = ioChannel;
		this.optNextReader = optReader;
	}

	static <T, F extends FunctionFromFuture<T, ?>, G extends FunctionFromFuture<?, ?>, R> IO<T, F, G, R> of(
			IOChannel<T> ioChannel, final Optional<IO<?, G, ?, R>> optReader,
			final DefaultCurrent<ClappContext, ClappContext> defaultCurrentClappContext) {
		return new IO<>(ioChannel, optReader, defaultCurrentClappContext);
	}

	<U> IO<U, FunctionFromFuture<U, F>, F, R> thenReadFrom(final IChannel<U> channel) {
		return IO.of(IOChannel.fromIChannel(channel), Optional.of(this), defaultCurrentClappContext.withDefault());
	}
	
	IO<T, F, G, R> orFrom(final IChannel<T> channel){
		var executor = defaultCurrentClappContext.getCurrentValue().getIExecutor();
		var handler = defaultCurrentClappContext.getCurrentValue().getFutureExceptionHandler();
		var newIOChannel = ioChannel.addIChannel(channel, executor, handler);
		return IO.of(newIOChannel, optNextReader, defaultCurrentClappContext);
	}


	IO<T, F, G, R> andWriteItTo(final OChannel<T> channel) {
		var newIOChannel = ioChannel.addOChannel(channel);
		return IO.of(newIOChannel, optNextReader, defaultCurrentClappContext);
	}

	IO<T, F, G, R> withLocalContext(ClappContext currentContext) {
		var newDefaultCurrentClappContext = defaultCurrentClappContext.withNewCurrent(currentContext);
		return IO.of(ioChannel, optNextReader, newDefaultCurrentClappContext);
	}

	IO<T, F, G, R> withGlobalContext(ClappContext defaultContext) {
		var newDefaultCurrentClappContext = DefaultCurrent.fromDefault(defaultContext);
		return IO.of(ioChannel, optNextReader, newDefaultCurrentClappContext);
	}

	IO<Future<T>, FunctionFromFuture<Future<T>, ?>, G, R> inParallel() {
		var iExecutor = defaultCurrentClappContext.getCurrentValue().getIExecutor();
		var oExecutor = defaultCurrentClappContext.getCurrentValue().getOExecutor();
		var handler = defaultCurrentClappContext.getCurrentValue().getFutureExceptionHandler();
		var newIOChannel = ioChannel.inParallel(iExecutor, oExecutor, handler);
		return IO.of(newIOChannel, optNextReader, defaultCurrentClappContext);
	}

	@SuppressWarnings("unchecked")
	IChannel_Open<R> execute(final F f) {
		return IChannel_Open.fromSupplier(() -> {
			try(IChannel_Open<T> iChannel  = ioChannel.openIChannel()){
				var t = iChannel.pull();
				var handler = defaultCurrentClappContext.getCurrentValue().getClosingOChannelExceptionHandler();
				ioChannel.openOChannel().ifPresent(oChannel -> oChannel.pushAndClose(t, handler));
				return optNextReader
						.map(r -> notLastApply(f, t, r))
						.orElse((R) f.apply(t));
			} catch (InterruptedException | ExecutionException ex) {
				var handler = defaultCurrentClappContext.getCurrentValue().getFutureExceptionHandler();
				throw handler.handle(ex);
			} catch (Exception ex) {
				var handler = defaultCurrentClappContext.getCurrentValue().getClosingIChannelExceptionHandler();
				throw handler.handle(ex);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private R notLastApply(final F f, final T t, final IO<?, G, ?, R> r) {
		try {
			var g = (G) f.apply(t);
			return r.execute(g).pull();
		} catch (InterruptedException | ExecutionException ex) {
			var handler = defaultCurrentClappContext.getCurrentValue().getFutureExceptionHandler();
			throw handler.handle(ex);
		}

	}

}

package com.cexj.clapp.builder;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.cexj.clapp.channels.IChannel;
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

	<U> IO<U, FunctionFromFuture<U, F>, F, R> andReadFrom(final IChannel<U> channel) {
		return IO.of(IOChannel.fromIChannel(channel), Optional.of(this), defaultCurrentClappContext.withDefault());
	}


	IO<T, F, G, R> andWriteItTo(final OChannel<T> channel) {
		return IO.of(ioChannel.addOChannel(channel), optNextReader, defaultCurrentClappContext);
	}

	IO<T, F, G, R> withLocalContext(ClappContext currentContext) {
		return IO.of(ioChannel, optNextReader, defaultCurrentClappContext.withNewCurrent(currentContext));
	}

	IO<T, F, G, R> withGlobalContext(ClappContext defaultContext) {
		return IO.of(ioChannel, optNextReader, DefaultCurrent.fromDefault(defaultContext));
	}

	IO<Future<T>, FunctionFromFuture<Future<T>, ?>, G, R> inParallel() {
		return IO.of(
				ioChannel.inParallel(defaultCurrentClappContext.getCurrentValue().getExecutor(),
						defaultCurrentClappContext.getCurrentValue().getFutureExceptionHandler()),
				optNextReader, defaultCurrentClappContext);
	}

	@SuppressWarnings("unchecked")
	IChannel<R> execute(final F f) {
		return IChannel.fromSupplier(() -> {
			var t = ioChannel.pullAndPush();
			try {
				return optNextReader.map(r -> notLastApply(f, t, r)).orElse((R) f.apply(t));
			} catch (InterruptedException | ExecutionException ex) {
				throw defaultCurrentClappContext.getCurrentValue().getFutureExceptionHandler().handle(ex);
			}}
		);
	}

	@SuppressWarnings("unchecked")
	private R notLastApply(final F f, final T t, final IO<?, G, ?, R> r) {
		try {
			var g = (G) f.apply(t);
			return r.execute(g).pull();
		} catch (InterruptedException | ExecutionException ex) {
			throw defaultCurrentClappContext.getCurrentValue().getFutureExceptionHandler().handle(ex);
		}

	}

}
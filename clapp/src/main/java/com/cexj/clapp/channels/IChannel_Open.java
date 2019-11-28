package com.cexj.clapp.channels;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.cexj.clapp.exceptions.handler.ClappExceptionRethrowHandler;
import com.cexj.clapp.exceptions.runtime.ClappRuntimeException;
import com.cexj.clapp.utils.Tuple;

public interface IChannel_Open<I> extends AutoCloseable {

	public static <I> IChannel_Open<I> fromSupplier(final Supplier<I> supplier){
		return new IChannel_Open<I>() {
			@Override
			public void close(){
			}

			@Override
			public I pull() {
				return supplier.get();
			}
		};
		
	}
	
	public static <I> IChannel_Open<Future<I>> inParallel(final IChannel_Open<I> channel, final ExecutorService executor, final CompletableFuture<Optional<I>> closeTrigger){
		return new IChannel_Open<Future<I>>() {

			@Override
			public void close() throws Exception {
				closeTrigger.get();
				channel.close();
			}

			@Override
			public Future<I> pull() {
				return executor.submit(() -> channel.pull());
			}
			
		};
	}
	
	
	public I pull();

	public default IChannel_Open<I> pipe(final IChannel_Open<I> channel, final ExecutorService executor, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler){
		var original = this;
		return new IChannel_Open<I>() {

			private Optional<IChannel_Open<I>> optOriginal;
			private Optional<IChannel_Open<I>> optChannel;
			private Optional<Future<I>> optOriginalPull = Optional.empty();
			private Optional<Future<I>> optChannelPull = Optional.empty();
			
			@Override
			public void close() throws Exception {
				optOriginalPull.ifPresent(f -> f.cancel(true));
				optChannelPull.ifPresent(f -> f.cancel(true));
				try {
					closeOptionalChannel(optOriginal,handler);
				} finally {
					closeOptionalChannel(optChannel,handler);
				}
			}

			@Override
			public I pull() {
				var originalPull = executor.submit(() -> original.pull());
				var channelPull = executor.submit(() -> channel.pull());
				optOriginal = Optional.of(original);
				optChannel = Optional.of(channel);
				optOriginalPull = Optional.of(originalPull);
				optChannelPull = Optional.of(channelPull);
				var futures = Stream.of(Tuple.of(original, originalPull), 
						Tuple.of(channel,channelPull)).collect(Collectors.toList());
				var optTupleResult = futures.parallelStream()
						.map(cf -> cf.mapSecond(f -> runFuture(handler, f)))
						.findAny();
				optTupleResult.ifPresent(tr -> 
					futures.stream()
						.filter(cf -> ! cf.firstEquals(tr))
						.forEach(cf -> cleanChannels(handler, cf)));
				return optTupleResult.map(Tuple::getSecond).orElseThrow();
			}

			private void closeOptionalChannel(
					final Optional<IChannel_Open<I>> optChannel, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler) {
				optChannel.ifPresent(c -> {
					try {
						c.close();
					} catch (Exception ex) {
						throw handler.handle(ex);
					}
				});
			}
			
			private I runFuture(ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler, Future<I> f) {
				try {
					return f.get();
				} catch (InterruptedException | ExecutionException ex) {
					throw handler.handle(ex);
				}
			}
			
			private void cleanChannels(ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler,
					Tuple<IChannel_Open<I>, Future<I>> cf) {
				cf.getSecond().cancel(true);
				try {
					cf.getFirst().close();
				} catch (Exception ex) {
					throw handler.handle(ex);
				}
			}

			
		};
	}

}

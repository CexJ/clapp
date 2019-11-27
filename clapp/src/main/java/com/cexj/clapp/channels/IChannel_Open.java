package com.cexj.clapp.channels;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

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
}

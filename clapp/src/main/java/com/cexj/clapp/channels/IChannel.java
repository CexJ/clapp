package com.cexj.clapp.channels;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public interface IChannel<I> extends AutoCloseable {

	public static <I> IChannel<I> fromSupplier(final Supplier<I> supplier){
		return new IChannel<I>() {

			@Override
			public void close() throws Exception {
			}

			@Override
			public I pull() {
				return supplier.get();
			}

			
		};
		
	}
	
	public static <I> IChannel<Future<I>> inParallel(final IChannel<I> channel, final ExecutorService executor){
		return new IChannel<Future<I>>() {

			@Override
			public void close() throws Exception {
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

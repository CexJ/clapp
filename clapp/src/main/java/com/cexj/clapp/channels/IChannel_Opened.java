package com.cexj.clapp.channels;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import com.cexj.clapp.exceptions.handler.ClappExceptionRethrowHandler;
import com.cexj.clapp.exceptions.runtime.ClappRuntimeException;

public interface IChannel_Opened<I> extends AutoCloseable {

	public static <I> IChannel_Opened<I> fromSupplier(final Supplier<I> supplier){
		return new IChannel_Opened<I>() {
			@Override
			public void close(){
			}

			@Override
			public I pull() {
				return supplier.get();
			}
		};
		
	}
	
	public static <I> IChannel_Opened<Future<I>> inParallel(final IChannel_Opened<I> channel, final ExecutorService executor, final Optional<CompletableFuture<Optional<I>>> closeTrigger){
		return new IChannel_Opened<Future<I>>() {

			@Override
			public void close() throws Exception {
				closeTrigger.ifPresent(c -> {
					try {
						c.get();
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				});
				channel.close();
			}

			@Override
			public Future<I> pull() {
				return executor.submit(() -> channel.pull());
			}
			
		};
	}
	
	
	public I pull();

	public default IChannel_Opened<I> pipe(final IChannel_Opened<I> channel, final ExecutorService executor, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler){
		return IChannel_Opened_Piped.getInstance(this, channel, executor, handler);
			
	}	

}

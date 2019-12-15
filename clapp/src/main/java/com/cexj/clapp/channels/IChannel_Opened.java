package com.cexj.clapp.channels;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import com.cexj.clapp.utils.either.Either;

public interface IChannel_Opened<I>{

	public Either<Exception, I> pull();
	public void close();

	
	public static <I> IChannel_Opened<I> fromSupplier(final Supplier<Either<Exception,I>> supplier){
		return new IChannel_Opened<I>() {
			
			public void close(){
			}

			@Override
			public Either<Exception, I> pull() {
				return supplier.get();
			}
		};
		
	}
	
	public static <I> IChannel_Opened<Future<I>> inParallel(final IChannel_Opened<I> channel, final ExecutorService executor, final Optional<CompletableFuture<Optional<I>>> closeTrigger){
		return new IChannel_Opened<Future<I>>() {

			public void close(){
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
			public Either<Exception,Future<I>> pull() {
				return Either.right(executor.submit(() -> channel.pull().getRight()));
			}
			
		};
	}
	
	public default IChannel_Opened<I> pipe(final IChannel_Opened<I> channel, final ExecutorService executor){
		return IChannel_Opened_Piped.getInstance(this, channel, executor);
			
	}	

}

package com.cexj.clapp.channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import com.cexj.clapp.results.PullResult;
import com.cexj.clapp.utils.either.Either;

public interface IChannel_Opened<I>{

	public PullResult<I> pull();
	public List<Exception> close();

	
	public static <I> IChannel_Opened<I> fromSupplier(final Supplier<PullResult<I>> supplier){
		return new IChannel_Opened<I>() {
			
			public List<Exception> close(){
				return new ArrayList<>();
			}

			@Override
			public PullResult<I> pull() {
				return supplier.get();
			}
		};
		
	}
	
	public static <I> IChannel_Opened<Future<I>> inParallel(final IChannel_Opened<I> channel, final ExecutorService executor, final Optional<CompletableFuture<Optional<I>>> closeTrigger){
		return new IChannel_Opened<Future<I>>() {

			public List<Exception> close(){
				closeTrigger.ifPresent(c -> {
					try {
						c.get();
					} catch (InterruptedException | ExecutionException e) {}
				});
				return channel.close();
			}

			@Override
			public PullResult<Future<I>> pull() {
				return PullResult.of(Either.right(executor.submit(() -> channel.pull().getFinalResult().getRight())), new ArrayList<>());
			}
			
		};
	}
	
	public default IChannel_Opened<I> pipe(final IChannel_Opened<I> channel, final ExecutorService executor){
		return IChannel_Opened_Piped.getInstance(this, channel, executor);
			
	}

}

package com.cexj.clapp.channels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.cexj.clapp.utils.either.Either;

public class IChannel_Opened_Piped<I> implements IChannel_Opened<I>{
	
	private final List<IChannel_Opened<I>> listOfChannels;
	private final ExecutorService executor; 
	
	private IChannel_Opened_Piped(final List<IChannel_Opened<I>> listOfChannels, final ExecutorService executor) {
		this.listOfChannels = listOfChannels;
		this.executor = executor;
	}
	
	private static <I> IChannel_Opened_Piped<I> getInstance(final List<IChannel_Opened<I>> listOfChannels, final ExecutorService executor){
		return new IChannel_Opened_Piped<I>(listOfChannels, executor);
	}
	
	public static <I> IChannel_Opened_Piped<I> getInstance(final IChannel_Opened<I> first, IChannel_Opened<I> second, final ExecutorService executor){
		return getInstance(Arrays.asList(first, second), executor);
	}
	
	@Override
	public Optional<Exception> close(){
		return listOfChannels.stream().map(c -> c.close()).findFirst().flatMap(Function.identity());
	}

	@Override
	public Either<Exception, I> pull() {
		List<Callable<I>> s = listOfChannels.stream().<Callable<I>>map(c -> () -> c.pull().getRight()).collect(Collectors.toList());
		try {
			return Either.right(executor.invokeAny(s));
		} catch (InterruptedException | ExecutionException e) {
			return Either.left(e);
		}
	}	

	@Override
	public IChannel_Opened<I> pipe(final IChannel_Opened<I> channel, final ExecutorService executor){
		List<IChannel_Opened<I>> newListOfChannels = new ArrayList<>(listOfChannels);
		newListOfChannels.add(channel);
		return getInstance(newListOfChannels, executor);
	}			
}


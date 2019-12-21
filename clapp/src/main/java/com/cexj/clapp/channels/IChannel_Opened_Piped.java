package com.cexj.clapp.channels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import com.cexj.clapp.results.PullResult;
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
	public List<Exception> close(){
		return listOfChannels.stream().flatMap(c -> c.close().stream()).collect(Collectors.toList());
	}

	@Override
	public PullResult<I> pull() {
		try {
			List<Callable<I>> s = listOfChannels.stream().<Callable<I>>map(c -> () -> c.pull().getFinalResult().getRight()).collect(Collectors.toList());
			return PullResult.of(Either.right(executor.invokeAny(s)), new ArrayList<>());
		} catch (Exception ex) {
			return PullResult.of(Either.left(ex), new ArrayList<>());
		}
	}	

	@Override
	public IChannel_Opened<I> pipe(final IChannel_Opened<I> channel, final ExecutorService executor){
		List<IChannel_Opened<I>> newListOfChannels = new ArrayList<>(listOfChannels);
		newListOfChannels.add(channel);
		return getInstance(newListOfChannels, executor);
	}			
}


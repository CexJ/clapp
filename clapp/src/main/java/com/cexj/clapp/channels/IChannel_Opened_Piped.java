package com.cexj.clapp.channels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import com.cexj.clapp.exceptions.handler.ClappExceptionRethrowHandler;
import com.cexj.clapp.exceptions.runtime.ClappRuntimeException;

public class IChannel_Opened_Piped<I> implements IChannel_Opened<I>{
	
	private final List<IChannel_Opened<I>> listOfChannels;
	private final ExecutorService executor; 
	private final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler;
	
	private IChannel_Opened_Piped(final List<IChannel_Opened<I>> listOfChannels, final ExecutorService executor, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler) {
		this.listOfChannels = listOfChannels;
		this.executor = executor;
		this.handler = handler;
	}
	
	private static <I> IChannel_Opened_Piped<I> getInstance(final List<IChannel_Opened<I>> listOfChannels, final ExecutorService executor, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler){
		return new IChannel_Opened_Piped<I>(listOfChannels, executor, handler);
	}
	
	public static <I> IChannel_Opened_Piped<I> getInstance(final IChannel_Opened<I> first, IChannel_Opened<I> second, final ExecutorService executor, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler){
		return getInstance(Arrays.asList(first, second), executor, handler);
	}
	
	@Override
	public void close() throws Exception {
		listOfChannels.forEach(c -> {
			try {
				c.close();
			} catch (Exception e) {
				handler.handle(e);
			}
		});
	}

	@Override
	public I pull() {
		List<Callable<I>> s = listOfChannels.stream().<Callable<I>>map(c -> () -> c.pull()).collect(Collectors.toList());
		try {
			return executor.invokeAny(s);
		} catch (InterruptedException | ExecutionException e) {
			throw handler.handle(e);
		}
	}	

	@Override
	public IChannel_Opened<I> pipe(final IChannel_Opened<I> channel, final ExecutorService executor, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler){
		List<IChannel_Opened<I>> newListOfChannels = new ArrayList<>(listOfChannels);
		newListOfChannels.add(channel);
		return getInstance(newListOfChannels, executor, handler);
	}			
}


package com.cexj.clapp.builder;

import com.cexj.clapp.channels.IChannel;
import com.cexj.clapp.channels.IChannel_Open;
import com.cexj.clapp.context.ClappContext;
import com.cexj.clapp.utils.FunctionFromFuture;

public final class IO_Parallel<T,F extends FunctionFromFuture<T,?>,G extends FunctionFromFuture<?,?>,R> {

	private final IO<T, F, G, R> io;
	
	private IO_Parallel(final IO<T, F, G, R> io) {
		this.io = io;
	}

	static <T,F extends FunctionFromFuture<T,?>,G extends FunctionFromFuture<?,?>,R> IO_Parallel<T,F,G,R> of(final IO<T,F,G,R> io) {
		return new IO_Parallel<>(io);
	}
	
	public <U> IO_Read<U, FunctionFromFuture<U, F>, F, R> thenReadFrom(final IChannel<U> channel){
		var newIo = io.thenReadFrom(channel);
		return IO_Read.of(newIo);
	}
	
	public IO_Context<T, F, G, R> withLocalContext(ClappContext currentContext){
		var newIo = io.withLocalContext(currentContext);
		return IO_Context.of(newIo);
	}
	
	public IO_Context<T, F, G, R> withGlobalContext(ClappContext defaultContext){
		var newIo = io.withGlobalContext(defaultContext);
		return IO_Context.of(newIo);
	}
	
	public IChannel_Open<R> execute(final F f) {
		return io.execute(f);
	}
	
}

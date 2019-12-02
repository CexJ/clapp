package com.cexj.clapp.builder;

import com.cexj.clapp.channels.IChannel;
import com.cexj.clapp.channels.IChannel_Open;
import com.cexj.clapp.utils.FunctionFromFuture;

public final class IO_Context<T,F extends FunctionFromFuture<T,?>,G extends FunctionFromFuture<?,?>,R,N> {

	private final IO<T, F, G, R, N> io;
	
	private IO_Context(final IO<T, F, G, R, N> io) {
		this.io = io;
	}

	static <T,F extends FunctionFromFuture<T,?>,G extends FunctionFromFuture<?,?>,R> IO_Context<T,F,G,R, ?> of(final IO<T,F,G,R, ?> io) {
		return new IO_Context<>(io);
	}
	
	public <U> IO_Read<U, FunctionFromFuture<U, F>, F, R, ?> thenReadFrom(final IChannel<U> channel){
		var newIo = io.thenReadFrom(channel);
		return IO_Read.of(newIo);
	}
	
	public IChannel_Open<R> execute(final F f) {
		return io.execute(f);
	}
}

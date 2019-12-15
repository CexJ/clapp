package com.cexj.clapp.builder;

import java.util.concurrent.Future;

import com.cexj.clapp.channels.IChannel;
import com.cexj.clapp.channels.IChannel_Opened;
import com.cexj.clapp.channels.OChannel;
import com.cexj.clapp.context.ClappContext;
import com.cexj.clapp.utils.FunctionFromFuture;

public final class IO_Read<T,F extends FunctionFromFuture<T,?>,G extends FunctionFromFuture<?,?>,R, N> {

	private final IO<T, F , G, R, N> io;
	
	private IO_Read(final IO<T, F , G, R, N> io) {
		this.io = io;
	}

	static <T,F extends FunctionFromFuture<T,?>,G extends FunctionFromFuture<?,?>,R,N> IO_Read<T,F,G,R, N> of(final IO<T,F,G,R, N> io) {
		return new IO_Read<>(io);
	}
	
	public <U> IO_Read<U, FunctionFromFuture<U, F>, F, R, F> thenReadFrom(final IChannel<U> channel){
		var newIo = io.thenReadFrom(channel);
		return IO_Read.of(newIo);
	}
	
	public IO_Read<T, F, G, R, N>orFrom(final IChannel<T> channel){
		var newIo = io.orFrom(channel);
		return IO_Read.of(newIo);
	}
	
	public IO_Write<T, F, G, R, N> andWriteItTo(final OChannel<T> channel) {
		var newIo = io.andWriteItTo(channel);
		return IO_Write.of(newIo);
	}
	
	public IO_Parallel<Future<T>, FunctionFromFuture<Future<T>, N>, G, R, ?> inParallel() {
		var newIo = io.inParallel();
		return IO_Parallel.of(newIo);
	}
	
	public IO_Context<T, F, G, R, ?> withLocalContext(ClappContext currentContext){
		var newIo = io.withLocalContext(currentContext);
		return IO_Context.of(newIo);
	}
	
	public IO_Context<T, F, G, R, ?> withGlobalContext(ClappContext defaultContext){
		var newIo = io.withGlobalContext(defaultContext);
		return IO_Context.of(newIo);
	}
	
	public IChannel_Opened<R> execute(final F f) {
		return io.execute(f);
	}
	
}

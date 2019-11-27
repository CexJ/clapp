package com.cexj.clapp.builder;

import com.cexj.clapp.channels.IChannel_Open;
import com.cexj.clapp.utils.FunctionFromFuture;

public final class Type_GlobalContext<R> {

	private final Type<R> type;
	
	private Type_GlobalContext(final Type<R> type) {
		this.type = type;
	}
	
	static <R> Type_GlobalContext<R> of(final Type<R> type) {
		return new Type_GlobalContext<>(type);
	}
	
	public <A> IO_Read<A,FunctionFromFuture<A,R>,FunctionFromFuture<A,R>,R> readFrom(final IChannel_Open<A> channel) {
		return type.readFrom(channel);
	}
	
}

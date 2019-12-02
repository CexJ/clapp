package com.cexj.clapp.builder;

import com.cexj.clapp.channels.IChannel;
import com.cexj.clapp.utils.FunctionFromFuture;

public final class Type_LocalContext<R> {

	private final Type<R> type;
	
	Type_LocalContext(final Type<R> type) {
		this.type = type;
	}
	
	public <A> IO_Read<A,FunctionFromFuture<A,R>,FunctionFromFuture<A,R>,R,R> readFrom(final IChannel<A> channel) {
		return type.readFrom(channel);
	}

}

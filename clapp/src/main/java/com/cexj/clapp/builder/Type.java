package com.cexj.clapp.builder;

import java.util.Optional;

import com.cexj.clapp.channels.IChannel_Open;
import com.cexj.clapp.channels.IOChannel;
import com.cexj.clapp.context.ClappContext;
import com.cexj.clapp.utils.DefaultCurrent;
import com.cexj.clapp.utils.FunctionFromFuture;

final class Type<R>{
	
	private final DefaultCurrent<ClappContext,ClappContext> defaultCurrentContext;
	
	private Type(final DefaultCurrent<ClappContext,ClappContext> defaultCurrentContext) {
		this.defaultCurrentContext = defaultCurrentContext;
	}
	
	static <R> Type<R> of(final DefaultCurrent<ClappContext,ClappContext> defaultCurrentContext){
		return new Type<>(defaultCurrentContext);
	}
	
	Type<R> withLocalContext(ClappContext currentContext){
		return Type.of(defaultCurrentContext.withNewCurrent(currentContext));
	}
	
	<A> IO_Read<A,FunctionFromFuture<A,R>,FunctionFromFuture<A,R>,R> readFrom(final IChannel_Open<A> channel) {
		return IO_Read.of(IO.of(IOChannel.fromIChannel(() -> channel), Optional.empty(), defaultCurrentContext));
	}
	
}

package com.cexj.clapp.builder;

import com.cexj.clapp.context.ClappContext;
import com.cexj.clapp.utils.DefaultCurrent;

public final class Clapp {

	private final ClappContext globalContext;
	
	private Clapp(final ClappContext globalContext){
		this.globalContext = globalContext;
	}
	
	public static Clapp load(final ClappContext clappContext){
		return new Clapp(clappContext);
	}
	
	public <R> Type_GlobalContext<R> returnType(final Class<R> clazz) {
		return Type_GlobalContext.of(Type.of(DefaultCurrent.fromDefault(globalContext)));
	}
}

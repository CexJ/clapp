package com.cexj.clapp.utils;

import java.util.concurrent.ExecutionException;

public interface FunctionFromFuture<T, R> {

	R apply(final T t) throws InterruptedException, ExecutionException;

	public static <T> FunctionFromFuture<T, T> identity(){
		return new FunctionFromFuture<>() {

			@Override
			public T apply(T t) throws InterruptedException, ExecutionException {
				return t;
			}
		};
	}
}

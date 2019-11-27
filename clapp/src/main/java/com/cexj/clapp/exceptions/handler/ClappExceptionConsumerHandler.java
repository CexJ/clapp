package com.cexj.clapp.exceptions.handler;

public interface ClappExceptionConsumerHandler<T extends Exception> {

	void handle(T t);
}

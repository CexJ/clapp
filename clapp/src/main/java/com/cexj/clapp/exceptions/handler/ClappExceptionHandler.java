package com.cexj.clapp.exceptions.handler;

import com.cexj.clapp.exceptions.runtime.ClappRuntimeException;

public interface ClappExceptionHandler<T extends Exception, R extends ClappRuntimeException> {

	R handle(T t);
}

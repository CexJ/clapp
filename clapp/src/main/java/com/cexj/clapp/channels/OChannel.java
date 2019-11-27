package com.cexj.clapp.channels;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.cexj.clapp.exceptions.handler.ClappExceptionConsumerHandler;
import com.cexj.clapp.exceptions.handler.ClappExceptionRethrowHandler;
import com.cexj.clapp.exceptions.runtime.ClappRuntimeException;

public interface OChannel<O> extends AutoCloseable {

	public void push(final O o);
	
	public default void pushAndClose(final O o, final ClappExceptionConsumerHandler<Exception> handlerCloseException){
		push(o);
		try {
			close();
		} catch (Exception e) {
			handlerCloseException.handle(e);
		}
	}
	
	public static <O> OChannel<O> fromConsumer(final Consumer<O> consumer){
		return new OChannel<O>() {

			@Override
			public void close() throws Exception {
			}

			@Override
			public void push(O o) {
				consumer.accept(o);
			}

			@Override
			public void pushAndClose(final O o, final ClappExceptionConsumerHandler<Exception> handler) {
				consumer.accept(o);
			}
			

			
		};
		
	}
	
	public static <O> OChannel<Future<O>> inParallel(final OChannel<O> channel, final ExecutorService executor, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler){
		return new OChannel<Future<O>>() {

			@Override
			public void close() throws Exception {
				channel.close();
			}

			@Override
			public void push(Future<O> o){
				executor.submit(() -> {
					try {
						channel.push(o.get());
					} catch (InterruptedException | ExecutionException ex) {
						throw handler.handle(ex);
					}
				});
			}

			@Override
			public void pushAndClose(final Future<O> o, final ClappExceptionConsumerHandler<Exception> handlerCloseException) {
				executor.submit(() -> {
					try {
						channel.push(o.get());
						channel.close();
					} catch (InterruptedException | ExecutionException ex) {
						throw handler.handle(ex);
					} catch (Exception ex) {
						handlerCloseException.handle(ex);
					}
				});
				
			}
			
		};
	}

	public default OChannel<O> pipe(OChannel<O> oChannel){
		OChannel<O> original = this;
		return new OChannel<O>() {

			@Override
			public void close() throws Exception {
				try {
					oChannel.close();
				} finally {
					original.close();
				}
			}

			@Override
			public void push(O o) {
				try {
					original.push(o);
				} finally {
					oChannel.push(o);
				}
			}
		
		};
	}
}

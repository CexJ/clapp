package com.cexj.clapp.channels;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.cexj.clapp.exceptions.handler.ClappExceptionRethrowHandler;
import com.cexj.clapp.exceptions.runtime.ClappRuntimeException;

public interface OChannel_Opened<O> {

	public void push(final O o);
	public void close();

	
	public default void pushAndClose(final O o){
		push(o);
		close();
	}
	
	public static <O> OChannel_Opened<O> fromConsumer(final Consumer<O> consumer){
		return new OChannel_Opened<O>() {

			@Override
			public void close(){
			}

			@Override
			public void push(O o) {
				consumer.accept(o);
			}

			@Override
			public void pushAndClose(final O o) {
				consumer.accept(o);
			}
			

			
		};
		
	}
	
	public static <O> OChannel_Opened<Future<O>> inParallel(final OChannel_Opened<O> channel, final ExecutorService executor, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler, final CompletableFuture<Optional<O>> closeTrigger){
		return new OChannel_Opened<Future<O>>() {

			@Override
			public void close(){
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
			public void pushAndClose(final Future<O> futureValue) {
				executor.submit(() -> {
					Optional<O> optValue = Optional.empty();
					try {
						var value = futureValue.get();
						channel.push(value);
						optValue = Optional.of(value);
						channel.close();
					} catch (InterruptedException | ExecutionException ex) {
						throw handler.handle(ex);
					}finally {
						closeTrigger.complete(optValue);
					}
				});
				
			}
			
		};
	}

	public default OChannel_Opened<O> pipe(OChannel_Opened<O> oChannel){
		OChannel_Opened<O> original = this;
		return new OChannel_Opened<O>() {

			@Override
			public void close(){
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

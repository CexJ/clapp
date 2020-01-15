package com.cexj.clapp.channels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface OChannel_Opened<O> {

	public Future<List<Exception>> push(final O o);
	public List<Exception> close();

	
	public default Future<List<Exception>> pushAndClose(final O o){
		var result = push(o);
		close();
		return result;
	}
	
	public static <O> OChannel_Opened<O> fromConsumer(final Consumer<O> consumer){
		return new OChannel_Opened<O>() {

			@Override
			public List<Exception> close(){
				return Collections.emptyList();
			}

			@Override
			public Future<List<Exception>> push(O o) {
				consumer.accept(o);
				var result = new CompletableFuture<List<Exception>>();
				result.complete(Collections.emptyList());
				return result;
			}
			
		};
		
	}
	
	public static <O> OChannel_Opened<Future<O>> inParallel(final OChannel_Opened<O> channel, final ExecutorService executor, final CompletableFuture<Optional<O>> closeTrigger){
		return new OChannel_Opened<Future<O>>() {

			@Override
			public List<Exception> close(){
				return channel.close();
			}

			@Override
			public Future<List<Exception>> push(Future<O> o){
				return executor.submit(() -> {
					try {
						channel.push(o.get());
						return Collections.emptyList();
					} catch (InterruptedException | ExecutionException ex) {
						return Collections.singletonList(ex);
					}
				});
			}

			@Override
			public Future<List<Exception>> pushAndClose(final Future<O> futureValue) {
				return executor.submit(() -> {
					Optional<O> optValue = Optional.empty();
					try {
						var value = futureValue.get();
						channel.push(value);
						optValue = Optional.of(value);
						channel.close();
						return Collections.emptyList();
					} catch (InterruptedException | ExecutionException ex) {
						return Collections.singletonList(ex);
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
			public List<Exception> close(){
				var result = new ArrayList<>(original.close());
				result.addAll(oChannel.close());
				return result;
			}

			@Override
			public Future<List<Exception>> push(O o) {
				var listExp = new ArrayList<>(extractExceptions(original, o));
				listExp.addAll(extractExceptions(oChannel, o));
				var result = new CompletableFuture<List<Exception>>();
				result.complete(listExp);
				return result;
			}

			private List<Exception> extractExceptions(OChannel_Opened<O> original, O o) {
				List<Exception> originalExp;
				try {
					originalExp = original.push(o).get();
				} catch (InterruptedException | ExecutionException ex) {
					originalExp = Collections.singletonList(ex);
				}
				return originalExp;
			}
		
		};
	}
}

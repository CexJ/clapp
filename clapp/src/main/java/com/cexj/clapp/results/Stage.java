package com.cexj.clapp.results;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Stage<T> {
	
	private final PullResult<T> pulledValue;
	private final CompletableFuture<List<Exception>> closingIChannelExceptions;
	
	private Stage(PullResult<T> pulledValue, CompletableFuture<List<Exception>> closingIChannelExceptions) {
		this.pulledValue = pulledValue;
		this.closingIChannelExceptions = closingIChannelExceptions;
	}

	public static <T> Stage<T> of(PullResult<T> pulledValue, CompletableFuture<List<Exception>> closingIChannelExceptions) {
		return new Stage<>(pulledValue, closingIChannelExceptions);
	}

	public PullResult<T> getPulledValue() {
		return pulledValue;
	}

	public CompletableFuture<List<Exception>> getClosingIChannelExceptions() {
		return closingIChannelExceptions;
	}
	
	
}

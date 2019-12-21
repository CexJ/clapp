package com.cexj.clapp.results;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

public class Stage<T> {
	
	private final PullResult<T> pulledValue;
	private final Future<List<Exception>> closingIChannelExceptions;
	private final Optional<Future<List<Exception>>> pushingOChannelExceptions;
	
	private Stage(PullResult<T> pulledValue, Future<List<Exception>> closingIChannelExceptions, Optional<Future<List<Exception>>> pushingOChannelExceptions) {
		this.pulledValue = pulledValue;
		this.closingIChannelExceptions = closingIChannelExceptions;
		this.pushingOChannelExceptions = pushingOChannelExceptions;
		
	}

	public static <T> Stage<T> of(PullResult<T> pulledValue, Future<List<Exception>> closingIChannelExceptions, Optional<Future<List<Exception>>> pushingOChannelExceptions) {
		return new Stage<>(pulledValue, closingIChannelExceptions, pushingOChannelExceptions);
	}

	public PullResult<T> getPulledValue() {
		return pulledValue;
	}

	public Future<List<Exception>> getClosingIChannelExceptions() {
		return closingIChannelExceptions;
	}

	public Optional<Future<List<Exception>>> getPushingOChannelExceptions() {
		return pushingOChannelExceptions;
	}
	
	
	
}

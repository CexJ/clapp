package com.cexj.clapp.context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cexj.clapp.exceptions.handler.ClappExceptionHandler;
import com.cexj.clapp.exceptions.runtime.ChannelClappRuntimeException;
import com.cexj.clapp.exceptions.runtime.ClappRuntimeException;
import com.cexj.clapp.exceptions.runtime.FutureClappRuntimeException;

public final class ClappContext {
	
	public static final ClappContext DEFAULT_CONTEXT = ClappContextBuilder.defaultClapContext();
	
	private final ExecutorService executor;
	private final ClappExceptionHandler<Exception,ClappRuntimeException> futureExceptionHandler;
	private final ClappExceptionHandler<Exception,ClappRuntimeException> channelExceptionHandler;
	
	public static final class ClappContextBuilder {
		
		
		private ExecutorService executor;
		private final static ExecutorService defaultExecutor = Executors.newFixedThreadPool(10);
		
		private ClappExceptionHandler<Exception,ClappRuntimeException> futureExceptionHandler;
		private final static ClappExceptionHandler<Exception,ClappRuntimeException> defaultFutureExceptionHandler = ex -> new FutureClappRuntimeException();
		
		private ClappExceptionHandler<Exception,ClappRuntimeException> channelExceptionHandler;
		private final static ClappExceptionHandler<Exception,ClappRuntimeException> defaultChannelExceptionHandler = ex -> new ChannelClappRuntimeException();
		
		private ClappContextBuilder(ClappContext clappContext){
			this.executor = clappContext.executor;
			this.futureExceptionHandler = clappContext.futureExceptionHandler;
			this.channelExceptionHandler = clappContext.channelExceptionHandler;
		}
		
		
		public ClappContextBuilder withExecutor(final ExecutorService executor) {
			this.executor = executor;
			return this;
		}
		
		public ClappContextBuilder withFutureHandler(final ClappExceptionHandler<Exception,ClappRuntimeException> futureExceptionHandler) {
			this.futureExceptionHandler = futureExceptionHandler;
			return this;
		}
		
		public ClappContextBuilder withChannelHandler(final ClappExceptionHandler<Exception,ClappRuntimeException> channelExceptionHandler) {
			this.channelExceptionHandler = channelExceptionHandler;
			return this;
		}
		
		public ClappContext build() {
			return new ClappContext(executor, futureExceptionHandler, channelExceptionHandler);
		}
		
		
		public final static ClappContextBuilder fromDefault(){
			return new ClappContextBuilder(defaultClapContext());
		}
		
		public final static ClappContextBuilder from(ClappContext clappContext){
			return new ClappContextBuilder(clappContext);
		}
		
		public final static ClappContext defaultClapContext(){
			return new ClappContext(defaultExecutor, defaultFutureExceptionHandler, defaultChannelExceptionHandler);
		}
		
	}
	
	private ClappContext(final ExecutorService executor, final ClappExceptionHandler<Exception,ClappRuntimeException> futureExceptionHandler, final ClappExceptionHandler<Exception,ClappRuntimeException> channelExceptionHandler) {
		super();
		this.executor = executor;
		this.futureExceptionHandler = futureExceptionHandler;
		this.channelExceptionHandler = channelExceptionHandler;
	}
	
	
	public ExecutorService getExecutor() {
		return executor;
	}
	

	public ClappExceptionHandler<Exception,ClappRuntimeException> getFutureExceptionHandler() {
		return futureExceptionHandler;
	}
	
	public ClappExceptionHandler<Exception,ClappRuntimeException> getChannelExceptionHandler() {
		return channelExceptionHandler;
	}

}

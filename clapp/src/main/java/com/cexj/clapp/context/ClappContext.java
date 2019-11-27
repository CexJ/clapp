package com.cexj.clapp.context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.cexj.clapp.exceptions.handler.ClappExceptionConsumerHandler;
import com.cexj.clapp.exceptions.handler.ClappExceptionRethrowHandler;
import com.cexj.clapp.exceptions.runtime.ClappRuntimeException;
import com.cexj.clapp.exceptions.runtime.ClosingIChannelClappRuntimeException;
import com.cexj.clapp.exceptions.runtime.FutureClappRuntimeException;

public final class ClappContext {
	
	public static final ClappContext DEFAULT_CONTEXT = ClappContextBuilder.defaultClapContext();
	
	private final ExecutorService executor;
	private final ClappExceptionRethrowHandler<Exception,ClappRuntimeException> futureExceptionHandler;
	private final ClappExceptionRethrowHandler<Exception,ClappRuntimeException> closingIChannelExceptionHandler;
	private final ClappExceptionConsumerHandler<Exception> closingOChannelExceptionHandler;
	
	public static final class ClappContextBuilder {
		
		
		private ExecutorService executor;
		private final static ExecutorService defaultExecutor = Executors.newFixedThreadPool(10);
		
		private ClappExceptionRethrowHandler<Exception,ClappRuntimeException> futureExceptionHandler;
		private final static ClappExceptionRethrowHandler<Exception,ClappRuntimeException> defaultFutureExceptionHandler = ex -> new FutureClappRuntimeException();
		
		
		private ClappExceptionRethrowHandler<Exception,ClappRuntimeException> closingIChannelExceptionHandler;
		private final static ClappExceptionRethrowHandler<Exception,ClappRuntimeException> defaultClosingIChannelExceptionHandler = ex -> new ClosingIChannelClappRuntimeException();
		
		private ClappExceptionConsumerHandler<Exception> closingOChannelExceptionHandler;
		private final static ClappExceptionConsumerHandler<Exception> defaultClosingOChannelExceptionHandler = ex -> {return;};
		
		private ClappContextBuilder(ClappContext clappContext){
			this.executor = clappContext.executor;
			this.futureExceptionHandler = clappContext.futureExceptionHandler;
			this.closingIChannelExceptionHandler = clappContext.closingIChannelExceptionHandler;
			this.closingOChannelExceptionHandler = clappContext.closingOChannelExceptionHandler;
		}
		
		
		public ClappContextBuilder withExecutor(final ExecutorService executor) {
			this.executor = executor;
			return this;
		}
		
		public ClappContextBuilder withFutureHandler(final ClappExceptionRethrowHandler<Exception,ClappRuntimeException> futureExceptionHandler) {
			this.futureExceptionHandler = futureExceptionHandler;
			return this;
		}
		
		public ClappContextBuilder withClosingIChannelExceptionHandler(final ClappExceptionRethrowHandler<Exception,ClappRuntimeException> closingIChannelExceptionHandler) {
			this.closingIChannelExceptionHandler = closingIChannelExceptionHandler;
			return this;
		}
		
		public ClappContextBuilder withClosingOChannelExceptionHandler(final ClappExceptionConsumerHandler<Exception> closingOChannelExceptionHandler) {
			this.closingOChannelExceptionHandler = closingOChannelExceptionHandler;
			return this;
		}
		
		
		public ClappContext build() {
			return new ClappContext(
					executor, 
					futureExceptionHandler, 
					closingIChannelExceptionHandler, 
					closingOChannelExceptionHandler);
		}
		
		
		public final static ClappContextBuilder fromDefault(){
			return new ClappContextBuilder(defaultClapContext());
		}
		
		public final static ClappContextBuilder from(ClappContext clappContext){
			return new ClappContextBuilder(clappContext);
		}
		
		public final static ClappContext defaultClapContext(){
			return new ClappContext(
					defaultExecutor, 
					defaultFutureExceptionHandler, 
					defaultClosingIChannelExceptionHandler, 
					defaultClosingOChannelExceptionHandler);
		}
		
	}
	
	private ClappContext(final ExecutorService executor, 
			final ClappExceptionRethrowHandler<Exception,ClappRuntimeException> futureExceptionHandler, 
			final ClappExceptionRethrowHandler<Exception,ClappRuntimeException> closingIChannelExceptionHandler,
			final ClappExceptionConsumerHandler<Exception> closingOChannelExceptionHandler) {
		super();
		this.executor = executor;
		this.futureExceptionHandler = futureExceptionHandler;
		this.closingIChannelExceptionHandler = closingIChannelExceptionHandler;
		this.closingOChannelExceptionHandler = closingOChannelExceptionHandler;
	}
	
	
	public ExecutorService getExecutor() {
		return executor;
	}
	

	public ClappExceptionRethrowHandler<Exception,ClappRuntimeException> getFutureExceptionHandler() {
		return futureExceptionHandler;
	}
	

	public ClappExceptionConsumerHandler<Exception> getClosingOChannelExceptionHandler() {
		return closingOChannelExceptionHandler;
	}


	public ClappExceptionRethrowHandler<Exception,ClappRuntimeException> getClosingIChannelExceptionHandler() {
		return closingIChannelExceptionHandler;
	}

}

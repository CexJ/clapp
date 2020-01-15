package com.cexj.clapp.context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class ClappContext {
	
	public static final ClappContext DEFAULT_CONTEXT = ClappContextBuilder.defaultClapContext();
	
	private final ExecutorService iExecutor;
	private final ExecutorService closingIExecutor;
	private final ExecutorService oExecutor;
	
	public static final class ClappContextBuilder {
		
		
		private ExecutorService iExecutor;
		private final static ExecutorService defaultIExecutor = Executors.newFixedThreadPool(10);
		
		private ExecutorService closingIExecutor;
		private final static ExecutorService defaultClosingIExecutor = Executors.newFixedThreadPool(10);
		
		
		private ExecutorService oExecutor;
		private final static ExecutorService defaultOExecutor = Executors.newFixedThreadPool(10);
		
		
		private ClappContextBuilder(ClappContext clappContext){
			this.iExecutor = clappContext.iExecutor;
			this.closingIExecutor = clappContext.closingIExecutor;
			this.oExecutor = clappContext.oExecutor;
		}
		
		
		public ClappContextBuilder withIExecutor(final ExecutorService iExecutor) {
			this.iExecutor = iExecutor;
			return this;
		}
		
		public ClappContextBuilder withClosingIExecutor(final ExecutorService closingIExecutor) {
			this.closingIExecutor = closingIExecutor;
			return this;
		}
		
		public ClappContextBuilder withOExecutor(final ExecutorService oExecutor) {
			this.oExecutor = oExecutor;
			return this;
		}
		
		public ClappContext build() {
			return new ClappContext(
					iExecutor, 
					closingIExecutor,
					oExecutor);
		}
		
		
		public final static ClappContextBuilder fromDefault(){
			return new ClappContextBuilder(defaultClapContext());
		}
		
		public final static ClappContextBuilder from(ClappContext clappContext){
			return new ClappContextBuilder(clappContext);
		}
		
		public final static ClappContext defaultClapContext(){
			return new ClappContext(
					defaultIExecutor, 
					defaultClosingIExecutor,
					defaultOExecutor);
		}
		
	}
	
	private ClappContext(
			final ExecutorService iExecutor, 
			final ExecutorService closingIExecutor, 
			final ExecutorService oExecutor) { 
		super();
		this.closingIExecutor = closingIExecutor;
		this.iExecutor = iExecutor;
		this.oExecutor = oExecutor;
	}
	
	
	public ExecutorService getIExecutor() {
		return iExecutor;
	}
	
	
	public ExecutorService getClosingIExecutor() {
		return closingIExecutor;
	}


	public ExecutorService getOExecutor() {
		return oExecutor;
	}
}

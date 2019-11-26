package com.cexj.clapp.utils;

public final class DefaultCurrent<D,C> {

	private final D defaultValue;
	private final C currentValue;
	
	private DefaultCurrent(D defaultValue, C currentValue) {
		super();
		this.defaultValue = defaultValue;
		this.currentValue = currentValue;
	}
	
	public static <D,C> DefaultCurrent<D,C> of(D defaultValue, C currentValue){
		return new DefaultCurrent<>(defaultValue, currentValue);
	}
	
	public static <D> DefaultCurrent<D,D> fromDefault(D defaultValue){
		return DefaultCurrent.of(defaultValue, defaultValue);
	}
	
	public <N> DefaultCurrent<D,N> withNewCurrent(N newCurrentValue){
		return DefaultCurrent.of(defaultValue, newCurrentValue);
	}
	
	public DefaultCurrent<D,D> withDefault(){
		return DefaultCurrent.of(defaultValue, defaultValue);
	}
	
	public D getDefaultValue() {
		return defaultValue;
	}
	public C getCurrentValue() {
		return currentValue;
	}
	
	
}

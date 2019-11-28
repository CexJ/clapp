package com.cexj.clapp.utils;

import java.util.function.Function;

public final class Tuple<T,R> {

	private final T firstValue;
	private final R secondValue;
	private Tuple(final T firstValue, final R secondValue) {
		super();
		this.firstValue = firstValue;
		this.secondValue = secondValue;
	}
	
	public static <T,R> Tuple<T,R> of(final T firstValue, final R secondValue) {
		return new Tuple<>(firstValue, secondValue);
	}
	
	public <U> Tuple<U,R> mapFirst(Function<? super T, ? extends U> f){
		return map(f,Function.identity());
	}
	
	public <V> Tuple<T,V> mapSecond(Function<? super R, ? extends V> f){
		return map(Function.identity(),f);
	}
	
	public <U,V> Tuple<U,V> map(Function<? super T, ? extends U> f1, Function<? super R, ? extends V> f2){
		return new Tuple<>(f1.apply(firstValue), f2.apply(secondValue));
	}
	
	public T getFirst() {
		return firstValue;
	}
	public R getSecond() {
		return secondValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstValue == null) ? 0 : firstValue.hashCode());
		result = prime * result + ((secondValue == null) ? 0 : secondValue.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	public boolean firstEquals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		if (firstValue == null) {
			if (other.firstValue != null)
				return false;
		} else if (!firstValue.equals(other.firstValue))
			return false;
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean secondEquals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		if (secondValue == null) {
			if (other.secondValue != null)
				return false;
		} else if (!secondValue.equals(other.secondValue))
			return false;
		return true;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple other = (Tuple) obj;
		if (firstValue == null) {
			if (other.firstValue != null)
				return false;
		} else if (!firstValue.equals(other.firstValue))
			return false;
		if (secondValue == null) {
			if (other.secondValue != null)
				return false;
		} else if (!secondValue.equals(other.secondValue))
			return false;
		return true;
	}
	
	
}

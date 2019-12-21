package com.cexj.clapp.utils;

public final class Product<L,R> {

	private final L leftValue;
	private final R rightValue;
	
	private Product(L leftValue, R rightValue) {
		this.leftValue = leftValue;
		this.rightValue = rightValue;
	}

	public static <L,R> Product<L,R> of(L leftValue, R rightValue) {
		return new Product<>(leftValue, rightValue);
	}
	
	public L getLeft() {
		return leftValue;
	}

	public R getRight() {
		return rightValue;
	}
	
	
}

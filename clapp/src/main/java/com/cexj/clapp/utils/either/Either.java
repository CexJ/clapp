package com.cexj.clapp.utils.either;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Either<L,R> {

	public enum Type {
		LEFT, RIGHT
	}

	public static <L,R> Either<L, R> left(L value){
		return Left.of(value);
	}
	
	public static <L,R> Either<L, R> right(R value){
		return Right.of(value);
	}
	
	
	boolean isLeft();
	boolean isRight();
	void ifLeft(Consumer<L> consumer);
	void ifRight(Consumer<R> consumer);
	L getLeft();
	R getRight();
	<U> Either<L,U> map(Function<? super R, ? extends U> f);
	<U> Either<L,U> flatMap(Function<? super R, ? extends Either<L,U>> f);
	Type getType();
	
}

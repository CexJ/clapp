package com.cexj.clapp.utils.either;

import java.util.function.Consumer;
import java.util.function.Function;

final class Left<L,R> implements Either<L, R> {

	private final L value;
	
	private Left(L value) {
		super();
		this.value = value;
	}
	
	public static <L,R> Left<L,R> of(L value) {
		return new Left<>(value);
	}

	@Override
	public boolean isLeft() {
		return true;
	}

	@Override
	public boolean isRight() {
		return false;
	}

	@Override
	public void ifLeft(Consumer<L> consumer) {
		consumer.accept(value);
	}

	@Override
	public void ifRight(Consumer<R> consumer) {
	}

	@Override
	public <U> Either<L, U> map(Function<? super R, ? extends U> f) {
		return Left.of(value);
	}

	@Override
	public <U> Either<L, U> flatMap(Function<? super R, ? extends Either<L, U>> f) {
		return Left.of(value);
	}

	@Override
	public Type getType() {
		return Type.LEFT;
	}

	@Override
	public L getLeft() {
		return value;
	}

	@Override
	public R getRight() {
		throw new NullPointerException();
	}

}

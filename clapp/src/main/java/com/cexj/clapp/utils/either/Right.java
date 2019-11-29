package com.cexj.clapp.utils.either;

import java.util.function.Consumer;
import java.util.function.Function;

final class Right<L,R> implements Either<L, R> {

	private final R value;
	
	private Right(R value) {
		super();
		this.value = value;
	}
	
	public static <L,R> Right<L,R> of(R value) {
		return new Right<>(value);
	}
	
	@Override
	public boolean isLeft() {
		return false;
	}

	@Override
	public boolean isRight() {
		return true;
	}

	@Override
	public void ifLeft(Consumer<L> consumer) {}

	@Override
	public void ifRight(Consumer<R> consumer) {
		consumer.accept(value);
	}

	@Override
	public <U> Either<L, U> map(Function<? super R, ? extends U> f) {
		return Right.of(f.apply(value));
	}

	@SuppressWarnings("preview")
	@Override
	public <U> Either<L, U> flatMap(Function<? super R, ? extends Either<L, U>> f) {
		var nestedEither = f.apply(value);
		return switch (nestedEither.getType()) {
			case LEFT -> Left.of(nestedEither.getLeft());
			case RIGHT -> Right.of(nestedEither.getRight());
		};
	}
	
	@Override
	public Type getType() {
		return Type.RIGHT;
	}

	@Override
	public L getLeft() {
		throw new NullPointerException();
	}

	@Override
	public R getRight() {
		return value;
	}

}

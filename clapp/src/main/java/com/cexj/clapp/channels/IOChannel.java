package com.cexj.clapp.channels;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import com.cexj.clapp.exceptions.handler.ClappExceptionRethrowHandler;
import com.cexj.clapp.exceptions.runtime.ClappRuntimeException;

public class IOChannel<T> {

	private final Supplier<IChannel<T>> iChannel;
	private final Optional<Supplier<OChannel<T>>> oChannel;
	
	private IOChannel(final Supplier<IChannel<T>> iChannel, final Optional<Supplier<OChannel<T>>> oChannel) {
		this.iChannel = iChannel;
		this.oChannel = oChannel;
	}
	
	public static <T> IOChannel<T> of(final Supplier<IChannel<T>> iChannel, final Optional<Supplier<OChannel<T>>> oChannel) {
		return new IOChannel<>(iChannel, oChannel);
	}
	

	
	public static <T> IOChannel<T> fromIChannel(final Supplier<IChannel<T>> iChannel) {
		return IOChannel.of(iChannel, Optional.empty());
	}
	

	public IOChannel<T> addOChannel(final Supplier<OChannel<T>> sup) {
		Optional<Supplier<OChannel<T>>> opt = this.oChannel.map(s -> () -> sup.get().pipe(s.get()));
		Optional<Supplier<OChannel<T>>> newOChannel = Optional.of(opt.orElse(sup));
		return IOChannel.of(iChannel, newOChannel);
	}
	
	
	
	public IOChannel<Future<T>> inParallel(final ExecutorService executor, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler) {
		Supplier<IChannel<Future<T>>> newIChannel = () -> IChannel.inParallel(iChannel.get(), executor);
		Optional<Supplier<OChannel<Future<T>>>> newOChannel = oChannel.map(o -> () -> OChannel.inParallel(o.get(), executor, handler));
		return IOChannel.of(newIChannel, newOChannel);
	}

	public IChannel<T> getIChannel() {
		return iChannel.get();
	}

	public Optional<OChannel<T>> getOChannel() {
		return oChannel.map(Supplier::get);
	}

}

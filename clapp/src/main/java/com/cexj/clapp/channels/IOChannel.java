package com.cexj.clapp.channels;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.cexj.clapp.exceptions.handler.ClappExceptionRethrowHandler;
import com.cexj.clapp.exceptions.runtime.ClappRuntimeException;

public class IOChannel<T> {

	private final IChannel<T> iChannel;
	private final Optional<OChannel<T>> oChannel;
	
	private IOChannel(final IChannel<T> iChannel, final Optional<OChannel<T>> oChannel) {
		this.iChannel = iChannel;
		this.oChannel = oChannel;
	}
	
	public static <T> IOChannel<T> of(final IChannel<T> iChannel, final Optional<OChannel<T>> oChannel) {
		return new IOChannel<>(iChannel, oChannel);
	}
	

	
	public static <T> IOChannel<T> fromIChannel(final IChannel<T> iChannel) {
		return IOChannel.of(iChannel, Optional.empty());
	}
	

	public IOChannel<T> addOChannel(final OChannel<T> sup) {
		Optional<OChannel<T>> opt = this.oChannel.map(s -> () -> sup.open().pipe(s.open()));
		Optional<OChannel<T>> newOChannel = Optional.of(opt.orElse(sup));
		return IOChannel.of(iChannel, newOChannel);
	}
	
	public IOChannel<T> addIChannel(IChannel<T> channel, final ExecutorService executor, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler) {
		IChannel<T> newIChannel = () -> iChannel.open().pipe(channel.open(), executor, handler);
		return IOChannel.of(newIChannel, oChannel);
	}
	
	public IOChannel<Future<T>> inParallel(final ExecutorService iExecutor, final ExecutorService oExecutor, final ClappExceptionRethrowHandler<Exception, ClappRuntimeException> handler) {
		var trigger = oChannel.map(o -> new CompletableFuture<Optional<T>>()); 
		Optional<OChannel<Future<T>>> newOChannel = oChannel.map(o -> () -> OChannel_Opened.inParallel(o.open(), oExecutor, handler, trigger.get()));
		IChannel<Future<T>> newIChannel = () -> IChannel_Opened.inParallel(iChannel.open(), iExecutor, trigger);
		return IOChannel.of(newIChannel, newOChannel);
	}

	public IChannel_Opened<T> openIChannel() {
		return iChannel.open();
	}

	public Optional<OChannel_Opened<T>> openOChannel() {
		return oChannel.map(OChannel::open);
	}

	

}

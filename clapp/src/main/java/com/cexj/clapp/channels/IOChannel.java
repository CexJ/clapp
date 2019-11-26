package com.cexj.clapp.channels;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.cexj.clapp.exceptions.handler.ClappExceptionHandler;
import com.cexj.clapp.exceptions.runtime.ClappRuntimeException;

public class IOChannel<T> implements IChannel<T>, OChannel<T> {

	private final IChannel<T> iChannel;
	private final OChannel<T> oChannel;
	
	private IOChannel(final IChannel<T> iChannel, final OChannel<T> oChannel) {
		this.iChannel = iChannel;
		this.oChannel = oChannel;
	}
	
	public static <T> IOChannel<T> of(final IChannel<T> iChannel, final OChannel<T> oChannel) {
		return new IOChannel<>(iChannel, oChannel);
	}
	
	public T pullAndPush() {
		var t = iChannel.pull();
		oChannel.push(t);
		return t;
	}
	
	@Override
	public void close() throws Exception {
		try {
			oChannel.close();
		} finally {
			iChannel.close();
		}
	}

	
	public static <T> IOChannel<T> fromIChannel(final IChannel<T> iChannel) {
		return IOChannel.of(iChannel, OChannel.empty());
	}
	

	public IOChannel<T> addOChannel(final OChannel<T> oChannel) {
		return IOChannel.of(iChannel, this.oChannel.pipe(oChannel));
	}
	
	public IOChannel<Future<T>> inParallel(final ExecutorService executor, final ClappExceptionHandler<Exception, ClappRuntimeException> handler) {
		return IOChannel.of(IChannel.inParallel(iChannel, executor), OChannel.inParallel(oChannel, executor, handler));
	}

	@Override
	public void push(T t) {
		oChannel.push(t);
	}

	@Override
	public T pull() {
		return iChannel.pull();
	}


}

package com.cexj.clapp.channels;

public interface IChannel<I> {

	IChannel_Opened<I> open();
	
}

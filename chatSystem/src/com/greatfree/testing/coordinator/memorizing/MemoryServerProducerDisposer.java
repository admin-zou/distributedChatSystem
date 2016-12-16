package com.greatfree.testing.coordinator.memorizing;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the memory server message producer of the coordinator. 11/28/2014, Bing Li
 */

// Created: 11/28/2014, Bing Li
public class MemoryServerProducerDisposer implements RunDisposable<MessageProducer<MemoryServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MemoryServerDispatcher> r) throws InterruptedException
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<MemoryServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose();
	}
}

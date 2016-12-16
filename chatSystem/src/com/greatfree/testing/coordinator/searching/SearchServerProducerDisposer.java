package com.greatfree.testing.coordinator.searching;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the searchers' message producer of the coordinator. 11/29/2014, Bing Li
 */

// Created: 11/29/2014, Bing Li
public class SearchServerProducerDisposer implements RunDisposable<MessageProducer<SearchServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<SearchServerDispatcher> r) throws InterruptedException
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/29/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<SearchServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose();
	}
}

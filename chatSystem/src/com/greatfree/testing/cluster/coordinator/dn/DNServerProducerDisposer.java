package com.greatfree.testing.cluster.coordinator.dn;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the DN server message producer of the coordinator. 11/28/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class DNServerProducerDisposer implements RunDisposable<MessageProducer<DNServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<DNServerDispatcher> r) throws InterruptedException
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/28/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<DNServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose();
	}

}

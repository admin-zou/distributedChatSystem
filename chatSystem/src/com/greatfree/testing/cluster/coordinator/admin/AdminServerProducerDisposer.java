package com.greatfree.testing.cluster.coordinator.admin;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the administration message producer of the coordinator. 11/27/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class AdminServerProducerDisposer implements RunDisposable<MessageProducer<AdminServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/27/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<AdminServerDispatcher> r) throws InterruptedException
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/27/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<AdminServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose();
	}

}

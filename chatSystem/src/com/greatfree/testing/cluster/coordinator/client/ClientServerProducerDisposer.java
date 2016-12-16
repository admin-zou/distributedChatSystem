package com.greatfree.testing.cluster.coordinator.client;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the client message producer of the coordinator. 11/24/2014, Bing Li
 */

// Created: 11/19/2016, Bing Li
public class ClientServerProducerDisposer implements RunDisposable<MessageProducer<ClientServerDispatcher>>
{

	/*
	 * Dispose the message producer. 11/24/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<ClientServerDispatcher> r) throws InterruptedException
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/24/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<ClientServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose();
	}

}

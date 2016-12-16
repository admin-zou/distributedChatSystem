package com.greatfree.testing.client;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.reuse.RunDisposable;

/*
 * This is the disposer to dispose the instance of ClientServerDispatcher. It is usually executed when the client is shutdown. 11/07/2014, Bing Li
 */

// Created: 11/07/2014, Bing Li
public class ClientServerDispatcherDisposer implements RunDisposable<MessageProducer<ClientServerDispatcher>>
{
	/*
	 * Dispose the message producer. 11/07/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<ClientServerDispatcher> r) throws InterruptedException
	{
		r.dispose();
	}

	/*
	 * The method does not make sense to the class of MessageProducer. Just leave it here. 11/07/2014, Bing Li
	 */
	@Override
	public void dispose(MessageProducer<ClientServerDispatcher> r, long time) throws InterruptedException
	{
		r.dispose();
	}
}

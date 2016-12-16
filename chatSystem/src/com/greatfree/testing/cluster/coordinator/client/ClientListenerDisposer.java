package com.greatfree.testing.cluster.coordinator.client;

import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the instance of CrawlListener by invoking its method of shutdown(). 11/25/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientListenerDisposer implements RunDisposable<ClientListener>
{

	@Override
	public void dispose(ClientListener r)
	{
		r.shutdown();
	}

	@Override
	public void dispose(ClientListener r, long time)
	{
		r.shutdown();
	}

}

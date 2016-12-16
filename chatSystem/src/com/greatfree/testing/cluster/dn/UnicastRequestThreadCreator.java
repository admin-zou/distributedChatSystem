package com.greatfree.testing.cluster.dn;

import com.greatfree.concurrency.BoundBroadcastRequestThreadCreatable;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.remote.IPPort;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.message.UnicastRequest;
import com.greatfree.testing.message.UnicastResponse;

/*
 * The creator is responsible for initializing an instance of UnicastRequestThread. It works with the BoundBroadcastRequestDispatcher to manage the searching process efficiently and concurrently. 11/29/2014, Bing Li
 */

// Created: 11/26/2016, Bing Li
public class UnicastRequestThreadCreator implements BoundBroadcastRequestThreadCreatable<UnicastRequest, UnicastResponse, MulticastMessageDisposer<UnicastRequest>, UnicastRequestThread>
{

	@Override
	public UnicastRequestThread createRequestThreadInstance(IPPort ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, MulticastMessageDisposer<UnicastRequest> reqBinder)
	{
		return new UnicastRequestThread(ipPort, pool, taskSize, dispatcherKey, reqBinder);
	}

}

package com.greatfree.testing.cluster.dn;

import com.greatfree.concurrency.BoundBroadcastRequestThreadCreatable;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.remote.IPPort;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.message.DNBroadcastRequest;
import com.greatfree.testing.message.DNBroadcastResponse;

/*
 * The creator is responsible for initializing an instance of BroadcastRequestThread. It works with the BoundBroadcastRequestDispatcher to manage the searching process efficiently and concurrently. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class BroadcastRequestThreadCreator implements BoundBroadcastRequestThreadCreatable<DNBroadcastRequest, DNBroadcastResponse, MulticastMessageDisposer<DNBroadcastRequest>, BroadcastRequestThread>
{

	@Override
	public BroadcastRequestThread createRequestThreadInstance(IPPort ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, MulticastMessageDisposer<DNBroadcastRequest> reqBinder)
	{
		return new BroadcastRequestThread(ipPort, pool, taskSize, dispatcherKey, reqBinder);
	}

}

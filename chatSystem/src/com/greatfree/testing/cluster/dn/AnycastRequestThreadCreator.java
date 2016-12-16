package com.greatfree.testing.cluster.dn;

import com.greatfree.concurrency.BoundBroadcastRequestThreadCreatable;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.remote.IPPort;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.message.DNAnycastRequest;
import com.greatfree.testing.message.DNAnycastResponse;

/*
 * The creator is responsible for initializing an instance of AnycastRequestThread. It works with the BoundBroadcastRequestDispatcher to manage the searching process efficiently and concurrently. 11/29/2014, Bing Li
 */

// Created: 11/27/2016, Bing Li
public class AnycastRequestThreadCreator implements BoundBroadcastRequestThreadCreatable<DNAnycastRequest, DNAnycastResponse, MulticastMessageDisposer<DNAnycastRequest>, AnycastRequestThread>
{

	@Override
	public AnycastRequestThread createRequestThreadInstance(IPPort ipPort, FreeClientPool pool, int taskSize, String dispatcherKey, MulticastMessageDisposer<DNAnycastRequest> reqBinder)
	{
		return new AnycastRequestThread(ipPort, pool, taskSize, dispatcherKey, reqBinder);
	}

}

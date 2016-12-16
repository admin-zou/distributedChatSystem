package com.greatfree.testing.cluster.dn;

import com.greatfree.multicast.ChildMulticastorSource;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.DNBroadcastRequest;

/*
 * The source contains the arguments to initialize a new broadcastor to forward the request of SearchKeywordBroadcastRequest. It is used by the resource pool. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class DNBroadcastRequestChildBroadcastorSource extends ChildMulticastorSource<DNBroadcastRequest, DNBroadcastRequestCreator>
{
	/*
	 * Initialize the source. 11/29/2014, Bing Li
	 */
	public DNBroadcastRequestChildBroadcastorSource(FreeClientPool clientPool, int treeBranchCount, int serverPort, DNBroadcastRequestCreator creator)
	{
		super(clientPool, treeBranchCount, serverPort, creator);
	}
}

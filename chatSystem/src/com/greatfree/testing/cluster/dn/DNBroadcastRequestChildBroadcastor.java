package com.greatfree.testing.cluster.dn;

import com.greatfree.multicast.ChildMulticastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.DNBroadcastRequest;

/*
 * The child broadcastor forwards the instance of DNBroadcastRequest to the local node's children. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class DNBroadcastRequestChildBroadcastor extends ChildMulticastor<DNBroadcastRequest, DNBroadcastRequestCreator>
{

	public DNBroadcastRequestChildBroadcastor(FreeClientPool clientPool, int treeBranchCount, int clusterServerPort, DNBroadcastRequestCreator messageCreator)
	{
		super(clientPool, treeBranchCount, clusterServerPort, messageCreator);
	}

}

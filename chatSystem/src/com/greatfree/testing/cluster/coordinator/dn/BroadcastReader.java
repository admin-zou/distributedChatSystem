package com.greatfree.testing.cluster.coordinator.dn;

import com.greatfree.multicast.RootRequestBroadcastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.coordinator.CoorConfig;
import com.greatfree.testing.message.DNBroadcastRequest;
import com.greatfree.testing.message.DNBroadcastResponse;

/*
 * The reader is derived from the RootRequestBroadcastor. It attempts to retrieve data in the way of broadcast among the cluster nodes. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class BroadcastReader extends RootRequestBroadcastor<String, DNBroadcastRequest, DNBroadcastResponse, BroadcastRequestCreator>
{
	/*
	 * Initialize the broadcastor. 11/29/2014, Bing Li
	 */
	public BroadcastReader(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, BroadcastRequestCreator requestCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, requestCreator, CoorConfig.BROADCAST_REQUEST_WAIT_TIME);
	}

}

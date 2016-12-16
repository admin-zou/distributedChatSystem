package com.greatfree.testing.cluster.coordinator.dn;

import com.greatfree.multicast.RootRequestBroadcastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.coordinator.CoorConfig;
import com.greatfree.testing.message.UnicastRequest;
import com.greatfree.testing.message.UnicastResponse;

/*
 * The reader is derived from the RootRequestBroadcastor. It attempts to retrieve data in the way of unicast among the cluster nodes. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastReader extends RootRequestBroadcastor<String, UnicastRequest, UnicastResponse, UnicastRequestCreator>
{
	/*
	 * Initialize the unicastor. 11/29/2014, Bing Li
	 */
	public UnicastReader(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, UnicastRequestCreator requestCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, requestCreator, CoorConfig.BROADCAST_REQUEST_WAIT_TIME);
	}

}

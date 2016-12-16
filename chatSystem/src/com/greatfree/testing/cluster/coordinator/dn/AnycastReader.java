package com.greatfree.testing.cluster.coordinator.dn;

import com.greatfree.multicast.RootRequestBroadcastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.coordinator.CoorConfig;
import com.greatfree.testing.message.DNAnycastRequest;
import com.greatfree.testing.message.DNAnycastResponse;

/*
 * The reader is derived from the RootRequestBroadcastor. It attempts to retrieve data in the way of anycast among the cluster nodes. 11/29/2014, Bing Li
 */

// Created: 11/27/2016, Bing Li
public class AnycastReader extends RootRequestBroadcastor<String, DNAnycastRequest, DNAnycastResponse, AnycastRequestCreator>
{
	/*
	 * Initialize the anycastor. 11/29/2014, Bing Li
	 */
	public AnycastReader(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, AnycastRequestCreator requestCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, requestCreator, CoorConfig.BROADCAST_REQUEST_WAIT_TIME);
	}

}

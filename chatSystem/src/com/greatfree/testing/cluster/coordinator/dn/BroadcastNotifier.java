package com.greatfree.testing.cluster.coordinator.dn;

import com.greatfree.multicast.RootObjectMulticastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.BroadcastNotification;

/*
 * This is an extending of RootObjectMulticastor to transfer the notification of BroadcastNotification to all of the crawlers. 11/26/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class BroadcastNotifier extends RootObjectMulticastor<String, BroadcastNotification, BroadcastNotificationCreator>
{
	/*
	 * Initialize the multicastor. 11/26/2014, Bing Li
	 */
	public BroadcastNotifier(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, BroadcastNotificationCreator messageCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, messageCreator);
	}

}

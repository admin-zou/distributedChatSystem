package com.greatfree.testing.cluster.coordinator.dn;

import com.greatfree.multicast.RootObjectMulticastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.UnicastNotification;

/*
 * This is an extending of RootObjectMulticastor to transfer the notification of UnicastNotification to all of the DNs. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastNotifier extends RootObjectMulticastor<String, UnicastNotification, UnicastNotificationCreator>
{
	/*
	 * Initialize the unicastor. 11/26/2014, Bing Li
	 */
	public UnicastNotifier(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, UnicastNotificationCreator messageCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, messageCreator);
	}

}

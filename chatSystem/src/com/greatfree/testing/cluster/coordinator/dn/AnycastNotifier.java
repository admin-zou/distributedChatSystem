package com.greatfree.testing.cluster.coordinator.dn;

import com.greatfree.multicast.RootObjectMulticastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.AnycastNotification;

/*
 * This is an extending of RootObjectMulticastor to transfer the notification of AnycastNotification to all of the DNs. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class AnycastNotifier extends RootObjectMulticastor<String, AnycastNotification, AnycastNotificationCreator>
{
	/*
	 * Initialize the anycastor. 11/26/2014, Bing Li
	 */
	public AnycastNotifier(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, AnycastNotificationCreator messageCreator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, messageCreator);
	}

}

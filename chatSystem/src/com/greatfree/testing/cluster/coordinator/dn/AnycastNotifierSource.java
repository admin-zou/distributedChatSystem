package com.greatfree.testing.cluster.coordinator.dn;

import com.greatfree.multicast.RootMulticastorSource;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.AnycastNotification;

/*
 * The class provides the pool with the initial values to create a AnycastNotifier. The sources that are needed to create an instance of RootMulticastor are enclosed in the class. That is required by the pool to create anycastors. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class AnycastNotifierSource extends RootMulticastorSource<String, AnycastNotification, AnycastNotificationCreator>
{

	public AnycastNotifierSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, AnycastNotificationCreator creator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator);
	}
}

package com.greatfree.testing.cluster.coordinator.admin;

import com.greatfree.multicast.RootMulticastorSource;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.StopDNMultiNotification;
import com.greatfree.util.NullObject;

/*
 * The class provides the pool with the initial values to create a StopDNMulticastor. The sources that are needed to create an instance of RootMulticastor are enclosed in the class. That is required by the pool to create multicastors. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNMulticastorSource extends RootMulticastorSource<NullObject, StopDNMultiNotification, StopDNNotificationCreator>
{
	/*
	 * Initialize the source. 11/27/2014, Bing Li
	 */
	public StopDNMulticastorSource(FreeClientPool clientPool, int rootBranchCount, int treeBranchCount, StopDNNotificationCreator creator)
	{
		super(clientPool, rootBranchCount, treeBranchCount, creator);
	}
}

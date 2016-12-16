package com.greatfree.testing.cluster.dn;

import com.greatfree.multicast.ChildMulticastorSource;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.StopDNMultiNotification;

/*
 * The sources that are needed to create an instance of ChildMulticastor are enclosed in the class. That is required by the pool to create children multicastors. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNNotificationMulticastorSource extends ChildMulticastorSource<StopDNMultiNotification, StopDNMultiNotificationCreator>
{
	/*
	 * Initialize the source. 11/27/2014, Bing Li
	 */
	public StopDNNotificationMulticastorSource(FreeClientPool clientPool, int treeBranchCount, int serverPort, StopDNMultiNotificationCreator creator)
	{
		super(clientPool, treeBranchCount, serverPort, creator);
	}

}

package com.greatfree.testing.cluster.dn;

import com.greatfree.multicast.ChildMulticastor;
import com.greatfree.remote.FreeClientPool;
import com.greatfree.testing.message.StopDNMultiNotification;

/*
 * The multicastor is derived from ChildMulticastor to transfer the notification, StopDNMultiNotification, to the children nodes. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNNotificationMulticastor extends ChildMulticastor<StopDNMultiNotification, StopDNMultiNotificationCreator>
{

	public StopDNNotificationMulticastor(FreeClientPool clientPool, int treeBranchCount, int clusterServerPort, StopDNMultiNotificationCreator messageCreator)
	{
		super(clientPool, treeBranchCount, clusterServerPort, messageCreator);
	}

}

package com.greatfree.testing.cluster.dn;

import java.util.HashMap;

import com.greatfree.multicast.ChildMulticastMessageCreatable;
import com.greatfree.testing.message.BroadcastNotification;
import com.greatfree.util.Tools;

/*
 * The creator generates the notifications of BroadcastNotification that should be multicast to the local DN's children. 11/27/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class BroadcastNotificationCreator implements ChildMulticastMessageCreatable<BroadcastNotification>
{

	@Override
	public BroadcastNotification createInstanceWithChildren(BroadcastNotification msg, HashMap<String, String> children)
	{
		return new BroadcastNotification(Tools.generateUniqueKey(), children, msg.getMessage());
	}

}

package com.greatfree.testing.cluster.dn;

import java.util.HashMap;

import com.greatfree.multicast.ChildMulticastMessageCreatable;
import com.greatfree.testing.message.DNBroadcastRequest;
import com.greatfree.util.Tools;

/*
 * The creator initiates the instance of DNBroadcastRequest that is needed by the multicastor. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class DNBroadcastRequestCreator implements ChildMulticastMessageCreatable<DNBroadcastRequest>
{

	@Override
	public DNBroadcastRequest createInstanceWithChildren(DNBroadcastRequest msg, HashMap<String, String> children)
	{
		return new DNBroadcastRequest(msg.getRequest(), Tools.generateUniqueKey(), msg.getCollaboratorKey(), children);
	}

}

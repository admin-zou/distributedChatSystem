package com.greatfree.testing.cluster.dn;

import com.greatfree.concurrency.BoundNotificationThreadCreatable;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.message.DNBroadcastRequest;

/*
 * The creator initialize the instance of the thread, TransmitBroadcastRequestThread. The BoundNotificationDispatcher can schedule tasks to the thread. 11/29/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class MulticastBroadcastRequestThreadCreator implements BoundNotificationThreadCreatable<DNBroadcastRequest, MulticastMessageDisposer<DNBroadcastRequest>, MulticastBroadcastRequestThread>
{

	@Override
	public MulticastBroadcastRequestThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MulticastMessageDisposer<DNBroadcastRequest> binder)
	{
		return new MulticastBroadcastRequestThread(taskSize, dispatcherKey, binder);
	}

}

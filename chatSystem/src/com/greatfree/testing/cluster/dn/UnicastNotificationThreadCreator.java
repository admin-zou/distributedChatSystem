package com.greatfree.testing.cluster.dn;

import com.greatfree.concurrency.BoundNotificationThreadCreatable;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.message.UnicastNotification;

/*
 * This is an implementation of the interface BoundNotificationThreadCreatable to create the instance of UnicastNotificationThread inside the pool, BoundNotificationDispatcher. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class UnicastNotificationThreadCreator implements BoundNotificationThreadCreatable<UnicastNotification, MulticastMessageDisposer<UnicastNotification>, UnicastNotificationThread>
{

	@Override
	public UnicastNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MulticastMessageDisposer<UnicastNotification> binder)
	{
		return new UnicastNotificationThread(taskSize, dispatcherKey, binder);
	}

}

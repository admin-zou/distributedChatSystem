package com.greatfree.testing.cluster.dn;

import com.greatfree.concurrency.BoundNotificationThreadCreatable;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.message.AnycastNotification;

/*
 * This is an implementation of the interface BoundNotificationThreadCreatable to create the instance of AnycastNotificationThread inside the pool, BoundNotificationDispatcher. 11/26/2014, Bing Li
 */

// Created: 11/25/2016, Bing Li
public class AnycastNotificationThreadCreator implements BoundNotificationThreadCreatable<AnycastNotification, MulticastMessageDisposer<AnycastNotification>, AnycastNotificationThread>
{

	@Override
	public AnycastNotificationThread createNotificationThreadInstance(int taskSize, String dispatcherKey, MulticastMessageDisposer<AnycastNotification> binder)
	{
		return new AnycastNotificationThread(taskSize, dispatcherKey, binder);
	}

}

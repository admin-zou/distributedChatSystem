package com.greatfree.testing.server;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.ShutdownServerNotification;

/*
 * The code here attempts to create instances of ShutdownThread. 01/20/2016, Bing Li
 */

// Created: 01/20/2016, Bing Li
public class ShutdownThreadCreator implements NotificationThreadCreatable<ShutdownServerNotification, ShutdownThread>
{
	@Override
	public ShutdownThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownThread(taskSize);
	}
}

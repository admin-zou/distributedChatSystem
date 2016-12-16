package com.greatfree.testing.cluster.coordinator.admin;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.ShutdownDNNotification;

/*
 * The creator here attempts to create instances of ShutdownDNThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class ShutdownDNThreadCreator implements NotificationThreadCreatable<ShutdownDNNotification, ShutdownDNThread>
{

	@Override
	public ShutdownDNThread createNotificationThreadInstance(int taskSize)
	{
		return new ShutdownDNThread(taskSize);
	}

}

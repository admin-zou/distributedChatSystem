package com.greatfree.testing.cluster.coordinator.client;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.ClientForUnicastNotification;

/*
 * The creator here attempts to create instances of ClientForBroadcastNotificationThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/21/2016, Bing Li
public class ClientForUnicastNotificationThreadCreator implements NotificationThreadCreatable<ClientForUnicastNotification, ClientForUnicastNotificationThread>
{

	@Override
	public ClientForUnicastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ClientForUnicastNotificationThread(taskSize);
	}
}

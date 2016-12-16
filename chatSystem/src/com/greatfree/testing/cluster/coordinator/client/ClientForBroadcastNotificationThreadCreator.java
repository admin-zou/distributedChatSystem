package com.greatfree.testing.cluster.coordinator.client;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.ClientForBroadcastNotification;

/*
 * The creator here attempts to create instances of ClientForBroadcastNotificationThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/21/2016, Bing Li
public class ClientForBroadcastNotificationThreadCreator implements NotificationThreadCreatable<ClientForBroadcastNotification, ClientForBroadcastNotificationThread>
{

	@Override
	public ClientForBroadcastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ClientForBroadcastNotificationThread(taskSize);
	}

}

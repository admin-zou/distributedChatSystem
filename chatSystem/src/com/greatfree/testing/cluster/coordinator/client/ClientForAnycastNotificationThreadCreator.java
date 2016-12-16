package com.greatfree.testing.cluster.coordinator.client;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.ClientForAnycastNotification;

/*
 * The creator here attempts to create instances of ClientForAnycastNotificationThread. It works with the notification dispatcher to schedule the tasks concurrently. 11/27/2014, Bing Li
 */

// Created: 11/21/2016, Bing Li
public class ClientForAnycastNotificationThreadCreator implements NotificationThreadCreatable<ClientForAnycastNotification, ClientForAnycastNotificationThread>
{

	@Override
	public ClientForAnycastNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new ClientForAnycastNotificationThread(taskSize);
	}

}

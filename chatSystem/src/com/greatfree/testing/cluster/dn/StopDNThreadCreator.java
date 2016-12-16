package com.greatfree.testing.cluster.dn;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.StopDNMultiNotification;

/*
 * The code here attempts to create instances of StopDNMultiNotificationThread. It works with the notification dispatcher. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class StopDNThreadCreator implements NotificationThreadCreatable<StopDNMultiNotification, StopDNThread>
{

	@Override
	public StopDNThread createNotificationThreadInstance(int taskSize)
	{
		return new StopDNThread(taskSize);
	}

}

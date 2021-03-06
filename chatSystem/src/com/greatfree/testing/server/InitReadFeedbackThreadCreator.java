package com.greatfree.testing.server;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.message.InitReadNotification;

/*
 * The code here attempts to create instances of SetInputStreamThread. 11/07/2014, Bing Li
 */

// Created: 11/09/2014, Bing Li
public class InitReadFeedbackThreadCreator implements NotificationThreadCreatable<InitReadNotification, InitReadFeedbackThread>
{
	@Override
	public InitReadFeedbackThread createNotificationThreadInstance(int taskSize)
	{
		return new InitReadFeedbackThread(taskSize);
	}
}

package com.greatfree.testing.server;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.TestNotification; //zou

public class TestNotificationThreadCreator implements NotificationThreadCreatable<TestNotification,TestNotificationThread>
{
	public TestNotificationThread createNotificationThreadInstance(int taskSize)
	{
		return new TestNotificationThread(taskSize);
	}
}

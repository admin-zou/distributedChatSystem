package com.greatfree.testing.server;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.message.TestNotification; //zou
import com.greatfree.testing.server.resources.FriendListDB;
import com.greatfree.testing.data.ServerConfig;


public class TestNotificationThread extends NotificationQueue<TestNotification>
{
	public TestNotificationThread(int taskSize)
	{
		super(taskSize);
	}
	
	public void run()
	{
		TestNotification notification;
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 02/11/2016, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 02/11/2016, Bing Li
					notification = this.getNotification();
					
					System.out.println(notification.getFriendList().getName()+" "+notification.getFriendList().getMessage()); //获取到消息了，给数据库设置
					
					//设置给DB
					//FriendListDB.SERVER().setNamePort(notification.getFriendList());
					FriendListDB.SERVER().addFriend(notification.getFriendList());
					
					
					// Collect the resource kept by the notification. 02/11/2016, Bing Li
					this.disposeMessage(notification);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				// Wait for a moment after all of the existing notifications are processed. 01/20/2016, Bing Li
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}

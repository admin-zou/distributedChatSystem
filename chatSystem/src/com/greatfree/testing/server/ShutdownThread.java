package com.greatfree.testing.server;

import java.io.IOException;

import com.greatfree.concurrency.NotificationQueue;
import com.greatfree.testing.admin.AdminConfig;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.ShutdownServerNotification;
import com.greatfree.util.ServerStatus;

/*
 * The thread receives shutdown notification from the administrator and shut down the server. 01/20/2016, Bing Li
 */

// Created: 01/20/2016, Bing Li
public class ShutdownThread extends NotificationQueue<ShutdownServerNotification>
{
	/*
	 * Initialize the thread. The argument, taskSize, is used to limit the count of tasks to be queued. 01/20/2016, Bing Li
	 */
	public ShutdownThread(int taskSize)
	{
		super(taskSize);
	}
	
	/*
	 * Once if a shutdown notification is received, it is processed concurrently as follows. 01/20/2016, Bing Li
	 */
	public void run()
	{
		// Declare an instance of ShutdownServerNotification. 01/20/2016, Bing Li
		ShutdownServerNotification notification;
		while (!this.isShutdown())
		{
			// Check whether the notification queue is empty. 01/20/2016, Bing Li
			while (!this.isEmpty())
			{
				try
				{
					// Dequeue the notification. 01/20/2016, Bing Li
					notification = this.getNotification();
					// Set the server as shutdown. 01/30/2016, Bing Li
					ServerStatus.FREE().setShutdown(AdminConfig.SERVER_ID);
					try
					{
						// Shutdown the server. 01/20/2016, Bing Li
						Server.FREE().stop();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					// Dispose the notification. 01/20/2016, Bing Li
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

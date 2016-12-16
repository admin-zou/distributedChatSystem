package com.greatfree.testing.coordinator.admin;

import com.greatfree.concurrency.NotificationDispatcher;
import com.greatfree.concurrency.Scheduler;
import com.greatfree.concurrency.ServerMessageDispatcher;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.MessageType;
import com.greatfree.testing.message.ShutdownCrawlServerNotification;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond the administrator's notifications for the coordinator. As the administrator is a human operator, the design is more complicated than required. Just an exercise. :-) 11/27/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization of notification dispatchers is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 11/27/2014, Bing Li
public class AdminServerDispatcher extends ServerMessageDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process the crawler shutting-down concurrently. 11/27/2014, Bing Li
	private NotificationDispatcher<ShutdownCrawlServerNotification, ShutdownCrawlServerThread, ShutdownCrawlServerThreadCreator> shutdownCrawlServerNotificationDispatcher;

	/*
	 * Initialize. 11/27/2014, Bing Li
	 */
	public AdminServerDispatcher(int corePoolSize, long keepAliveTime)
	{
		// Set the pool size and threads' alive time. 11/27/2014, Bing Li
		super(corePoolSize, keepAliveTime);

		// Initialize the shutdown dispatcher. 11/27/2014, Bing Li
		this.shutdownCrawlServerNotificationDispatcher = new NotificationDispatcher<ShutdownCrawlServerNotification, ShutdownCrawlServerThread, ShutdownCrawlServerThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ShutdownCrawlServerThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	}

	/*
	 * Shut down the administration message dispatcher. 11/27/2014, Bing Li
	 */
	public void shutdown() throws InterruptedException
	{
		// Dispose the crawler shutting-down dispatcher. 11/27/2014, Bing Li
		this.shutdownCrawlServerNotificationDispatcher.dispose();
		// Shutdown the server message dispatcher. 11/27/2014, Bing Li
		super.shutdown();
	}
	
	/*
	 * Process the available messages in a concurrent way. 11/27/2014, Bing Li
	 */
	public void consume(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/27/2014, Bing Li
		switch (message.getMessage().getType())
		{
			// If the message is the notification to register the crawler server. 11/27/2014, Bing Li
			case MessageType.SHUTDOWN_CRAWL_SERVER_NOTIFICATION:
				// Check whether the shutdown notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.shutdownCrawlServerNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.shutdownCrawlServerNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/27/2014, Bing Li
				this.shutdownCrawlServerNotificationDispatcher.enqueue((ShutdownCrawlServerNotification)message.getMessage());
				break;
		}
	}
}

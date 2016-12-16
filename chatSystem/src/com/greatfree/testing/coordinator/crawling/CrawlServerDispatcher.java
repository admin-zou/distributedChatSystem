package com.greatfree.testing.coordinator.crawling;

import com.greatfree.concurrency.NotificationDispatcher;
import com.greatfree.concurrency.Scheduler;
import com.greatfree.concurrency.ServerMessageDispatcher;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.CrawledLinksNotification;
import com.greatfree.testing.message.MessageType;
import com.greatfree.testing.message.RegisterCrawlServerNotification;
import com.greatfree.testing.message.UnregisterCrawlServerNotification;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond crawlers' requests and receive crawlers' notifications for the coordinator. 11/24/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization of notification dispatchers is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 11/24/2014, Bing Li
public class CrawlServerDispatcher extends ServerMessageDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process the crawler registration concurrently. 11/27/2014, Bing Li
	private NotificationDispatcher<RegisterCrawlServerNotification, RegisterCrawlServerThread, RegisterCrawlServerThreadCreator> registerCrawlServerNotificationDispatcher;
	// Declare a notification dispatcher to process the crawler unregistering concurrently. 11/28/2014, Bing Li
	private NotificationDispatcher<UnregisterCrawlServerNotification, UnregisterCrawlServerThread, UnregisterCrawlServerThreadCreator> unregisterCrawlServerNotificationDispatcher;
	// Declare a notification dispatcher to distribute crawled links concurrently. 11/28/2014, Bing Li
	private NotificationDispatcher<CrawledLinksNotification, DistributeLinksThread, DistributeLinksThreadCreator> distributeCrawledLinksNotificationDispatcher;

	/*
	 * Initialize. 11/24/2014, Bing Li
	 */
	public CrawlServerDispatcher(int corePoolSize, long keepAliveTime)
	{
		// Set the pool size and threads' alive time. 11/27/2014, Bing Li
		super(corePoolSize, keepAliveTime);

		// Initialize the crawler registration dispatcher. 11/27/2014, Bing Li
		this.registerCrawlServerNotificationDispatcher = new NotificationDispatcher<RegisterCrawlServerNotification, RegisterCrawlServerThread, RegisterCrawlServerThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterCrawlServerThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the crawler unregistering dispatcher. 11/27/2014, Bing Li
		this.unregisterCrawlServerNotificationDispatcher = new NotificationDispatcher<UnregisterCrawlServerNotification, UnregisterCrawlServerThread, UnregisterCrawlServerThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new UnregisterCrawlServerThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the distributing crawled links dispatcher. 11/28/2014, Bing Li
		this.distributeCrawledLinksNotificationDispatcher = new NotificationDispatcher<CrawledLinksNotification, DistributeLinksThread, DistributeLinksThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new DistributeLinksThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	}

	/*
	 * Shut down the crawling message dispatcher. 11/24/2014, Bing Li
	 */
	public void shutdown() throws InterruptedException
	{
		// Dispose the crawler registration dispatcher. 11/27/2014, Bing Li
		this.registerCrawlServerNotificationDispatcher.dispose();
		// Dispose the crawler unregistering dispatcher. 11/28/2014, Bing Li
		this.unregisterCrawlServerNotificationDispatcher.dispose();
		// Dispose the distributing crawled links dispatcher. 11/28/2014, Bing Li
		this.distributeCrawledLinksNotificationDispatcher.dispose();
		// Shutdown the server message dispatcher. 11/27/2014, Bing Li
		super.shutdown();
	}
	
	/*
	 * Process the available messages in a concurrent way. 11/24/2014, Bing Li
	 */
	public void consume(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/27/2014, Bing Li
		switch (message.getMessage().getType())
		{
			// If the message is the notification to register the crawler server. 11/27/2014, Bing Li
			case MessageType.REGISTER_CRAWL_SERVER_NOTIFICATION:
				// Check whether the crawling registration notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.registerCrawlServerNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.registerCrawlServerNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/27/2014, Bing Li
				this.registerCrawlServerNotificationDispatcher.enqueue((RegisterCrawlServerNotification)message.getMessage());
				break;
				
			// If the message is the notification to unregister the crawler server. 11/28/2014, Bing Li
			case MessageType.UNREGISTER_CRAWL_SERVER_NOTIFICATION:
				// Check whether the crawling unregistration dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.unregisterCrawlServerNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.unregisterCrawlServerNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/28/2014, Bing Li
				this.unregisterCrawlServerNotificationDispatcher.enqueue((UnregisterCrawlServerNotification)message.getMessage());
				break;

			// If the message is the notification which contains the crawled link. 11/28/2014, Bing Li
			case MessageType.CRAWLED_LINKS_NOTIFICATION:
				// Check whether the distributed links notification is ready or not. 01/14/2016, Bing Li
				if (!this.distributeCrawledLinksNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.distributeCrawledLinksNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/28/2014, Bing Li
				this.distributeCrawledLinksNotificationDispatcher.enqueue((CrawledLinksNotification)message.getMessage());
				break;
		}
	}
}

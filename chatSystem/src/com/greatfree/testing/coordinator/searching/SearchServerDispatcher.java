package com.greatfree.testing.coordinator.searching;

import com.greatfree.concurrency.NotificationDispatcher;
import com.greatfree.concurrency.RequestDispatcher;
import com.greatfree.concurrency.Scheduler;
import com.greatfree.concurrency.ServerMessageDispatcher;
import com.greatfree.message.InitReadNotification;
import com.greatfree.message.SystemMessageType;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.IsPublisherExistedRequest;
import com.greatfree.testing.message.IsPublisherExistedResponse;
import com.greatfree.testing.message.IsPublisherExistedStream;
import com.greatfree.testing.message.MessageType;
import com.greatfree.testing.message.SearchKeywordRequest;
import com.greatfree.testing.message.SearchKeywordResponse;
import com.greatfree.testing.message.SearchKeywordStream;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond searchers' requests and receive their notifications for the coordinator. 11/29/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization request dispatcher is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 11/29/2014, Bing Li
public class SearchServerDispatcher extends ServerMessageDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to deal with instances of InitReadNotification from a client concurrently such that the client can initialize its ObjectInputStream. 11/29/2014, Bing Li
	private NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator> initReadFeedbackNotificationDispatcher;
	// Declare a request dispatcher to respond users requests concurrently. 11/29/2014, Bing Li
	private RequestDispatcher<IsPublisherExistedRequest, IsPublisherExistedStream, IsPublisherExistedResponse, IsPublisherExistedThread, IsPublisherExistedThreadCreator> isPublisherExistedRequestDispatcher;
	// Declare a request dispatcher to respond users requests concurrently. 11/29/2014, Bing Li
	private RequestDispatcher<SearchKeywordRequest, SearchKeywordStream, SearchKeywordResponse, SearchKeywordThread, SearchKeywordThreadCreator> searchKeywordRequestDispatcher;
	
	/*
	 * Initialize. 11/29/2014, Bing Li
	 */
	public SearchServerDispatcher(int corePoolSize, long keepAliveTime)
	{
		super(corePoolSize, keepAliveTime);

		// Initialize the notification dispatcher. 01/14/2016, Bing Li
		this.initReadFeedbackNotificationDispatcher = new NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new InitReadFeedbackThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the request dispatcher. 11/29/2014, Bing Li
		this.isPublisherExistedRequestDispatcher = new RequestDispatcher<IsPublisherExistedRequest, IsPublisherExistedStream, IsPublisherExistedResponse, IsPublisherExistedThread, IsPublisherExistedThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new IsPublisherExistedThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the request dispatcher. 11/29/2014, Bing Li
		this.searchKeywordRequestDispatcher = new RequestDispatcher<SearchKeywordRequest, SearchKeywordStream, SearchKeywordResponse, SearchKeywordThread, SearchKeywordThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new SearchKeywordThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	}

	/*
	 * Shut down the server message dispatcher. 11/29/2014, Bing Li
	 */
	public void shutdown() throws InterruptedException
	{
		this.initReadFeedbackNotificationDispatcher.dispose();
		this.isPublisherExistedRequestDispatcher.dispose();
		this.searchKeywordRequestDispatcher.dispose();
		super.shutdown();
	}
	
	/*
	 * Process the available messages in a concurrent way. 11/29/2014, Bing Li
	 */
	public void consume(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/29/2014, Bing Li
		switch (message.getMessage().getType())
		{
			// If the message is the one of initializing notification. 11/29/2014, Bing Li
			case SystemMessageType.INIT_READ_NOTIFICATION:
				// Check whether the notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.initReadFeedbackNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.initReadFeedbackNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent feedback. 11/29/2014, Bing Li
				this.initReadFeedbackNotificationDispatcher.enqueue((InitReadNotification)message.getMessage());
				break;

			// Process the search request. 11/29/2014, Bing Li
			case MessageType.IS_PUBLISHER_EXISTED_REQUEST:
				// Check whether the dispatcher is ready or not. 01/14/2016, Bing Li
				if (this.isPublisherExistedRequestDispatcher.isReady())
				{
					// Execute the dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.isPublisherExistedRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 11/29/2014, Bing Li
				this.isPublisherExistedRequestDispatcher.enqueue(new IsPublisherExistedStream(message.getOutStream(), message.getLock(), (IsPublisherExistedRequest)message.getMessage()));
				break;
				
			// Process the search request. 11/29/2014, Bing Li
			case MessageType.SEARCH_KEYWORD_REQUEST:
				// Check whether the dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.searchKeywordRequestDispatcher.isReady())
				{
					// Execute the dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.searchKeywordRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 11/29/2014, Bing Li
				this.searchKeywordRequestDispatcher.enqueue(new SearchKeywordStream(message.getOutStream(), message.getLock(), (SearchKeywordRequest)message.getMessage()));
				break;
		}
	}
}

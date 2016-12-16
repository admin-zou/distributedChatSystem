package com.greatfree.testing.client;

import java.util.Calendar;

import com.greatfree.concurrency.NotificationDispatcher;
import com.greatfree.concurrency.Scheduler;
import com.greatfree.concurrency.ServerMessageDispatcher;
import com.greatfree.message.InitReadFeedbackNotification;
import com.greatfree.message.SystemMessageType;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.message.MessageType;
import com.greatfree.testing.message.NodeKeyNotification;

/*
 * The server dispatcher resides on the client rather than on the classic server. It has two goals. First, if the client is a peer end, i.e., a server plus a client, the dispatcher is required. Second, it helps a client to initialize instances of FreeClient to read remote data. The 2nd goal is needed in most clients. This is what it is done in the sample. 11/07/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization of notification dispatchers is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 11/07/2014, Bing Li
public class ClientServerDispatcher extends ServerMessageDispatcher<ServerMessage>
{
	// Declare a instance of notification dispatcher to deal with received the notification that contains the node key. 11/09/2014, Bing Li
	private NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator> nodeKeyNotificationDispatcher;
	
	// Declare a instance of notification dispatcher to deal with received the feedback for ObjectInputStream. 11/07/2014, Bing Li
	private NotificationDispatcher<InitReadFeedbackNotification, SetInputStreamThread, SetInputStreamThreadCreator> setInputStreamNotificationDispatcher;

	/*
	 * Initialize the dispatcher. 11/07/2014, Bing Li
	 */
	public ClientServerDispatcher(int corePoolSize, long keepAliveTime)
	{
		// Initialize the parent class. 11/07/2014, Bing Li
		super(corePoolSize, keepAliveTime);

		// Initialize the notification dispatcher for the notification, NodeKeyNotification. 11/09/2014, Bing Li
		this.nodeKeyNotificationDispatcher = new NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the notification dispatcher for the notification, InitReadFeedbackNotification. 11/07/2014, Bing Li
		this.setInputStreamNotificationDispatcher = new NotificationDispatcher<InitReadFeedbackNotification, SetInputStreamThread, SetInputStreamThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new SetInputStreamThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	}

	/*
	 * Shutdown the dispatcher. 11/07/2014, Bing Li
	 */
	public void shutdown() throws InterruptedException
	{
		// Shutdown the notification dispatcher for setting the node key. 11/07/2014, Bing Li
		this.nodeKeyNotificationDispatcher.dispose();
		// Shutdown the notification dispatcher for initializing ObjectInputStream. 11/07/2014, Bing Li
		this.setInputStreamNotificationDispatcher.dispose();
		// Shutdown the parent dispatcher. 11/07/2014, Bing Li
		super.shutdown();
	}

	/*
	 * Dispatch received messages to corresponding threads respectively for concurrent processing. 11/07/2014, Bing Li
	 */
	public void consume(OutMessageStream<ServerMessage> message)
	{
		// Detect the message type. 11/07/2014, Bing Li
		switch (message.getMessage().getType())
		{
			// Process the notification of NodeKeyNotification. 11/09/2014, Bing Li
			case MessageType.NODE_KEY_NOTIFICATION:
				System.out.println("NODE_KEY_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the node-key notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.nodeKeyNotificationDispatcher.isReady())
				{
					// Execute the node-key notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.nodeKeyNotificationDispatcher);
				}
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/09/2014, Bing Li
				this.nodeKeyNotificationDispatcher.enqueue((NodeKeyNotification)message.getMessage());
				break;

			// Process the notification of the type, InitReadFeedbackNotification. 11/07/2014, Bing Li
			case SystemMessageType.INIT_READ_FEEDBACK_NOTIFICATION:
				System.out.println("INIT_READ_FEEDBACK_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the set-input-stream notification dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.setInputStreamNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.setInputStreamNotificationDispatcher);
				}
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/07/2014, Bing Li
				this.setInputStreamNotificationDispatcher.enqueue((InitReadFeedbackNotification)message.getMessage());
				break;
		}
	}
}

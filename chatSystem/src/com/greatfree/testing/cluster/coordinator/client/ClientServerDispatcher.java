package com.greatfree.testing.cluster.coordinator.client;

import java.util.Calendar;

import com.greatfree.concurrency.NotificationDispatcher;
import com.greatfree.concurrency.RequestDispatcher;
import com.greatfree.concurrency.Scheduler;
import com.greatfree.concurrency.ServerMessageDispatcher;
import com.greatfree.message.InitReadNotification;
import com.greatfree.message.SystemMessageType;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.ClientForAnycastNotification;
import com.greatfree.testing.message.ClientForAnycastRequest;
import com.greatfree.testing.message.ClientForAnycastResponse;
import com.greatfree.testing.message.ClientForAnycastStream;
import com.greatfree.testing.message.ClientForBroadcastNotification;
import com.greatfree.testing.message.ClientForBroadcastRequest;
import com.greatfree.testing.message.ClientForBroadcastResponse;
import com.greatfree.testing.message.ClientForBroadcastStream;
import com.greatfree.testing.message.ClientForUnicastNotification;
import com.greatfree.testing.message.ClientForUnicastRequest;
import com.greatfree.testing.message.ClientForUnicastResponse;
import com.greatfree.testing.message.ClientForUnicastStream;
import com.greatfree.testing.message.MessageType;
import com.greatfree.testing.message.RegisterClientNotification;
import com.greatfree.testing.message.ShutdownServerNotification;
import com.greatfree.testing.message.UnregisterClientNotification;
import com.greatfree.testing.server.InitReadFeedbackThread;
import com.greatfree.testing.server.InitReadFeedbackThreadCreator;
import com.greatfree.testing.server.RegisterClientThread;
import com.greatfree.testing.server.RegisterClientThreadCreator;
import com.greatfree.testing.server.ShutdownThread;
import com.greatfree.testing.server.ShutdownThreadCreator;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond clients' requests and receive their notifications for the coordinator. 11/24/2014, Bing Li
 */

// Created: 11/19/2016, Bing Li
public class ClientServerDispatcher extends ServerMessageDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process the registration notification concurrently. 11/04/2014, Bing Li
	private NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator> registerClientNotificationDispatcher;

	// Declare a notification dispatcher to process the client unregistering concurrently. 11/28/2014, Bing Li
	private NotificationDispatcher<UnregisterClientNotification, UnregisterClientThread, UnregisterClientThreadCreator> unregisterClientNotificationDispatcher;

	// Declare a notification dispatcher to process the notification concurrently for broadcasting. 11/04/2014, Bing Li
	private NotificationDispatcher<ClientForBroadcastNotification, ClientForBroadcastNotificationThread, ClientForBroadcastNotificationThreadCreator> clientForBroadcastNotificationDispatcher;

	// Declare a notification dispatcher to process the notification concurrently for unicasting. 11/04/2014, Bing Li
	private NotificationDispatcher<ClientForUnicastNotification, ClientForUnicastNotificationThread, ClientForUnicastNotificationThreadCreator> clientForUnicastNotificationDispatcher;

	// Declare a notification dispatcher to process the notification concurrently for anycasting. 11/04/2014, Bing Li
	private NotificationDispatcher<ClientForAnycastNotification, ClientForAnycastNotificationThread, ClientForAnycastNotificationThreadCreator> clientForAnycastNotificationDispatcher;

	// Declare a request dispatcher to respond an instance of ClientForBroadcastResponse to the relevant remote client when an instance of ClientForBroadcastRequest is received. 02/15/2016, Bing Li
	private RequestDispatcher<ClientForBroadcastRequest, ClientForBroadcastStream, ClientForBroadcastResponse, ClientForBroadcastRequestThread, ClientForBroadcastRequestThreadCreator> clientForBroadcastRequestDispatcher;

	// Declare a request dispatcher to respond an instance of ClientForUnicastResponse to the relevant remote client when an instance of ClientForUnicastRequest is received. 02/15/2016, Bing Li
	private RequestDispatcher<ClientForUnicastRequest, ClientForUnicastStream, ClientForUnicastResponse, ClientForUnicastRequestThread, ClientForUnicastRequestThreadCreator> clientForUnicastRequestDispatcher;

	// Declare a request dispatcher to respond an instance of ClientForAnycastResponse to the relevant remote client when an instance of ClientForAnycastRequest is received. 02/15/2016, Bing Li
	private RequestDispatcher<ClientForAnycastRequest, ClientForAnycastStream, ClientForAnycastResponse, ClientForAnycastRequestThread, ClientForAnycastRequestThreadCreator> clientForAnycastRequestDispatcher;

	// Declare a notification dispatcher to deal with instances of InitReadNotification from a client concurrently such that the client can initialize its ObjectInputStream. 11/09/2014, Bing Li
	private NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator> initReadFeedbackNotificationDispatcher;
	// Declare a notification dispatcher to shutdown the server when such a notification is received. 02/15/2016, Bing Li
	private NotificationDispatcher<ShutdownServerNotification, ShutdownThread, ShutdownThreadCreator> shutdownNotificationDispatcher;

	/*
	 * Initialize. 11/24/2014, Bing Li
	 */
	public ClientServerDispatcher(int corePoolSize, long keepAliveTime)
	{
		super(corePoolSize, keepAliveTime);

		// Initialize the client registration notification dispatcher. 11/30/2014, Bing Li
		this.registerClientNotificationDispatcher = new NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterClientThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the client unregistering notification dispatcher. 11/30/2014, Bing Li
		this.unregisterClientNotificationDispatcher = new NotificationDispatcher<UnregisterClientNotification, UnregisterClientThread, UnregisterClientThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new UnregisterClientThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the client-for-broadcast-notification dispatcher. 11/30/2014, Bing Li
		this.clientForBroadcastNotificationDispatcher = new NotificationDispatcher<ClientForBroadcastNotification, ClientForBroadcastNotificationThread, ClientForBroadcastNotificationThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ClientForBroadcastNotificationThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the client-for-unicast-notification dispatcher. 11/30/2014, Bing Li
		this.clientForUnicastNotificationDispatcher = new NotificationDispatcher<ClientForUnicastNotification, ClientForUnicastNotificationThread, ClientForUnicastNotificationThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ClientForUnicastNotificationThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the client-for-anycast-notification dispatcher. 11/30/2014, Bing Li
		this.clientForAnycastNotificationDispatcher = new NotificationDispatcher<ClientForAnycastNotification, ClientForAnycastNotificationThread, ClientForAnycastNotificationThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ClientForAnycastNotificationThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the client-for-broadcast-request dispatcher. 11/30/2014, Bing Li
		this.clientForBroadcastRequestDispatcher = new RequestDispatcher<ClientForBroadcastRequest, ClientForBroadcastStream, ClientForBroadcastResponse, ClientForBroadcastRequestThread, ClientForBroadcastRequestThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new ClientForBroadcastRequestThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the client-for-unicast-request dispatcher. 11/30/2014, Bing Li
		this.clientForUnicastRequestDispatcher = new RequestDispatcher<ClientForUnicastRequest, ClientForUnicastStream, ClientForUnicastResponse, ClientForUnicastRequestThread, ClientForUnicastRequestThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new ClientForUnicastRequestThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the client-for-anycast-request dispatcher. 11/30/2014, Bing Li
		this.clientForAnycastRequestDispatcher = new RequestDispatcher<ClientForAnycastRequest, ClientForAnycastStream, ClientForAnycastResponse, ClientForAnycastRequestThread, ClientForAnycastRequestThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new ClientForAnycastRequestThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the read initialization notification dispatcher. 11/30/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher = new NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new InitReadFeedbackThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
		
		// Initialize the shutdown notification dispatcher. 11/30/2014, Bing Li
		this.shutdownNotificationDispatcher = new NotificationDispatcher<ShutdownServerNotification, ShutdownThread, ShutdownThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ShutdownThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	}

	/*
	 * Shut down the server message dispatcher. 09/20/2014, Bing Li
	 */
	public void shutdown() throws InterruptedException
	{
		// Dispose the register dispatcher. 01/14/2016, Bing Li
		this.registerClientNotificationDispatcher.dispose();
		// Dispose the unregister dispatcher. 01/14/2016, Bing Li
		this.unregisterClientNotificationDispatcher.dispose();
		// Dispose the broadcast notification dispatcher. 02/15/2016, Bing Li
		this.clientForBroadcastNotificationDispatcher.dispose();
		// Dispose the unicast notification dispatcher. 02/15/2016, Bing Li
		this.clientForUnicastNotificationDispatcher.dispose();
		// Dispose the anycast notification dispatcher. 02/15/2016, Bing Li
		this.clientForAnycastNotificationDispatcher.dispose();
		// Dispose the broadcast request dispatcher. 02/15/2016, Bing Li
		this.clientForBroadcastRequestDispatcher.dispose();
		// Dispose the unicast request dispatcher. 02/15/2016, Bing Li
		this.clientForUnicastRequestDispatcher.dispose();
		// Dispose the anycast request dispatcher. 02/15/2016, Bing Li
		this.clientForAnycastRequestDispatcher.dispose();
		// Dispose the dispatcher for initializing reading feedback. 11/09/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher.dispose();
		// Dispose the dispatcher for shutdown. 11/09/2014, Bing Li
		this.shutdownNotificationDispatcher.dispose();
		// Shutdown the derived server dispatcher. 11/04/2014, Bing Li
		super.shutdown();
	}
	
	/*
	 * Process the available messages in a concurrent way. 09/20/2014, Bing Li
	 */
	public void consume(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/09/2014, Bing Li
		switch (message.getMessage().getType())
		{
			case MessageType.REGISTER_CLIENT_NOTIFICATION:
				System.out.println("REGISTER_CLIENT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the registry notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.registerClientNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.registerClientNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.registerClientNotificationDispatcher.enqueue((RegisterClientNotification)message.getMessage());
				break;
				
			case MessageType.UNREGISTER_CLIENT_NOTIFICATION:
				System.out.println("UNREGISTER_CLIENT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the unregistry notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.unregisterClientNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.unregisterClientNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.unregisterClientNotificationDispatcher.enqueue((UnregisterClientNotification)message.getMessage());
				break;
				
			case MessageType.CLIENT_FOR_BROADCAST_NOTIFICATION:
				System.out.println("CLIENT_FOR_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the client broadcast notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.clientForBroadcastNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.clientForBroadcastNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.clientForBroadcastNotificationDispatcher.enqueue((ClientForBroadcastNotification)message.getMessage());
				break;
				
			case MessageType.CLIENT_FOR_UNICAST_NOTIFICATION:
				System.out.println("CLIENT_FOR_UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the client unicast notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.clientForUnicastNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.clientForUnicastNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.clientForUnicastNotificationDispatcher.enqueue((ClientForUnicastNotification)message.getMessage());
				break;
				
			case MessageType.CLIENT_FOR_ANYCAST_NOTIFICATION:
				System.out.println("CLIENT_FOR_ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the client anycast notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.clientForAnycastNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.clientForAnycastNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.clientForAnycastNotificationDispatcher.enqueue((ClientForAnycastNotification)message.getMessage());
				break;
				
			case MessageType.CLIENT_FOR_BROADCAST_REQUEST:
				System.out.println("CLIENT_FOR_BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the client broadcast request dispatcher is ready. 02/15/2016, Bing Li
				if (!this.clientForBroadcastRequestDispatcher.isReady())
				{
					// Execute the client broadcast request dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientForBroadcastRequestDispatcher);
				}
				// Enqueue the instance of ClientForBroadcastRequest into the dispatcher for concurrent responding. 02/15/2016, Bing Li
				this.clientForBroadcastRequestDispatcher.enqueue(new ClientForBroadcastStream(message.getOutStream(), message.getLock(), (ClientForBroadcastRequest)message.getMessage()));
				break;
				
			case MessageType.CLIENT_FOR_UNICAST_REQUEST:
				System.out.println("CLIENT_FOR_UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the client unicast request dispatcher is ready. 02/15/2016, Bing Li
				if (!this.clientForUnicastRequestDispatcher.isReady())
				{
					// Execute the client unicast request dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientForUnicastRequestDispatcher);
				}
				// Enqueue the instance of ClientForUnicastRequest into the dispatcher for concurrent responding. 02/15/2016, Bing Li
				this.clientForUnicastRequestDispatcher.enqueue(new ClientForUnicastStream(message.getOutStream(), message.getLock(), (ClientForUnicastRequest)message.getMessage()));
				break;
				
			case MessageType.CLIENT_FOR_ANYCAST_REQUEST:
				System.out.println("CLIENT_FOR_ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the client anycast request dispatcher is ready. 02/15/2016, Bing Li
				if (!this.clientForAnycastRequestDispatcher.isReady())
				{
					// Execute the client anycast request dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.clientForAnycastRequestDispatcher);
				}
				// Enqueue the instance of ClientForAnycastRequest into the dispatcher for concurrent responding. 02/15/2016, Bing Li
				this.clientForAnycastRequestDispatcher.enqueue(new ClientForAnycastStream(message.getOutStream(), message.getLock(), (ClientForAnycastRequest)message.getMessage()));
				break;

			// If the message is the one of initializing notification. 11/09/2014, Bing Li
			case SystemMessageType.INIT_READ_NOTIFICATION:
				System.out.println("INIT_READ_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the reading initialization dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.initReadFeedbackNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.initReadFeedbackNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 11/09/2014, Bing Li
				this.initReadFeedbackNotificationDispatcher.enqueue((InitReadNotification)message.getMessage());
				break;
				
			case MessageType.SHUTDOWN_REGULAR_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_REGULAR_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.shutdownNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.shutdownNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 11/09/2014, Bing Li
				this.shutdownNotificationDispatcher.enqueue((ShutdownServerNotification)message.getMessage());
				break;
		}
	}
}

package com.greatfree.testing.cluster.dn;

import java.util.Calendar;

import com.greatfree.concurrency.BoundBroadcastRequestDispatcher;
import com.greatfree.concurrency.BoundNotificationDispatcher;
import com.greatfree.concurrency.NotificationDispatcher;
import com.greatfree.concurrency.Scheduler;
import com.greatfree.concurrency.ServerMessageDispatcher;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.ClientPool;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.reuse.MulticastMessageDisposer;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.AnycastNotification;
import com.greatfree.testing.message.BroadcastNotification;
import com.greatfree.testing.message.DNAnycastRequest;
import com.greatfree.testing.message.DNAnycastResponse;
import com.greatfree.testing.message.DNBroadcastRequest;
import com.greatfree.testing.message.DNBroadcastResponse;
import com.greatfree.testing.message.MessageType;
import com.greatfree.testing.message.NodeKeyNotification;
import com.greatfree.testing.message.StopDNMultiNotification;
import com.greatfree.testing.message.UnicastNotification;
import com.greatfree.testing.message.UnicastRequest;
import com.greatfree.testing.message.UnicastResponse;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond the coordinator's requests and notifications for the DN server. 11/23/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class DNDispatcher extends ServerMessageDispatcher<ServerMessage>
{
	// Declare a instance of notification dispatcher to deal with received the notification that contains the node key. 11/25/2014, Bing Li
	private NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator> nodeKeyNotificationDispatcher;

	// The disposer is the binder that synchronizes the two bound notification dispatchers, broadcastNotificationDispatcher and multicastBroadcastNotificationDispatcher. After both of them finish their respective tasks concurrently, it disposes the notification of BroadcastNotification finally. 11/27/2014, Bing Li
	private MulticastMessageDisposer<BroadcastNotification> broadcastNotificationDisposer;
	// The dispatcher processes the notification of BroadcastNotification. It must be synchronized by the binder, broadcastNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<BroadcastNotification, MulticastMessageDisposer<BroadcastNotification>, BroadcastNotificationThread, BroadcastNotificationThreadCreator> broadcastNotificationDispatcher;
	// The dispatcher to disseminate the notification of BroadcastNotification to children nodes. It must be synchronized by the binder, broadcastNotificationDisposer. So it is implemented as a bound notification dispatcher. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<BroadcastNotification, MulticastMessageDisposer<BroadcastNotification>, MulticastBroadcastNotificationThread, MulticastBroadcastNotificationThreadCreator> multicastBroadcastNotificationDispatcher;

	// The disposer is the binder that synchronizes the two bound notification dispatchers, unicastNotificationDispatcher and multicastUnicastNotificationDispatcher. But for the specific case of unicast, it is useless. The reason to define it is to fill out the parameters of BoundNotificationDispatcher. In the future version, it is suggested to consider designing additional interfaces to avoid the redundant effort. 11/27/2014, Bing Li
	private MulticastMessageDisposer<UnicastNotification> unicastNotificationDisposer;
	// The dispatcher processes the notification of UnicastNotification. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<UnicastNotification, MulticastMessageDisposer<UnicastNotification>, UnicastNotificationThread, UnicastNotificationThreadCreator> unicastNotificationDispatcher;

	// The disposer is the binder that synchronizes the two bound notification dispatchers, anycastNotificationDispatcher and multicastUnicastNotificationDispatcher. But for the specific case of anycast, it is useless. The reason to define it is to fill out the parameters of BoundNotificationDispatcher. In the future version, it is suggested to consider designing additional interfaces to avoid the redundant effort. 11/27/2014, Bing Li
	private MulticastMessageDisposer<AnycastNotification> anycastNotificationDisposer;
	// The dispatcher processes the notification of AnycastNotification. 11/27/2014, Bing Li
	private BoundNotificationDispatcher<AnycastNotification, MulticastMessageDisposer<AnycastNotification>, AnycastNotificationThread, AnycastNotificationThreadCreator> anycastNotificationDispatcher;

	// Declare the message disposer to collect requests after it is handled and broadcast. 01/14/2016, Bing Li
	private MulticastMessageDisposer<DNBroadcastRequest> broadcastRequestDisposer;
	// Declare the dispatcher to handle the broadcast request. 01/14/2016, Bing Li 
	private BoundBroadcastRequestDispatcher<DNBroadcastRequest, DNBroadcastResponse, MulticastMessageDisposer<DNBroadcastRequest>, BroadcastRequestThread, BroadcastRequestThreadCreator> broadcastRequestDispatcher;
	// Declare the broadcast dispatcher to broadcast the request. 01/14/2016, Bing Li
	private BoundNotificationDispatcher<DNBroadcastRequest, MulticastMessageDisposer<DNBroadcastRequest>, MulticastBroadcastRequestThread, MulticastBroadcastRequestThreadCreator> multicastBroadcastRequestDispatcher;

	// Declare the message disposer to collect requests after it is handled and unicast. 01/14/2016, Bing Li
	private MulticastMessageDisposer<UnicastRequest> unicastRequestDisposer;
	// Declare the dispatcher to handle the unicast request. 01/14/2016, Bing Li 
	private BoundBroadcastRequestDispatcher<UnicastRequest, UnicastResponse, MulticastMessageDisposer<UnicastRequest>, UnicastRequestThread, UnicastRequestThreadCreator> unicastRequestDispatcher;


	// Declare the message disposer to collect requests after it is handled and anycast. 01/14/2016, Bing Li
	private MulticastMessageDisposer<DNAnycastRequest> anycastRequestDisposer;
	// Declare the dispatcher to handle the anycast request. 01/14/2016, Bing Li 
	private BoundBroadcastRequestDispatcher<DNAnycastRequest, DNAnycastResponse, MulticastMessageDisposer<DNAnycastRequest>, AnycastRequestThread, AnycastRequestThreadCreator> anycastRequestDispatcher;

	// A instance of notification dispatcher to deal with received the stop DN notification. 11/27/2014, Bing Li
	private NotificationDispatcher<StopDNMultiNotification, StopDNThread, StopDNThreadCreator> stopDNNotificationDispatcher;

	/*
	 * Initialize the dispatcher. 11/25/2014, Bing Li
	 */
	public DNDispatcher(int corePoolSize, long keepAliveTime)
	{
		super(corePoolSize, keepAliveTime);

		// Initialize the notification dispatcher for the notification, NodeKeyNotification. 11/25/2014, Bing Li
		this.nodeKeyNotificationDispatcher = new NotificationDispatcher<NodeKeyNotification, RegisterThread, RegisterThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the disposer for the notification of BroadcastNotification, which works as a binder. 11/27/2014, Bing Li
		this.broadcastNotificationDisposer = new MulticastMessageDisposer<BroadcastNotification>();
		// Initialize the bound notification dispatcher for the notification, BroadcastNotification. 11/27/2014, Bing Li
		this.broadcastNotificationDispatcher = new BoundNotificationDispatcher<BroadcastNotification, MulticastMessageDisposer<BroadcastNotification>, BroadcastNotificationThread, BroadcastNotificationThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, this.broadcastNotificationDisposer, new BroadcastNotificationThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
		// Initialize the bound notification dispatcher for the notification, BroadcastNotification, to disseminate the notification to children crawlers. 11/27/2014, Bing Li
		this.multicastBroadcastNotificationDispatcher = new BoundNotificationDispatcher<BroadcastNotification, MulticastMessageDisposer<BroadcastNotification>, MulticastBroadcastNotificationThread, MulticastBroadcastNotificationThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, this.broadcastNotificationDisposer, new MulticastBroadcastNotificationThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the disposer for the notification of UnicastNotification, which works as a binder. 11/27/2014, Bing Li
		this.unicastNotificationDisposer = new MulticastMessageDisposer<UnicastNotification>();
		// Initialize the bound notification dispatcher for the notification, BroadcastNotification. 11/27/2014, Bing Li
		this.unicastNotificationDispatcher = new BoundNotificationDispatcher<UnicastNotification, MulticastMessageDisposer<UnicastNotification>, UnicastNotificationThread, UnicastNotificationThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, this.unicastNotificationDisposer, new UnicastNotificationThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the disposer for the notification of AnycastNotification, which works as a binder. 11/27/2014, Bing Li
		this.anycastNotificationDisposer = new MulticastMessageDisposer<AnycastNotification>();
		// Initialize the bound notification dispatcher for the notification, AnycastNotification. 11/27/2014, Bing Li
		this.anycastNotificationDispatcher = new BoundNotificationDispatcher<AnycastNotification, MulticastMessageDisposer<AnycastNotification>, AnycastNotificationThread, AnycastNotificationThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, this.anycastNotificationDisposer, new AnycastNotificationThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
		
		// Initialize the message disposer to collect requests after it is handled and broadcast. 01/14/2016, Bing Li
		this.broadcastRequestDisposer = new MulticastMessageDisposer<DNBroadcastRequest>();
		// Initialize the dispatcher to handle the request. 01/14/2016, Bing Li 
		this.broadcastRequestDispatcher = new BoundBroadcastRequestDispatcher<DNBroadcastRequest, DNBroadcastResponse, MulticastMessageDisposer<DNBroadcastRequest>, BroadcastRequestThread, BroadcastRequestThreadCreator>(ClientPool.SERVER().getPool(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_DN_PORT, ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, this.broadcastRequestDisposer, new BroadcastRequestThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
		// Initialize the broadcast dispatcher to broadcast the request. 01/14/2016, Bing Li
		this.multicastBroadcastRequestDispatcher = new BoundNotificationDispatcher<DNBroadcastRequest, MulticastMessageDisposer<DNBroadcastRequest>, MulticastBroadcastRequestThread, MulticastBroadcastRequestThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, this.broadcastRequestDisposer, new MulticastBroadcastRequestThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the message disposer to collect requests after it is handled and unicast. 01/14/2016, Bing Li
		this.unicastRequestDisposer = new MulticastMessageDisposer<UnicastRequest>();
		// Initialize the dispatcher to handle the request. 01/14/2016, Bing Li 
		this.unicastRequestDispatcher = new BoundBroadcastRequestDispatcher<UnicastRequest, UnicastResponse, MulticastMessageDisposer<UnicastRequest>, UnicastRequestThread, UnicastRequestThreadCreator>(ClientPool.SERVER().getPool(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_DN_PORT, ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, this.unicastRequestDisposer, new UnicastRequestThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the message disposer to collect requests after it is handled and anycast. 01/14/2016, Bing Li
		this.anycastRequestDisposer = new MulticastMessageDisposer<DNAnycastRequest>();
		// Initialize the dispatcher to handle the request. 01/14/2016, Bing Li 
		this.anycastRequestDispatcher = new BoundBroadcastRequestDispatcher<DNAnycastRequest, DNAnycastResponse, MulticastMessageDisposer<DNAnycastRequest>, AnycastRequestThread, AnycastRequestThreadCreator>(ClientPool.SERVER().getPool(), ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_DN_PORT, ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, this.anycastRequestDisposer, new AnycastRequestThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
		
		// Initialize the notification dispatcher for the notification, StopDNMultiNotification. 11/27/2014, Bing Li
		this.stopDNNotificationDispatcher = new NotificationDispatcher<StopDNMultiNotification, StopDNThread, StopDNThreadCreator>(ClientConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new StopDNThreadCreator(), ClientConfig.MAX_NOTIFICATION_TASK_SIZE, ClientConfig.MAX_NOTIFICATION_THREAD_SIZE, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ClientConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ClientConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	}

	/*
	 * Shutdown the dispatcher. 11/25/2014, Bing Li
	 */
	public void shutdown() throws InterruptedException
	{
		// Shutdown the notification dispatcher for setting the node key. 11/25/2014, Bing Li
		this.nodeKeyNotificationDispatcher.dispose();

		// Shutdown the bound notification dispatcher to process broadcast notifications. 11/27/2014, Bing Li
		this.broadcastNotificationDispatcher.dispose();
		// Shutdown the bound notification dispatcher for disseminating the broadcast notifications. 11/27/2014, Bing Li
		this.multicastBroadcastNotificationDispatcher.dispose();

		// Shutdown the bound notification dispatcher to process unicast notifications. 11/27/2014, Bing Li
		this.unicastNotificationDispatcher.dispose();
		
		// Shutdown the bound notification dispatcher to process anycast notifications. 11/27/2014, Bing Li
		this.anycastNotificationDispatcher.dispose();
		
		// Shutdown the bound request dispatcher to process broadcast requests. 11/27/2014, Bing Li
		this.broadcastRequestDispatcher.dispose();
		// Shutdown the bound request dispatcher for disseminating the broadcast requests. 11/27/2014, Bing Li
		this.multicastBroadcastRequestDispatcher.dispose();
		
		// Shutdown the bound request dispatcher to process unicast requests. 11/27/2014, Bing Li
		this.unicastRequestDispatcher.dispose();
		
		// Shutdown the bound request dispatcher to process anycast requests. 11/27/2014, Bing Li
		this.anycastRequestDispatcher.dispose();

		// Shutdown the notification dispatcher for stopping DN. 11/27/2014, Bing Li
		this.stopDNNotificationDispatcher.dispose();

		// Shutdown the parent dispatcher. 11/25/2014, Bing Li
		super.shutdown();
	}
	
	/*
	 * Dispatch received messages to corresponding threads respectively for concurrent processing. 11/25/2014, Bing Li
	 */
	public void consume(OutMessageStream<ServerMessage> message)
	{
		BroadcastNotification broadcastNotification;
		UnicastNotification unicastNotification;
		AnycastNotification anycastNotification;
		DNBroadcastRequest broadcastRequest;
		UnicastRequest unicastRequest;
		DNAnycastRequest anycastRequest;
		switch (message.getMessage().getType())
		{
			// Process the notification of NodeKeyNotification. 11/25/2014, Bing Li
			case MessageType.NODE_KEY_NOTIFICATION:
				System.out.println("NODE_KEY_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the notification is ready or not. 01/14/2016, Bing Li
				if (!this.nodeKeyNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.nodeKeyNotificationDispatcher);
				}
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/25/2014, Bing Li
				this.nodeKeyNotificationDispatcher.enqueue((NodeKeyNotification)message.getMessage());
				break;
				
			// Process the notification of BroadcastNotification. 11/27/2014, Bing Li
			case MessageType.BROADCAST_NOTIFICATION:
				System.out.println("BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Cast the message. 11/27/2014, Bing Li
				broadcastNotification = (BroadcastNotification)message.getMessage();
				// Check whether the bound broadcast notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.broadcastNotificationDispatcher.isReady())
				{
					// Execute the bound broadcast notification dispatcher. 02/02/2016, Bing Li
					super.execute(this.broadcastNotificationDispatcher);
				}
				// Enqueue the notification into those bound broadcast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.broadcastNotificationDispatcher.enqueue(broadcastNotification);
				
				// Check whether the bound broadcast notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.multicastBroadcastNotificationDispatcher.isReady())
				{
					// Execute the bound notification broadcast dispatcher. 02/02/2016, Bing Li
					super.execute(this.multicastBroadcastNotificationDispatcher);
				}
				// Enqueue the notification into those bound broadcast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.multicastBroadcastNotificationDispatcher.enqueue(broadcastNotification);
				break;

			// Process the notification of UnicastNotification. 11/27/2014, Bing Li
			case MessageType.UNICAST_NOTIFICATION:
				System.out.println("UNICAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Cast the message. 11/27/2014, Bing Li
				unicastNotification = (UnicastNotification)message.getMessage();
				// Check whether the bound unicast notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.unicastNotificationDispatcher.isReady())
				{
					// Execute the bound unicast notification dispatcher. 02/02/2016, Bing Li
					super.execute(this.unicastNotificationDispatcher);
				}
				// Enqueue the notification into those bound unicast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.unicastNotificationDispatcher.enqueue(unicastNotification);
				break;

			// Process the notification of AnycastNotification. 11/27/2014, Bing Li
			case MessageType.ANYCAST_NOTIFICATION:
				System.out.println("ANYCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Cast the message. 11/27/2014, Bing Li
				anycastNotification = (AnycastNotification)message.getMessage();
				// Check whether the bound anycast notification dispatcher is ready. 02/02/2016, Bing Li
				if (!this.anycastNotificationDispatcher.isReady())
				{
					// Execute the bound anycast notification dispatcher. 02/02/2016, Bing Li
					super.execute(this.anycastNotificationDispatcher);
				}
				// Enqueue the notification into those bound anycast notification dispatchers. The notifications are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.anycastNotificationDispatcher.enqueue(anycastNotification);
				break;
				
			// Process the request of DNBroadcastRequest. 11/27/2014, Bing Li
			case MessageType.BROADCAST_REQUEST:
				System.out.println("BROADCAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Cast the message. 11/27/2014, Bing Li
				broadcastRequest = (DNBroadcastRequest)message.getMessage();
				// Check whether the bound broadcast request dispatcher is ready. 02/02/2016, Bing Li
				if (!this.broadcastRequestDispatcher.isReady())
				{
					// Execute the bound broadcast request dispatcher. 02/02/2016, Bing Li
					super.execute(this.broadcastRequestDispatcher);
				}
				// Enqueue the request into those bound broadcast request dispatchers. The requests are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.broadcastRequestDispatcher.enqueue(broadcastRequest);

				// Check whether the bound broadcast request dispatcher is ready. 02/02/2016, Bing Li
				if (!this.multicastBroadcastRequestDispatcher.isReady())
				{
					// Execute the bound request broadcast dispatcher. 02/02/2016, Bing Li
					super.execute(this.multicastBroadcastRequestDispatcher);
				}
				// Enqueue the request into those bound broadcast request dispatchers. The requests are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.multicastBroadcastRequestDispatcher.enqueue(broadcastRequest);
				break;
				
			// Process the request of UnicastRequest. 11/27/2014, Bing Li
			case MessageType.UNICAST_REQUEST:
				System.out.println("UNICAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Cast the message. 11/27/2014, Bing Li
				unicastRequest = (UnicastRequest)message.getMessage();
				// Check whether the bound unicast request dispatcher is ready. 02/02/2016, Bing Li
				if (!this.unicastRequestDispatcher.isReady())
				{
					// Execute the bound unicast request dispatcher. 02/02/2016, Bing Li
					super.execute(this.unicastRequestDispatcher);
				}
				// Enqueue the request into those bound unicast request dispatchers. The requests are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.unicastRequestDispatcher.enqueue(unicastRequest);
				break;
				
			// Process the request of AnycastRequest. 11/27/2014, Bing Li
			case MessageType.ANYCAST_REQUEST:
				System.out.println("ANYCAST_REQUEST received @" + Calendar.getInstance().getTime());
				// Cast the message. 11/27/2014, Bing Li
				anycastRequest = (DNAnycastRequest)message.getMessage();
				// Check whether the bound anycast request dispatcher is ready. 02/02/2016, Bing Li
				if (!this.anycastRequestDispatcher.isReady())
				{
					// Execute the bound anycast request dispatcher. 02/02/2016, Bing Li
					super.execute(this.anycastRequestDispatcher);
				}
				// Enqueue the request into those bound anycast request dispatchers. The requests are queued and processed asynchronously with a proper synchronization. 11/27/2014, Bing Li
				this.anycastRequestDispatcher.enqueue(anycastRequest);
				break;
				
			// Process the notification of StopDNMultiNotification. 11/27/2014, Bing Li
			case MessageType.STOP_DN_NOTIFICATION:
				System.out.println("STOP_DN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the stopping-DN notification thread is ready or not. 01/14/2016, Bing Li
				if (!this.stopDNNotificationDispatcher.isReady())
				{
					// Execute the dispatcher as a thread. 01/16/2016, Bing Li
					super.execute(this.stopDNNotificationDispatcher);
				}
				// Enqueue the notification into the notification dispatcher. The notifications are queued and processed asynchronously. 11/27/2014, Bing Li
				this.stopDNNotificationDispatcher.enqueue((StopDNMultiNotification)message.getMessage());
				break;
		}
	}
}

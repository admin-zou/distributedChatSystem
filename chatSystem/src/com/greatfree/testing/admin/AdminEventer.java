package com.greatfree.testing.admin;

import com.greatfree.concurrency.Scheduler;
import com.greatfree.concurrency.ThreadPool;
import com.greatfree.remote.AsyncRemoteEventer;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.ShutdownCoordinatorServerNotification;
import com.greatfree.testing.message.ShutdownCrawlServerNotification;
import com.greatfree.testing.message.ShutdownMemoryServerNotification;
import com.greatfree.testing.message.ShutdownServerNotification;

/*
 * This is an eventer that sends notifications to the coordinator in a synchronous or asynchronous manner. 11/27/2014, Bing Li
 */

// Created: 11/27/2014, Bing Li
public class AdminEventer
{
	// The notification of ShutdownCrawlServerNotification is sent to the coordinator in an asynchronous manner. 11/27/2014, Bing Li
	private AsyncRemoteEventer<ShutdownCrawlServerNotification> shutdownCrawlServerNotificationEventer;
	// The notification of ShutdownMemoryServerNotification is sent to the coordinator in an asynchronous manner. 11/27/2014, Bing Li
	private AsyncRemoteEventer<ShutdownMemoryServerNotification> shutdownMemServerNotificationEventer;
	// The notification of ShutdownCoordinatorServerNotification is sent to the coordinator in an asynchronous manner. 11/27/2014, Bing Li
	private AsyncRemoteEventer<ShutdownCoordinatorServerNotification> shutdownCoordinatorNotificationEventer;
	// The notification of ShutdownServerNotification is sent to the server in an asynchronous manner. 01/20/2016, Bing Li
	private AsyncRemoteEventer<ShutdownServerNotification> shutdownServerNotificationEventer;
	// The thread pool that starts up the asynchronous eventer. 11/27/2014, Bing Li
	private ThreadPool pool;
	
	private AdminEventer()
	{
	}
	
	/*
	 * Initialize a singleton. 11/27/2014, Bing Li
	 */
	private static AdminEventer instance = new AdminEventer();
	
	public static AdminEventer CONSOLE()
	{
		if (instance == null)
		{
			instance = new AdminEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Dispose the eventer. 11/27/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		// Shutdown the eventer that sends the notification of ShutdownCrawlServerNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownCrawlServerNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of ShutdownMemoryServerNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownMemServerNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of ShutdownCoordinatorServerNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownCoordinatorNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of ShutdownServerNotification in an asynchronous manner. 01/20/2016, Bing Li
		this.shutdownServerNotificationEventer.dispose();
		// Shutdown the thread pool. 11/27/2014, Bing Li
		this.pool.shutdown();
	}
	
	/*
	 * Initialize the eventer. 11/27/2014, Bing Li
	 */
	public void init()
	{
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		
		// Initialize the shutting down crawling server notification eventer. 11/27/2014, Bing Li
		this.shutdownCrawlServerNotificationEventer = new AsyncRemoteEventer<ShutdownCrawlServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME, ClientConfig.EVENTER_WAIT_ROUND, ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
		// Set the idle checking for the shutdown notification eventer. 11/27/2014, Bing Li
//		this.shutdownCrawlServerNotificationEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the shutdown notification eventer. 11/27/2014, Bing Li
//		this.pool.execute(this.shutdownCrawlServerNotificationEventer);
		
		// Initialize the shutting down memory server notification eventer. 11/27/2014, Bing Li
		this.shutdownMemServerNotificationEventer = new AsyncRemoteEventer<ShutdownMemoryServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME, ClientConfig.EVENTER_WAIT_ROUND, ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
		// Set the idle checking for the shutdown notification eventer. 11/27/2014, Bing Li
//		this.shutdownMemServerNotificationEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the shutdown notification eventer. 11/27/2014, Bing Li
//		this.pool.execute(this.shutdownMemServerNotificationEventer);
		
		// Initialize the shutting down coordinator server notification eventer. 11/27/2014, Bing Li
		this.shutdownCoordinatorNotificationEventer = new AsyncRemoteEventer<ShutdownCoordinatorServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME, ClientConfig.EVENTER_WAIT_ROUND, ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
		// Set the idle checking for the shutdown notification eventer. 11/27/2014, Bing Li
//		this.shutdownCoordinatorNotificationEventer.setIdleChecker(ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD);
		// Start up the shutdown notification eventer. 11/27/2014, Bing Li
//		this.pool.execute(this.shutdownCoordinatorNotificationEventer);
		
		// Initialize the shutting down server notification eventer. 01/20/2016, Bing Li
		this.shutdownServerNotificationEventer = new AsyncRemoteEventer<ShutdownServerNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME, ClientConfig.EVENTER_WAIT_ROUND, ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	}

	/*
	 * Notify the coordinator to shut down the cluster of crawler servers. 11/27/2014, Bing Li
	 */
	public void notifyShutdownCrawlServer()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 01/20/2016, Bing Li
		if (!this.shutdownCrawlServerNotificationEventer.isReady())
		{
			// Execute the eventer manager. 01/20/2016, Bing Li
			this.pool.execute(this.shutdownCrawlServerNotificationEventer);
		}
		// Send the notification. 01/20/2016, Bing Li
		this.shutdownCrawlServerNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownCrawlServerNotification());
	}

	/*
	 * Notify the coordinator to shut down the cluster of memory servers. 11/27/2014, Bing Li
	 */
	public void notifyShutdownMemoryServer()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 01/20/2016, Bing Li
		if (!this.shutdownMemServerNotificationEventer.isReady())
		{
			// Execute the eventer manager. 01/20/2016, Bing Li
			this.pool.execute(this.shutdownMemServerNotificationEventer);
		}
		// Send the notification. 01/20/2016, Bing Li
		this.shutdownMemServerNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownMemoryServerNotification());
	}

	/*
	 * Notify the coordinator to shut down the coordinator. 11/27/2014, Bing Li
	 */
	public void notifyShutdownCoordinator()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 01/20/2016, Bing Li
		if (!this.shutdownCoordinatorNotificationEventer.isReady())
		{
			// Execute the eventer manager. 01/20/2016, Bing Li
			this.pool.execute(this.shutdownCoordinatorNotificationEventer);
		}
		// Send the notification. 01/20/2016, Bing Li
		this.shutdownCoordinatorNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownCoordinatorServerNotification());
	}
	
	/*
	 * Notify the coordinator to shut down the server. 01/20/2016, Bing Li
	 */
	public void notifyShutdownServer()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 01/20/2016, Bing Li
		if (!this.shutdownServerNotificationEventer.isReady())
		{
			// Execute the eventer manager. 01/20/2016, Bing Li
			this.pool.execute(this.shutdownServerNotificationEventer);
		}
		// Send the notification. 01/20/2016, Bing Li
		this.shutdownServerNotificationEventer.notify(ServerConfig.SERVER_IP, ServerConfig.ADMIN_PORT, new ShutdownServerNotification());
	}
}

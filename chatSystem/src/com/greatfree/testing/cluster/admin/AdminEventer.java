package com.greatfree.testing.cluster.admin;

import com.greatfree.concurrency.Scheduler;
import com.greatfree.concurrency.ThreadPool;
import com.greatfree.remote.AsyncRemoteEventer;
import com.greatfree.testing.admin.ClientPool;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.ShutdownCoordinatorNotification;
import com.greatfree.testing.message.ShutdownDNNotification;

/*
 * This is an eventer that sends notifications to the coordinator in a synchronous or asynchronous manner. 11/27/2014, Bing Li
 */

// Created: 11/30/2016, Bing Li
public class AdminEventer
{
	// The notification of ShutdownDNNotification is sent to the coordinator in an asynchronous manner. 11/27/2014, Bing Li
	private AsyncRemoteEventer<ShutdownDNNotification> shutdownDNNotificationEventer;
	// The notification of ShutdownCoordinatorNotification is sent to the coordinator in an asynchronous manner. 11/27/2014, Bing Li
	private AsyncRemoteEventer<ShutdownCoordinatorNotification> shutdownCoordinatorNotificationEventer;
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
		// Shutdown the eventer that sends the notification of ShutdownDNNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownDNNotificationEventer.dispose();
		// Shutdown the eventer that sends the notification of ShutdownCoordinatorNotification in an asynchronous manner. 11/27/2014, Bing Li
		this.shutdownCoordinatorNotificationEventer.dispose();
		// Shutdown the thread pool. 11/27/2014, Bing Li
		this.pool.shutdown();
	}
	
	/*
	 * Initialize the eventer. 11/27/2014, Bing Li
	 */
	public void init()
	{
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		
		// Initialize the shutting down DN notification eventer. 11/27/2014, Bing Li
		this.shutdownDNNotificationEventer = new AsyncRemoteEventer<ShutdownDNNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME, ClientConfig.EVENTER_WAIT_ROUND, ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		
		// Initialize the shutting down coordinator server notification eventer. 11/27/2014, Bing Li
		this.shutdownCoordinatorNotificationEventer = new AsyncRemoteEventer<ShutdownCoordinatorNotification>(ClientPool.ADMIN().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME, ClientConfig.EVENTER_WAIT_ROUND, ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	}

	/*
	 * Notify the coordinator to shut down the cluster of DNs. 11/27/2014, Bing Li
	 */
	public void notifyShutdownDN()
	{
		// Check whether the eventer manager is ready to send asynchronous notifications. 01/20/2016, Bing Li
		if (!this.shutdownDNNotificationEventer.isReady())
		{
			// Execute the eventer manager. 01/20/2016, Bing Li
			this.pool.execute(this.shutdownDNNotificationEventer);
		}
		// Send the notification. 01/20/2016, Bing Li
		this.shutdownDNNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownDNNotification());
	}

	/*
	 * Notify the coordinator to shut down the cluster of DNs. 11/27/2014, Bing Li
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
		this.shutdownCoordinatorNotificationEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT_FOR_ADMIN, new ShutdownCoordinatorNotification());
	}
}

package com.greatfree.testing.client;

import java.io.IOException;

import com.greatfree.concurrency.Scheduler;
import com.greatfree.concurrency.ThreadPool;
import com.greatfree.remote.AsyncRemoteEventer;
import com.greatfree.remote.SyncRemoteEventer;
import com.greatfree.testing.data.ClientConfig;
import com.greatfree.testing.data.FriendList;
import com.greatfree.testing.data.Weather;
import com.greatfree.testing.message.ClientForAnycastNotification;
import com.greatfree.testing.message.ClientForBroadcastNotification;
import com.greatfree.testing.message.ClientForUnicastNotification;
import com.greatfree.testing.message.OnlineNotification;
import com.greatfree.testing.message.RegisterClientNotification;
import com.greatfree.testing.message.UnregisterClientNotification;
import com.greatfree.testing.message.WeatherNotification;
import com.greatfree.testing.message.TestNotification; //zou
import com.greatfree.util.NodeID;

/*
 * The class is an example that applies SynchRemoteEventer and AsyncRemoteEventer. 11/05/2014, Bing Li
 */

// Created: 11/05/2014, Bing Li
public class ClientEventer
{
	// Declare the ip of the remote server. 11/07/2014, Bing Li
	private String ip;
	// Declare the port of the remote server. 11/07/2014, Bing Li
	private int port;
	// The synchronous eventer to send the online notification. 11/07/2014, Bing Li
	private SyncRemoteEventer<OnlineNotification> onlineEventer;
	// The synchronous eventer to send the registering notification. 11/07/2014, Bing Li
	private SyncRemoteEventer<RegisterClientNotification> registerClientEventer;
	// The synchronous eventer to send the unregistering notification. 11/07/2014, Bing Li
	private SyncRemoteEventer<UnregisterClientNotification> unregisterClientEventer;
	// The asynchronous eventer to send one instance of WeatherNotification to the remote server to set the value of the weather. 02/15/2016, Bing Li
	private AsyncRemoteEventer<WeatherNotification> weatherEventer;

	// The asynchronous eventer to send one instance of ClientForBroadcastNotification to the remote server. 02/15/2016, Bing Li
	private AsyncRemoteEventer<ClientForBroadcastNotification> broadcastEventer;
	// The asynchronous eventer to send one instance of ClientForUnicastNotification to the remote server. 02/15/2016, Bing Li
	private AsyncRemoteEventer<ClientForUnicastNotification> unicastEventer;
	// The asynchronous eventer to send one instance of ClientForAnycastNotification to the remote server. 02/15/2016, Bing Li
	private AsyncRemoteEventer<ClientForAnycastNotification> anycastEventer;

	private AsyncRemoteEventer<TestNotification> TestEventer; //zou
	
	// A thread pool to assist sending notification asynchronously. 11/07/2014, Bing Li
	private ThreadPool pool;

	/*
	 * Initialize. 11/07/2014, Bing Li
	 */
	private ClientEventer()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static ClientEventer instance = new ClientEventer();
	
	public static ClientEventer NOTIFY()
	{
		if (instance == null)
		{
			instance = new ClientEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the eventers. 11/07/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.onlineEventer.dispose();
		this.registerClientEventer.dispose();
		this.unregisterClientEventer.dispose();
		this.weatherEventer.dispose();

		this.broadcastEventer.dispose();
		this.unicastEventer.dispose();
		this.anycastEventer.dispose();

		this.TestEventer.dispose(); //zou
		
		// Shutdown the thread pool. 11/07/2014, Bing Li
		this.pool.shutdown();
	}

	/*
	 * Initialize the eventers. 11/07/2014, Bing Li
	 */
	public void init(String ip, int port)
	{
		this.ip = ip;
		this.port = port;
		this.pool = new ThreadPool(ClientConfig.EVENTER_THREAD_POOL_SIZE, ClientConfig.EVENTER_THREAD_POOL_ALIVE_TIME);
		// Initialize the synchronous eventer. The FreeClient pool is one parameter for the initialization. The clients needs to be managed by the pool. 02/15/2016, Bing Li
		this.onlineEventer = new SyncRemoteEventer<OnlineNotification>(ClientPool.LOCAL().getPool());
		// Initialize the synchronous eventer. The FreeClient pool is one parameter for the initialization. The clients needs to be managed by the pool. 02/15/2016, Bing Li
		this.registerClientEventer = new SyncRemoteEventer<RegisterClientNotification>(ClientPool.LOCAL().getPool());
		// Initialize the synchronous eventer. The FreeClient pool is one parameter for the initialization. The clients needs to be managed by the pool. 02/15/2016, Bing Li
		this.unregisterClientEventer = new SyncRemoteEventer<UnregisterClientNotification>(ClientPool.LOCAL().getPool());

		// Initialize the asynchronous eventer. Since the eventer is sent out to the remote server asynchronously, more parameters are required to set. 02/15/2016, Bing Li
		this.weatherEventer = new AsyncRemoteEventer<WeatherNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME, ClientConfig.EVENTER_WAIT_ROUND, ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the asynchronous eventer. Since the eventer is sent out to the remote server asynchronously, more parameters are required to set. 02/15/2016, Bing Li
		this.broadcastEventer = new AsyncRemoteEventer<ClientForBroadcastNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME, ClientConfig.EVENTER_WAIT_ROUND, ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the asynchronous eventer. Since the eventer is sent out to the remote server asynchronously, more parameters are required to set. 02/15/2016, Bing Li
		this.unicastEventer = new AsyncRemoteEventer<ClientForUnicastNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME, ClientConfig.EVENTER_WAIT_ROUND, ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the asynchronous eventer. Since the eventer is sent out to the remote server asynchronously, more parameters are required to set. 02/15/2016, Bing Li
		this.anycastEventer = new AsyncRemoteEventer<ClientForAnycastNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME, ClientConfig.EVENTER_WAIT_ROUND, ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	
		// zou    Initialize the testNotification eventer. 
		this.TestEventer = new AsyncRemoteEventer<TestNotification>(ClientPool.LOCAL().getPool(), this.pool, ClientConfig.EVENT_QUEUE_SIZE, ClientConfig.EVENTER_SIZE, ClientConfig.EVENTING_WAIT_TIME, ClientConfig.EVENTER_WAIT_TIME, ClientConfig.EVENTER_WAIT_ROUND, ClientConfig.EVENT_IDLE_CHECK_DELAY, ClientConfig.EVENT_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	
	}

	/*
	 * Send the online notification to the remote server in a synchronous manner. 11/07/2014, Bing Li
	 */
	public void notifyOnline() throws IOException, InterruptedException
	{
		this.onlineEventer.notify(this.ip, this.port, new OnlineNotification());
	}

	/*
	 * Send the registering notification to the remote server in a synchronous manner. 11/07/2014, Bing Li
	 */
	public void register()
	{
		try
		{
			this.registerClientEventer.notify(this.ip, this.port, new RegisterClientNotification(NodeID.DISTRIBUTED().getKey()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Send the unregistering notification to the remote server in a synchronous manner. 11/07/2014, Bing Li
	 */
	public void unregister()
	{
		try
		{
			this.unregisterClientEventer.notify(this.ip, this.port, new UnregisterClientNotification(NodeID.DISTRIBUTED().getKey()));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * Send the weather notification to the remote server in an asynchronous manner. 11/07/2014, Bing Li
	 */
	public void notifyWeather(Weather weather)
	{
		if (!this.weatherEventer.isReady())
		{
			this.pool.execute(this.weatherEventer);
		}
		this.weatherEventer.notify(this.ip, this.port, new WeatherNotification(weather));
	}
	
	/*
	//add
	public void notifyWeather(String n, Integer p)
	{
		if (!this.weatherEventer.isReady())
		{
			this.pool.execute(this.weatherEventer);
		}
		this.weatherEventer.notify(this.ip, this.port, new WeatherNotification(n,p));
	}
	//end
	 */
	
	/*
	 * Send the broadcast notification to the remote server in an asynchronous manner. 11/07/2014, Bing Li
	 */
	public void notifyBroadcastly(String message)
	{
		if (!this.broadcastEventer.isReady())
		{
			this.pool.execute(this.broadcastEventer);
		}
		this.broadcastEventer.notify(this.ip, this.port, new ClientForBroadcastNotification(message));
	}

	/*
	 * Send the unicast notification to the remote server in an asynchronous manner. 11/07/2014, Bing Li
	 */
	public void notifyUnicastly(String message)
	{
		if (!this.unicastEventer.isReady())
		{
			this.pool.execute(this.unicastEventer);
		}
		this.unicastEventer.notify(this.ip, this.port, new ClientForUnicastNotification(message));
	}

	/*
	 * Send the unicast notification to the remote server in an asynchronous manner. 11/07/2014, Bing Li
	 */
	public void notifyAnycastly(String message)
	{
		if (!this.anycastEventer.isReady())
		{
			this.pool.execute(this.anycastEventer);
		}
		this.anycastEventer.notify(this.ip, this.port, new ClientForAnycastNotification(message));
	}
	
	/*
	public void notifyTest(String message) //zou
	{
		if(!this.TestEventer.isReady())
		{
			this.pool.execute(this.TestEventer);
		}
		this.TestEventer.notify(this.ip,this.port,new TestNotification(message));
	}
	*/
	public void notifyTest(FriendList friendlist)
	{
		if(!this.TestEventer.isReady())
		{
			this.pool.execute(this.TestEventer);
		}
		this.TestEventer.notify(this.ip,this.port,new TestNotification(friendlist));
	}
}

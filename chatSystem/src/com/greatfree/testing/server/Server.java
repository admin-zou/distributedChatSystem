package com.greatfree.testing.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import com.greatfree.concurrency.Runner;
import com.greatfree.concurrency.Scheduler;
import com.greatfree.concurrency.SharedThreadPool;
import com.greatfree.remote.ClientPool;
import com.greatfree.testing.admin.AdminConfig;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.util.ServerStatus;
import com.greatfree.util.TerminateSignal;

/*
 * The class is a testing case for relevant server classes. It is responsible for starting up and shutting down the server. Since it is the unique entry and exit of the server, it is implemented in the form of a singleton. 07/30/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
public class Server
{
	// The ServerSocket waits for clients' connecting. The socket serves the server in the sense that it not only responds to clients' requests but also notifies clients even without clients' requests. 08/10/2014, Bing Li
	private ServerSocket mySocket;
	// The port number for socket. 08/10/2014, Bing Li
	private int myPort;
	// The ServerSocket waits for administrator's connecting. 01/20/2016, Bing Li
	private ServerSocket manSocket;
	// The port number for the administration socket. 01/20/2016, Bing Li
	private int manPort;

	// The list keeps all of the threads that listen to connecting from clients of the server. When the server is shutdown, those threads can be killed to avoid possible missing. 08/10/2014, Bing Li
	private List<Runner<MyServerListener, MyServerListenerDisposer>> listenerRunnerList;
	
	// Declare one runner for the administration. Since the load is lower, it is not necessary to initialize multiple threads to listen to potential connections. 01/20/2016, Bing Li
	private Runner<ManServerListener, ManServerListenerDisposer> manListenerRunner;

	/*
	 * A singleton is designed for the server's startup and shutdown interface. 08/10/2014, Bing Li
	 */
	private Server()
	{
	}
	
	private static Server instance = new Server();
	
	public static Server FREE()
	{
		if (instance == null)
		{
			instance = new Server();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Start the server and relevant listeners with concurrent threads for potential busy connecting. 08/10/2014, Bing Li
	 */
	public void start(int myPort, int manPort)
	{
		// Initialize the shared thread pool for server listeners. 02/27/2016, Bing Li
		SharedThreadPool.SHARED().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);
		
		// Initialize and start the server sockets. 08/10/2014, Bing Li
		this.myPort = myPort;
		try
		{
			this.mySocket = new ServerSocket(this.myPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// Initialize and start the admin server sockets. 01/20/2016, Bing Li
		this.manPort = manPort;
		try
		{
			this.manSocket = new ServerSocket(this.manPort);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		// Initialize a disposer which collects the server listener. 08/10/2014, Bing Li
		MyServerListenerDisposer disposer = new MyServerListenerDisposer();
		// Initialize the runner list. 11/25/2014, Bing Li
		this.listenerRunnerList = new ArrayList<Runner<MyServerListener, MyServerListenerDisposer>>();
		
		// Start up the threads to listen to connecting from clients which send requests as well as receive notifications. 08/10/2014, Bing Li
		Runner<MyServerListener, MyServerListenerDisposer> runner;
		for (int i = 0; i < ServerConfig.LISTENING_THREAD_COUNT; i++)
		{
//			runner = new Runner<MyServerListener, MyServerListenerDisposer>(new MyServerListener(this.mySocket, ServerConfig.LISTENER_THREAD_POOL_SIZE, ServerConfig.LISTENER_THREAD_ALIVE_TIME), disposer, true);
			runner = new Runner<MyServerListener, MyServerListenerDisposer>(new MyServerListener(this.mySocket, SharedThreadPool.SHARED().getPool()), disposer, true);
			this.listenerRunnerList.add(runner);
			runner.start();
		}
		
		// Initialize a disposer which collects the administration listener. 01/20/2016, Bing Li
		ManServerListenerDisposer manDisposer = new ManServerListenerDisposer();
		// Initialize the runner to listen to connecting from the administrator which sends and notifications to the coordinator. 01/20/2016, Bing Li
//		this.manListenerRunner = new Runner<ManServerListener, ManServerListenerDisposer>(new ManServerListener(this.manSocket, ServerConfig.LISTENER_THREAD_POOL_SIZE, ServerConfig.LISTENER_THREAD_ALIVE_TIME), manDisposer, true);
		this.manListenerRunner = new Runner<ManServerListener, ManServerListenerDisposer>(new ManServerListener(this.manSocket, SharedThreadPool.SHARED().getPool()), manDisposer, true);
		// Start up the runner. 01/20/2016, Bing Li
		this.manListenerRunner.start();

		// Initialize the ServerStatus to keep the nodes' status in the distributed system. 02/06/2016, Bing Li
		ServerStatus.FREE().init(AdminConfig.getServerIDs());
		
		// Initialize the scheduler to do something periodical. 02/02/2016, Bing Li
		Scheduler.GREATFREE().init(ServerConfig.SCHEDULER_POOL_SIZE, ServerConfig.SCHEDULER_KEEP_ALIVE_TIME);

		// Initialize the server IO registry. 11/07/2014, Bing Li
		MyServerIORegistry.REGISTRY().init();
		// Initialize the administrator server IO registry. 01/20/2016, Bing Li
		ManIORegistry.REGISTRY().init();
		// Initialize a client pool, which is used by the server to connect to the remote end. 09/17/2014, Bing Li
		ClientPool.SERVER().init();
		
		// Initialize the message producer to dispatcher messages. 11/23/2014, Bing Li
		MyServerMessageProducer.SERVER().init();
		
//		ServerStatus.FREE().init(AdminConfig.SERVER_ID);
	}

	/*
	 * Shutdown the server. 08/10/2014, Bing Li
	 */
	public void stop() throws IOException, InterruptedException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();
		// Close the socket for the server. 08/10/2014, Bing Li
		this.mySocket.close();
		// Close the socket for the administrator server. 01/20/2016, Bing Li
		this.manSocket.close();
		
		// Stop all of the threads that listen to clients' connecting to the server. 08/10/2014, Bing Li
		for (Runner<MyServerListener, MyServerListenerDisposer> runner : this.listenerRunnerList)
		{
			runner.stop();
		}
		
		// Stop the administration runner. 01/20/2016, Bing Li
		this.manListenerRunner.stop();
		
		// Shutdown the scheduler. 02/02/2016, Bing Li
		Scheduler.GREATFREE().shutdown();

		// Dispose the message producer. 11/23/2014, Bing Li
		MyServerMessageProducer.SERVER().dispose();
		
		// Shutdown the IO registry. 11/07/2014, Bing Li
		MyServerIORegistry.REGISTRY().dispose();
		
		// Shutdown the administration IO registry. 01/20/2016, Bing Li
		ManIORegistry.REGISTRY().dispose();
		
		// Shut down the client pool. 09/17/2014, Bing Li
		ClientPool.SERVER().dispose();
		
		// Shutdown the SharedThreadPool. 02/27/2016, Bing Li
		SharedThreadPool.SHARED().dispose();
	}
}

package com.greatfree.testing.cluster.coordinator.admin;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.greatfree.concurrency.ThreadPool;
import com.greatfree.remote.ServerListener;
import com.greatfree.testing.data.Prompts;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.util.ServerStatus;

/*
 * This is a coordinator listener that not only responds to the administrator. Since the administrator is controlled by a human, it is not necessary to consider so complicated as follows. The reason to do that as below is to keep the style consistent. And, the implementation does not affect the coordinator in terms of much additional resources. 11/27/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class AdminListener extends ServerListener implements Runnable
{
	/*
	 * Initialize the listener. 11/27/2014, Bing Li
	 */
	public AdminListener(ServerSocket serverSocket, ThreadPool pool)
	{
		super(serverSocket, pool);
	}

	/*
	 * Shutdown the listener. 11/27/2014, Bing Li
	 */
	public void shutdown()
	{
		super.shutdown();
	}

	/*
	 * The task that must be executed concurrently. 11/27/2014, Bing Li
	 */
	@Override
	public void run()
	{
		Socket clientSocket;
		AdminIO serverIO;

		// Detect whether the listener is shutdown. If not, it must be running all the time to wait for potential connections from crawlers. 11/27/2014, Bing Li
		while (!super.isShutdown())
		{
			try
			{
				// Wait and accept a connecting from a possible crawler. 11/27/2014, Bing Li
				clientSocket = super.accept();
				// Check whether the connected server IOs exceed the upper limit. 11/27/2014, Bing Li
				if (AdminIORegistry.REGISTRY().getIOCount() >= ServerConfig.MAX_SERVER_IO_COUNT)
				{
					try
					{
						// If the upper limit is reached, the listener has to wait until an existing server IO is disposed. 11/25/2014, Bing Li
						super.holdOn();
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

				// If the upper limit of IOs is not reached, an administrator server IO is initialized. A common Collaborator and the socket are the initial parameters. The shared common collaborator guarantees all of the administrator server IOs from a certain crawler could notify with each other with the same lock. Then, the upper limit of administrator server IOs is under the control. 11/27/2014, Bing Li
				serverIO = new AdminIO(clientSocket, super.getCollaborator(), ServerConfig.ADMIN_PORT);
				// Add the new created server IO into the registry for further management. 11/27/2014, Bing Li
				AdminIORegistry.REGISTRY().addIO(serverIO);
				// Execute the new created administration IO concurrently to respond the crawlers requests and notifications in an asynchronous manner. 11/27/2014, Bing Li
				super.execute(serverIO);
			}
			catch (IOException e)
			{
				ServerStatus.FREE().printException(Prompts.SOCKET_GOT_EXCEPTION);
			}
		}
	}

}

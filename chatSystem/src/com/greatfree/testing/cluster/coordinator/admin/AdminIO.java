package com.greatfree.testing.cluster.coordinator.admin;

import java.io.IOException;
import java.net.Socket;

import com.greatfree.concurrency.Sync;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.remote.ServerIO;
import com.greatfree.testing.cluster.coordinator.CoordinatorMessageProducer;

/*
 * The class is actually an implementation of ServerIO, which serves for the administrator which interacts with the coordinator. 11/27/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class AdminIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the administrator and the coordinator. The collaborator is shared with other IOs to control the count of ServerIOs instances. 11/27/2014, Bing Li
	 */
	public AdminIO(Socket clientSocket, Sync collaborator, int remoteServerPort)
	{
		super(clientSocket, collaborator, remoteServerPort);
	}

	/*
	 * A concurrent method to respond the received messages asynchronously. 11/27/2014, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			// Wait and read messages from the administrator. 11/27/2014, Bing Li
			try
			{
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 11/27/2014, Bing Li
				CoordinatorMessageProducer.SERVER().produceAdminMessage(new OutMessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
			}
			catch (ClassNotFoundException | IOException e)
			{
				try
				{
					AdminIORegistry.REGISTRY().removeIO(this);
				}
				catch (IOException | InterruptedException e1)
				{
				}
				return;
			}
		}
	}
}

package com.greatfree.testing.server;

import java.io.IOException;
import java.net.Socket;

import com.greatfree.concurrency.Sync;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.remote.ServerIO;

/*
 * The class is actually an implementation of ServerIO, which serves for the administrator which interacts with the server. 01/20/2016, Bing Li
 */

// Created: 01/20/2016, Bing Li
public class ManIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the administrator and the server. The server is shared with other IOs to control the count of ServerIOs instances. 01/20/2016, Bing Li
	 */
	public ManIO(Socket clientSocket, Sync collaborator, int remoteServerPort)
	{
		super(clientSocket, collaborator, remoteServerPort);
	}

	/*
	 * A concurrent method to respond the received messages asynchronously. 01/20/2016, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			// Wait and read messages from a client. 01/20/2016, Bing Li
			try
			{
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 01/20/2016, Bing Li
				MyServerMessageProducer.SERVER().produceMessage(new OutMessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
			}
			catch (ClassNotFoundException | IOException e)
			{
				try
				{
					// Remove and dispose the ServerIO. 01/20/2016, Bing Li
					ManIORegistry.REGISTRY().removeIO(this);
				}
				catch (IOException | InterruptedException ex)
				{
				}
				return;
			}
		}
	}
}

package com.greatfree.testing.server;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import com.greatfree.concurrency.Sync;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.ClientPool;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.remote.ServerIO;
import com.greatfree.testing.admin.AdminConfig;
import com.greatfree.util.ServerStatus;

/*
 * The class is actually an implementation of ServerIO, which serves for clients which access the server. 07/30/2014, Bing Li
 */

// Created: 08/10/2014, Bing Li
public class MyServerIO extends ServerIO
{
	/*
	 * Initialize the server IO. The socket is the connection between the client and the server. The collaborator is shared with other IOs to control the count of ServerIOs instances. 11/23/2014, Bing Li
	 */
	public MyServerIO(Socket clientSocket, Sync collaborator, int remoteServerPort)
	{
		super(clientSocket, collaborator, remoteServerPort);
	}

	/*
	 * A concurrent method to respond the received messages asynchronously. 08/22/2014, Bing Li
	 */
	public void run()
	{
		ServerMessage message;
		while (!super.isShutdown())
		{
			try
			{
//				System.out.println("1) MyServerIO: ServerIO count = " + MyServerIORegistry.REGISTRY().getIOCount());
				// Wait and read messages from a client. 08/22/2014, Bing Li
				message = (ServerMessage)super.read();
				// Convert the received message to OutMessageStream and put it into the relevant dispatcher for concurrent processing. 09/20/2014, Bing Li
				MyServerMessageProducer.SERVER().produceMessage(new OutMessageStream<ServerMessage>(super.getOutStream(), super.getLock(), message));
			}
			catch (SocketException e)
			{
//				e.printStackTrace();
				// If the remote node is not shutdown, it indicates that the remote node disconnects one connection and the exception is raised. Then, relevant management tasks need to be accomplished. 02/06/2016, Bing Li 
				if (!ServerStatus.FREE().isServerDown(AdminConfig.CLIENT_ID))
				{
					try
					{
						// Remove the instance of the shutdown ServerIO. 02/06/2016, Bing Li
						MyServerIORegistry.REGISTRY().removeIO(this);
						// Remote the client from the pool. 02/20/2016, Bing Li
						ClientPool.SERVER().getPool().removeClient(this.getRemoteServerKey());
					}
					catch (IOException | InterruptedException e1)
					{
//						e1.printStackTrace();
					}
					// Print a concise prompt on the client screen. 02/06/2016, Bing Li
//					System.out.println(Prompts.SERVER_IO_SHUTDOWN);
				}
				// Exist the loop such that the instance is collected. 02/06/2016, Bing Li
//				System.out.println("2) MyServerIO: ServerIO count = " + MyServerIORegistry.REGISTRY().getIOCount());
				return;
			}
			catch (IOException e)
			{
//				e.printStackTrace();
				// If the remote node is not shutdown, it indicates that the remote node disconnects one connection and the exception is raised. Then, relevant management tasks need to be accomplished. 02/06/2016, Bing Li 
				if (!ServerStatus.FREE().isServerDown(AdminConfig.CLIENT_ID))
				{
					try
					{
						// Remove the instance of the shutdown ServerIO. 02/06/2016, Bing Li
						MyServerIORegistry.REGISTRY().removeIO(this);
						// Remote the client from the pool. 02/20/2016, Bing Li
						ClientPool.SERVER().getPool().removeClient(this.getRemoteServerKey());
					}
					catch (IOException | InterruptedException e1)
					{
//						e1.printStackTrace();
					}
					// Print a concise prompt on the client screen. 02/06/2016, Bing Li
//					System.out.println(Prompts.SERVER_IO_SHUTDOWN);
				}
				// Exist the loop such that the instance is collected. 02/06/2016, Bing Li
				System.out.println("3) MyServerIO: ServerIO count = " + MyServerIORegistry.REGISTRY().getIOCount());
				return;
			}
			catch (ClassNotFoundException e)
			{
//				e.printStackTrace();
				// If the remote node is not shutdown, it indicates that the remote node disconnects one connection and the exception is raised. Then, relevant management tasks need to be accomplished. 02/06/2016, Bing Li 
				if (!ServerStatus.FREE().isServerDown(AdminConfig.CLIENT_ID))
				{
					try
					{
						// Remove the instance of the shutdown ServerIO. 02/06/2016, Bing Li
						MyServerIORegistry.REGISTRY().removeIO(this);
						// Remote the client from the pool. 02/20/2016, Bing Li
						ClientPool.SERVER().getPool().removeClient(this.getRemoteServerKey());
					}
					catch (IOException | InterruptedException e1)
					{
						// Print a concise prompt on the client screen. 02/06/2016, Bing Li
///						e1.printStackTrace();
					}
					// Print a concise prompt on the client screen. 02/06/2016, Bing Li
//					System.out.println(Prompts.SERVER_IO_SHUTDOWN);
				}
				// Exist the loop such that the instance is collected. 02/06/2016, Bing Li
//				System.out.println("4) MyServerIO: ServerIO count = " + MyServerIORegistry.REGISTRY().getIOCount());
				return;
			}
		}
	}
}

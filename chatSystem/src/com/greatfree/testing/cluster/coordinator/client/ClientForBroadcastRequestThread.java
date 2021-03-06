package com.greatfree.testing.cluster.coordinator.client;

import java.io.IOException;

import com.greatfree.concurrency.RequestQueue;
import com.greatfree.testing.cluster.coordinator.CoordinatorMulticastReader;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.ClientForBroadcastRequest;
import com.greatfree.testing.message.ClientForBroadcastResponse;
import com.greatfree.testing.message.ClientForBroadcastStream;
import com.greatfree.util.UtilConfig;

/*
 * The thread derives the RequestQueue. It receives the request of ClientForBroadcastRequest and responds to users with the response of ClientForBroadcastResponse. 02/15/2016, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForBroadcastRequestThread extends RequestQueue<ClientForBroadcastRequest, ClientForBroadcastStream, ClientForBroadcastResponse>
{
	/*
	 * Initialize the thread of request queue. The value of maxTaskSize is the length of the queue to take the count of requests. 02/15/2016, Bing Li
	 */
	public ClientForBroadcastRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}

	/*
	 * Respond users' requests concurrently. 02/15/2015, Bing Li
	 */
	public void run()
	{
		// Declare the request stream. 02/15/2015, Bing Li
		ClientForBroadcastStream request;
		// Declare the response. 02/15/2014, Bing Li
		ClientForBroadcastResponse response;
		// Declare the result from the cluster. 11/25/2016, Bing Li
		String result = UtilConfig.EMPTY_STRING;
		// The thread is shutdown when it is idle long enough or when the server is shut down. Before that, the thread keeps alive. It is necessary to detect whether it is time to end the task. 02/15/2014, Bing Li
		while (!this.isShutdown())
		{
			// The loop detects whether the queue is empty or not. 02/15/2016, Bing Li
			while (!this.isEmpty())
			{
				// Dequeue a request. 02/15/2016, Bing Li
				request = this.getRequest();
				
				// Broadcasting request is performed here ...
				result = CoordinatorMulticastReader.COORDINATE().broadcastRequest(request.getMessage().getMessage());
				
				// Initialize an instance of ClientForBroadcastResponse. 02/15/2016, Bing Li
				response = new ClientForBroadcastResponse(result);
				
				try
				{
					// Respond the response to the remote client. 02/15/2016, Bing Li
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				// Dispose the messages after the responding is performed. 02/15/2016, Bing Li
				this.disposeMessage(request, response);
			}
			try
			{
				// Wait for some time when the queue is empty. During the period and before the thread is killed, some new requests might be received. If so, the thread can keep working. 02/15/2016, Bing Li
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}

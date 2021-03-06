package com.greatfree.testing.cluster.coordinator.client;

import com.greatfree.concurrency.RequestThreadCreatable;
import com.greatfree.testing.message.ClientForBroadcastRequest;
import com.greatfree.testing.message.ClientForBroadcastResponse;
import com.greatfree.testing.message.ClientForBroadcastStream;

/*
 * This is a class that creates the instance of ClientForBroadcastRequestThread. It is used by the RequestDispatcher to create the instances in a high-performance and low-cost manner. 02/15/2016, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForBroadcastRequestThreadCreator implements RequestThreadCreatable<ClientForBroadcastRequest, ClientForBroadcastStream, ClientForBroadcastResponse, ClientForBroadcastRequestThread>
{

	@Override
	public ClientForBroadcastRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ClientForBroadcastRequestThread(taskSize);
	}

}

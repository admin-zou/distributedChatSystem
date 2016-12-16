package com.greatfree.testing.cluster.coordinator.client;

import com.greatfree.concurrency.RequestThreadCreatable;
import com.greatfree.testing.message.ClientForUnicastRequest;
import com.greatfree.testing.message.ClientForUnicastResponse;
import com.greatfree.testing.message.ClientForUnicastStream;

/*
 * This is a class that creates the instance of ClientForUnicastRequestThread. It is used by the RequestDispatcher to create the instances in a high-performance and low-cost manner. 02/15/2016, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForUnicastRequestThreadCreator implements RequestThreadCreatable<ClientForUnicastRequest, ClientForUnicastStream, ClientForUnicastResponse, ClientForUnicastRequestThread>

{

	@Override
	public ClientForUnicastRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ClientForUnicastRequestThread(taskSize);
	}

}

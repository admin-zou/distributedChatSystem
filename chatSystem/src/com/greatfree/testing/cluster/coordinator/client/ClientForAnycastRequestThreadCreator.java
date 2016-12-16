package com.greatfree.testing.cluster.coordinator.client;

import com.greatfree.concurrency.RequestThreadCreatable;
import com.greatfree.testing.message.ClientForAnycastRequest;
import com.greatfree.testing.message.ClientForAnycastResponse;
import com.greatfree.testing.message.ClientForAnycastStream;

/*
 * This is a class that creates the instance of ClientForUnicastRequestThread. It is used by the RequestDispatcher to create the instances in a high-performance and low-cost manner. 02/15/2016, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientForAnycastRequestThreadCreator implements RequestThreadCreatable<ClientForAnycastRequest, ClientForAnycastStream, ClientForAnycastResponse, ClientForAnycastRequestThread>
{

	@Override
	public ClientForAnycastRequestThread createRequestThreadInstance(int taskSize)
	{
		return new ClientForAnycastRequestThread(taskSize);
	}

}

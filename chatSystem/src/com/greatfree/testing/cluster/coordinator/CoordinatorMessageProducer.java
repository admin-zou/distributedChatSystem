package com.greatfree.testing.cluster.coordinator;

import com.greatfree.concurrency.MessageProducer;
import com.greatfree.concurrency.Runner;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.testing.cluster.coordinator.admin.AdminServerDispatcher;
import com.greatfree.testing.cluster.coordinator.admin.AdminServerProducerDisposer;
import com.greatfree.testing.cluster.coordinator.client.ClientServerDispatcher;
import com.greatfree.testing.cluster.coordinator.client.ClientServerProducerDisposer;
import com.greatfree.testing.cluster.coordinator.dn.DNServerDispatcher;
import com.greatfree.testing.cluster.coordinator.dn.DNServerProducerDisposer;
import com.greatfree.testing.data.ServerConfig;

/*
 * The class is a singleton to enclose the instances of MessageProducer. Each of the enclosed message producers serves for one particular client, that connects to a respective port on the coordinator. Usually, each port aims to provide one particular service. 11/24/2014, Bing Li
 * 
 * The class is a wrapper that encloses all of the asynchronous message producers. It is responsible for assigning received messages to the corresponding producer in an asynchronous way. 08/22/2014, Bing Li
 */

// Created: 11/19/2016, Bing Li
public class CoordinatorMessageProducer
{
	// The Threader aims to associate with the client message producer to guarantee the producer can work concurrently. 11/24/2014, Bing Li
	private Runner<MessageProducer<ClientServerDispatcher>, ClientServerProducerDisposer> clientProducerThreader;
	// The Threader aims to associate with the DN server message producer to guarantee the producer can work concurrently. 11/28/2014, Bing Li
	private Runner<MessageProducer<DNServerDispatcher>, DNServerProducerDisposer> dnProducerThreader;
	// The Threader aims to associate with the administration message producer to guarantee the producer can work concurrently. 11/27/2014, Bing Li
	private Runner<MessageProducer<AdminServerDispatcher>, AdminServerProducerDisposer> adminProducerThreader;

	private CoordinatorMessageProducer()
	{
	}

	/*
	 * The class is required to be a singleton since it is nonsense to initiate it for the producers are unique. 11/24/2014, Bing Li
	 */
	private static CoordinatorMessageProducer instance = new CoordinatorMessageProducer();
	
	public static CoordinatorMessageProducer SERVER()
	{
		if (instance == null)
		{
			instance = new CoordinatorMessageProducer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the producers when the process of the coordinator is shutdown. 11/24/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		this.clientProducerThreader.stop();
		this.dnProducerThreader.stop();
		this.adminProducerThreader.stop();
	}
	
	/*
	 * Initialize the message producers. It is invoked when the connection modules of the coordinator is started since clients can send requests or notifications only after it is started. 11/24/2014, Bing Li
	 */
	public void init()
	{
		// Initialize the client message producer. A threader is associated with the crawling message producer such that the producer is able to work in a concurrent way. 11/24/2014, Bing Li
		this.clientProducerThreader = new Runner<MessageProducer<ClientServerDispatcher>, ClientServerProducerDisposer>(new MessageProducer<ClientServerDispatcher>(new ClientServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME)), new ClientServerProducerDisposer());
		// Start the associated thread for the client message producer. 11/24/2014, Bing Li
		this.clientProducerThreader.start();

		// Initialize the client message producer. A threader is associated with the crawling message producer such that the producer is able to work in a concurrent way. 11/24/2014, Bing Li
		this.dnProducerThreader = new Runner<MessageProducer<DNServerDispatcher>, DNServerProducerDisposer>(new MessageProducer<DNServerDispatcher>(new DNServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME)), new DNServerProducerDisposer());
		// Start the associated thread for the client message producer. 11/24/2014, Bing Li
		this.dnProducerThreader.start();

		// Initialize the administration message producer. A threader is associated with the administration message producer such that the producer is able to work in a concurrent way. 11/27/2014, Bing Li
		this.adminProducerThreader = new Runner<MessageProducer<AdminServerDispatcher>, AdminServerProducerDisposer>(new MessageProducer<AdminServerDispatcher>(new AdminServerDispatcher(ServerConfig.DISPATCHER_POOL_SIZE, ServerConfig.DISPATCHER_POOL_THREAD_POOL_ALIVE_TIME)), new AdminServerProducerDisposer());
		// Start the associated thread for the administration message producer. 11/27/2014, Bing Li
		this.adminProducerThreader.start();
	}

	/*
	 * Assign client messages, requests or notifications, to the bound client message dispatcher such that they can be responded or dealt with. 11/24/2014, Bing Li
	 */
	public void produceClientMessage(OutMessageStream<ServerMessage> message)
	{
		this.clientProducerThreader.getFunction().produce(message);
	}

	/*
	 * Assign memory server messages, requests or notifications, to the bound DN server message dispatcher such that they can be responded or dealt with. 11/28/2014, Bing Li
	 */
	public void produceDNMessage(OutMessageStream<ServerMessage> message)
	{
		this.dnProducerThreader.getFunction().produce(message);
	}

	/*
	 * Assign administration messages, requests or notifications, to the bound administration message dispatcher such that they can be responded or dealt with. 11/27/2014, Bing Li
	 */
	public void produceAdminMessage(OutMessageStream<ServerMessage> message)
	{
		this.adminProducerThreader.getFunction().produce(message);
	}
}

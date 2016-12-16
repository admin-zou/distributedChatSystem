package com.greatfree.testing.server;

import com.greatfree.concurrency.RequestThreadCreatable;
import com.greatfree.testing.message.WeatherRequest;
import com.greatfree.testing.message.WeatherResponse;
import com.greatfree.testing.message.WeatherStream;

/*
 * This is a class that creates the instance of WeatherThread. It is used by the RequestDispatcher to create the instances in a high-performance and low-cost manner. 02/15/2016, Bing Li
 */

// Created: 02/15/2016, Bing Li
public class WeatherThreadCreator implements RequestThreadCreatable<WeatherRequest, WeatherStream, WeatherResponse, WeatherThread>
{
	@Override
	public WeatherThread createRequestThreadInstance(int taskSize)
	{
		return new WeatherThread(taskSize);
	}
}

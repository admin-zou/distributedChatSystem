package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

// Created: 02/15/2016, Bing Li
public class WeatherRequest extends ServerMessage
{
	private static final long serialVersionUID = 2535049530104518280L;

	/*
	 * Initialize the request. 02/15/2016, Bing Li
	 */
	public WeatherRequest()
	{
		super(MessageType.WEATHER_REQUEST);
	}
}

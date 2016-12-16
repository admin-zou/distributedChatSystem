package com.greatfree.testing.server;

import com.greatfree.concurrency.NotificationThreadCreatable;
import com.greatfree.testing.message.WeatherNotification;

/*
 * The class creates an instance of the thread, SetWeatherThread. It is used by the NotificationDispatcher to manage the thread count and relevant resources. 02/15/2016, Bing Li
 */

// Created: 02/15/2016, Bing Li
public class SetWeatherThreadCreator implements NotificationThreadCreatable<WeatherNotification, SetWeatherThread>
{
	@Override
	public SetWeatherThread createNotificationThreadInstance(int taskSize)
	{
		return new SetWeatherThread(taskSize);
	}
}

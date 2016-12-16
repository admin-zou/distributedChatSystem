package com.greatfree.testing.server;

import java.util.Calendar;

import com.greatfree.concurrency.NotificationDispatcher;
import com.greatfree.concurrency.RequestDispatcher;
import com.greatfree.concurrency.Scheduler;
import com.greatfree.concurrency.ServerMessageDispatcher;
import com.greatfree.message.InitReadNotification;
import com.greatfree.message.SystemMessageType;
import com.greatfree.multicast.ServerMessage;
import com.greatfree.remote.OutMessageStream;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.MessageType;
import com.greatfree.testing.message.RegisterClientNotification;
import com.greatfree.testing.message.ShutdownServerNotification;
import com.greatfree.testing.message.TestNotification; //zou
import com.greatfree.testing.message.TestRequest;
import com.greatfree.testing.message.TestResponse;
import com.greatfree.testing.message.TestStream;
import com.greatfree.testing.message.SignUpRequest;
import com.greatfree.testing.message.SignUpResponse;
import com.greatfree.testing.message.SignUpStream;
import com.greatfree.testing.message.WeatherNotification;
import com.greatfree.testing.message.WeatherRequest;
import com.greatfree.testing.message.WeatherResponse;
import com.greatfree.testing.message.WeatherStream;

/*
 * This is an implementation of ServerMessageDispatcher. It contains the concurrency mechanism to respond clients' requests and receive clients' notifications for the server. 09/20/2014, Bing Li
 */

/*
 * Revision Log
 * 
 * The initialization request dispatcher is modified. When no tasks are available for some time, it needs to be shut down. 01/14/2016, Bing Li
 * 
 */

// Created: 09/20/2014, Bing Li
public class MyServerDispatcher extends ServerMessageDispatcher<ServerMessage>
{
	// Declare a notification dispatcher to process the registration notification concurrently. 11/04/2014, Bing Li
	private NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator> registerClientNotificationDispatcher;
	// Declare a request dispatcher to respond users sign-up requests concurrently. 11/04/2014, Bing Li
	private RequestDispatcher<SignUpRequest, SignUpStream, SignUpResponse, SignUpThread, SignUpThreadCreator> signUpRequestDispatcher;
	// Declare a notification dispatcher to set the value of Weather when an instance of WeatherNotification is received. 02/15/2016, Bing Li
	private NotificationDispatcher<WeatherNotification, SetWeatherThread, SetWeatherThreadCreator> setWeatherNotificationDispatcher;
	// Declare a request dispatcher to respond an instance of WeatherResponse to the relevant remote client when an instance of WeatherReques is received. 02/15/2016, Bing Li
	private RequestDispatcher<WeatherRequest, WeatherStream, WeatherResponse, WeatherThread, WeatherThreadCreator> weatherRequestDispatcher;
	// Declare a notification dispatcher to deal with instances of InitReadNotification from a client concurrently such that the client can initialize its ObjectInputStream. 11/09/2014, Bing Li
	private NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator> initReadFeedbackNotificationDispatcher;
	// Declare a notification dispatcher to shutdown the server when such a notification is received. 02/15/2016, Bing Li
	private NotificationDispatcher<ShutdownServerNotification, ShutdownThread, ShutdownThreadCreator> shutdownNotificationDispatcher;

	private NotificationDispatcher<TestNotification,TestNotificationThread,TestNotificationThreadCreator> TestNotificationDispatcher;
	
	
	private RequestDispatcher<TestRequest, TestStream, TestResponse, TestRequestThread, TestRequestThreadCreator> TestRequestDispatcher;  //zou
	
	/*
	 * Initialize. 09/20/2014, Bing Li
	 */
	public MyServerDispatcher(int corePoolSize, long keepAliveTime)
	{
		// Set the pool size and threads' alive time. 11/04/2014, Bing Li
		super(corePoolSize, keepAliveTime);

		// Initialize the client registration notification dispatcher. 11/30/2014, Bing Li
		this.registerClientNotificationDispatcher = new NotificationDispatcher<RegisterClientNotification, RegisterClientThread, RegisterClientThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new RegisterClientThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
		
		// Initialize the sign up request dispatcher. 11/04/2014, Bing Li
		this.signUpRequestDispatcher = new RequestDispatcher<SignUpRequest, SignUpStream, SignUpResponse, SignUpThread, SignUpThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new SignUpThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the weather notification dispatcher. 02/15/2016, Bing Li
		this.setWeatherNotificationDispatcher = new NotificationDispatcher<WeatherNotification, SetWeatherThread, SetWeatherThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new SetWeatherThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the sign up request dispatcher. 11/04/2014, Bing Li
		this.weatherRequestDispatcher = new RequestDispatcher<WeatherRequest, WeatherStream, WeatherResponse, WeatherThread, WeatherThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new WeatherThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());

		// Initialize the read initialization notification dispatcher. 11/30/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher = new NotificationDispatcher<InitReadNotification, InitReadFeedbackThread, InitReadFeedbackThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new InitReadFeedbackThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
		
		// Initialize the shutdown notification dispatcher. 11/30/2014, Bing Li
		this.shutdownNotificationDispatcher = new NotificationDispatcher<ShutdownServerNotification, ShutdownThread, ShutdownThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new ShutdownThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	
		//zou Initialize the Test notification dispatcher
		this.TestNotificationDispatcher = new NotificationDispatcher<TestNotification, TestNotificationThread, TestNotificationThreadCreator>(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME, new TestNotificationThreadCreator(), ServerConfig.MAX_NOTIFICATION_TASK_SIZE, ServerConfig.MAX_NOTIFICATION_THREAD_SIZE, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME, ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
		
		//zou
		this.TestRequestDispatcher = new RequestDispatcher<TestRequest, TestStream,TestResponse,TestRequestThread,TestRequestThreadCreator>(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE, ServerConfig.REQUEST_DISPATCHER_THREAD_ALIVE_TIME, new TestRequestThreadCreator(), ServerConfig.MAX_REQUEST_TASK_SIZE, ServerConfig.MAX_REQUEST_THREAD_SIZE, ServerConfig.REQUEST_DISPATCHER_WAIT_TIME, ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY, ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD, Scheduler.GREATFREE().getSchedulerPool());
	}

	/*
	 * Shut down the server message dispatcher. 09/20/2014, Bing Li
	 */
	public void shutdown() throws InterruptedException
	{
		// Dispose the register dispatcher. 01/14/2016, Bing Li
		this.registerClientNotificationDispatcher.dispose();
		// Dispose the sign-up dispatcher. 11/04/2014, Bing Li
		this.signUpRequestDispatcher.dispose();
		// Dispose the weather notification dispatcher. 02/15/2016, Bing Li
		this.setWeatherNotificationDispatcher.dispose();
		// Dispose the weather request dispatcher. 02/15/2016, Bing Li
		this.weatherRequestDispatcher.dispose();
		// Dispose the dispatcher for initializing reading feedback. 11/09/2014, Bing Li
		this.initReadFeedbackNotificationDispatcher.dispose();
		// Dispose the dispatcher for shutdown. 11/09/2014, Bing Li
		this.shutdownNotificationDispatcher.dispose();
		// Shutdown the derived server dispatcher. 11/04/2014, Bing Li
		
		this.TestNotificationDispatcher.dispose(); //zou
		this.TestRequestDispatcher.dispose(); //zou
		
		super.shutdown();
	}
	
	/*
	 * Process the available messages in a concurrent way. 09/20/2014, Bing Li
	 */
	public void consume(OutMessageStream<ServerMessage> message)
	{
		// Check the types of received messages. 11/09/2014, Bing Li
		switch (message.getMessage().getType())
		{
			case MessageType.REGISTER_CLIENT_NOTIFICATION:
				System.out.println("REGISTER_CLIENT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the registry notification dispatcher is ready. 01/14/2016, Bing Li
				if (!this.registerClientNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.registerClientNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 01/14/2016, Bing Li
				this.registerClientNotificationDispatcher.enqueue((RegisterClientNotification)message.getMessage());
				break;
			
			// If the message is the one of sign-up requests. 11/09/2014, Bing Li
			case MessageType.SIGN_UP_REQUEST:
				System.out.println("SIGN_UP_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the sign-up dispatcher is ready. 01/14/2016, Bing Li
				if (!this.signUpRequestDispatcher.isReady())
				{
					// Execute the sign-up dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.signUpRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 11/09/2014, Bing Li
				this.signUpRequestDispatcher.enqueue(new SignUpStream(message.getOutStream(), message.getLock(), (SignUpRequest)message.getMessage()));
				break;

			// If the message is the one of WeatherNotification. 02/15/2016, Bing Li
			case MessageType.WEATHER_NOTIFICATION:
				System.out.println("WEATHER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the weather notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.setWeatherNotificationDispatcher.isReady())
				{
//					System.out.println("Raise setWeatherNotificationDispatcher");
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.setWeatherNotificationDispatcher);
				}
				// Enqueue the instance of WeatherNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.setWeatherNotificationDispatcher.enqueue((WeatherNotification)message.getMessage());
				break;
				
				// If the message is the one of weather requests. 11/09/2014, Bing Li
			case MessageType.WEATHER_REQUEST:
				System.out.println("WEATHER_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the weather request dispatcher is ready. 02/15/2016, Bing Li
				if (!this.weatherRequestDispatcher.isReady())
				{
//					System.out.println("Raise weatherRequestDispatcher");
					// Execute the weather request dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.weatherRequestDispatcher);
				}
				// Enqueue the instance of WeatherRequest into the dispatcher for concurrent responding. 02/15/2016, Bing Li
				this.weatherRequestDispatcher.enqueue(new WeatherStream(message.getOutStream(), message.getLock(), (WeatherRequest)message.getMessage()));
				break;

			// If the message is the one of initializing notification. 11/09/2014, Bing Li
			case SystemMessageType.INIT_READ_NOTIFICATION:
				System.out.println("INIT_READ_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the reading initialization dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.initReadFeedbackNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.initReadFeedbackNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 11/09/2014, Bing Li
				this.initReadFeedbackNotificationDispatcher.enqueue((InitReadNotification)message.getMessage());
				break;
				
			case MessageType.SHUTDOWN_REGULAR_SERVER_NOTIFICATION:
				System.out.println("SHUTDOWN_REGULAR_SERVER_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the shutdown dispatcher is ready or not. 01/14/2016, Bing Li
				if (!this.shutdownNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread. 01/14/2016, Bing Li
					super.execute(this.shutdownNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 11/09/2014, Bing Li
				this.shutdownNotificationDispatcher.enqueue((ShutdownServerNotification)message.getMessage());
				break;
		
			case MessageType.TEST_NOTIFICATION:   //zou
				System.out.println("TEST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.TestNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher as a thread.
					super.execute(this.TestNotificationDispatcher);
				}
				// Enqueue the notification into the dispatcher for concurrent processing. 11/09/2014, Bing Li
				this.TestNotificationDispatcher.enqueue((TestNotification)message.getMessage());
				break;
			case MessageType.TEST_REQUEST:   //zou
				System.out.println("TEST_REQUEST received @" + Calendar.getInstance().getTime());
				if(!this.TestRequestDispatcher.isReady())
				{
					super.execute(this.TestRequestDispatcher);
				}
				this.TestRequestDispatcher.enqueue(new TestStream(message.getOutStream(),message.getLock(),(TestRequest)message.getMessage()));
				break;
		}
	}
}

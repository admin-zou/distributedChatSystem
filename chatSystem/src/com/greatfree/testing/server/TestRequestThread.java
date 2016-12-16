package com.greatfree.testing.server;

import java.io.IOException;

import com.greatfree.concurrency.RequestQueue;
//import com.greatfree.testing.data.FriendList;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.TestRequest;
import com.greatfree.testing.message.TestResponse;
import com.greatfree.testing.message.TestStream;
import com.greatfree.testing.server.resources.FriendListDB;

public class TestRequestThread extends RequestQueue<TestRequest, TestStream, TestResponse>
{
	public TestRequestThread(int maxTaskSize)
	{
		super(maxTaskSize);
	}
	
	public void run()
	{
		TestStream request;
		TestResponse response;
		
		while(!this.isShutdown())
		{
			while(!this.isEmpty())
			{
				request = this.getRequest();
				//System.out.println(request.getMessage().getRequest());
				//response = new TestResponse("response");
				response = new TestResponse(FriendListDB.SERVER().getFriendList());
				try
				{
					this.respond(request.getOutStream(), request.getLock(), response);
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
				this.disposeMessage(request, response);
			}
			
			try
			{
				this.holdOn(ServerConfig.REQUEST_THREAD_WAIT_TIME);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}
}

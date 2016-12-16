package com.greatfree.testing.server;

import com.greatfree.concurrency.RequestThreadCreatable;
import com.greatfree.testing.message.TestRequest;
import com.greatfree.testing.message.TestResponse;
import com.greatfree.testing.message.TestStream; 

public class TestRequestThreadCreator implements RequestThreadCreatable<TestRequest, TestStream, TestResponse, TestRequestThread> 
{
	public TestRequestThread createRequestThreadInstance(int taskSize)
	{
		return new TestRequestThread(taskSize);
	}

}




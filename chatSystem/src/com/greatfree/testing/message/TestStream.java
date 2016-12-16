package com.greatfree.testing.message;

import java.io.ObjectOutputStream;
import java.util.concurrent.locks.Lock;

import com.greatfree.remote.OutMessageStream; 

public class TestStream extends OutMessageStream<TestRequest>
{
	public TestStream(ObjectOutputStream out,Lock lock,TestRequest message)
	{
		super(out,lock,message);
	}
}

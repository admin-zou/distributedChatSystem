package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;

public class TestRequest extends ServerMessage
{
	private static final long serialVersionUID = -8727719427732353149L;
	
	public TestRequest()
	{
		super(MessageType.TEST_REQUEST);
	}
}

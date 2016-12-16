package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.testing.data.FriendList;

public class TestNotification extends ServerMessage 
{
	
	private static final long serialVersionUID=-6936158947185462689L;
	
	private FriendList friendlist;
	
	public TestNotification(FriendList friendlist)
	{
		super(MessageType.TEST_NOTIFICATION);
		this.friendlist = friendlist;
	}
	
	public FriendList getFriendList()
	{
		return this.friendlist;
	}
	
	/*
	private String message;
	
	public TestNotification(String message)
	{
		super(MessageType.TEST_NOTIFICATION);
		this.message = message;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	*/
}


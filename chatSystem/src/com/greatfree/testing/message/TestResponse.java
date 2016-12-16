package com.greatfree.testing.message;

import com.greatfree.multicast.ServerMessage;
import com.greatfree.testing.data.FriendList;

//import java.util.Set;
import java.util.Vector; 

public class TestResponse extends ServerMessage
{
	private static final long serialVersionUID = -4141489337639907434L;
	
	private Vector<FriendList> friends;
	
	public TestResponse(FriendList flist)
	{
		super(MessageType.TEST_RESPONSE);
		this.friends.add(flist);
	}
	
	public TestResponse(Vector<FriendList> friendSet)
	{
		super(MessageType.TEST_RESPONSE);
		this.friends = friendSet;
	}
	
	public Vector<FriendList> getFriendList()
	{
		return this.friends;
	}
	
	/*
	//private String response;
	private FriendList friendlist;
	
	public TestResponse(FriendList flist)
	{
		super(MessageType.TEST_RESPONSE);
		this.friendlist = flist;
	}
	
	public FriendList getFriendList()
	{
		return this.friendlist;
	}
	
	public TestResponse(String response)
	{
		super(MessageType.TEST_RESPONSE);
		this.response = response;
	}
	
	public String getResponse()
	{
		return this.response;
	}
	*/
}

package com.greatfree.testing.data;

import java.io.Serializable;


public class FriendList implements Serializable 
{
	private static final long serialVersionUID = -444619864165469902L;
	
	private String name;
	private String port; //message
	
	public FriendList(String n,String p)
	{
		this.name = n;
		this.port = p;
	}
	
	public void setName(String n)
	{
		this.name = n;
	}
	
	public void stePort(String p)
	{
		this.port = p;
	}
	
	public String getName()
	{
	//	System.out.println("getName");
		return this.name;
	}
	
	public String getMessage()
	{
		return this.port;
	}
}

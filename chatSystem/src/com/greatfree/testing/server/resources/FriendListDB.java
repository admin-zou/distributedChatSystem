package com.greatfree.testing.server.resources;

import com.greatfree.testing.data.FriendList;
   
//import java.util.Set;    
//import java.util.HashSet;
import java.util.Vector;

/*
 * This is a singleton class that keeps the weather information. 02/11/2016, Bing Li
 */

// Created: 02/11/2016, Bing Li
public class FriendListDB
{
	// Declare one instance of Weather. 02/15/2016, Bing Li
	//private Set<FriendList> friends = new HashSet<>();
	private Vector<FriendList> afriends;
	
	/*
	 * A singleton implementation. 02/15/2016, Bing Li
	 */
	private FriendListDB()
	{
	}
	
	private static FriendListDB instance = new FriendListDB();
	
	public static FriendListDB SERVER()
	{
		if (instance == null)
		{
			instance = new FriendListDB();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the instance. 02/15/2016, Bing Li
	 */
	public void dispose()
	{
		//this.friends.clear();
		this.afriends.clear();
	}

	public void addFriend(FriendList list)
	{
		//this.friends.add(list);
		this.afriends.add(list);
	}

	public Vector<FriendList> getFriendList()
	{
		return this.afriends;
	}
	/*
	public Set<FriendList> getFrindList()
	{
		//return this.friends;
	}
	*/
}

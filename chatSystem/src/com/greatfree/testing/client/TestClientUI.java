package com.greatfree.testing.client;

import com.greatfree.testing.message.ClientForBroadcastResponse;
import com.greatfree.testing.message.ClientForUnicastResponse;
import com.greatfree.testing.message.TestResponse;
import com.greatfree.testing.data.Constants;
import com.greatfree.testing.data.FriendList;
import com.greatfree.testing.message.ClientForAnycastResponse;
import java.util.Scanner;
//import java.util.Set;
import java.util.Vector;

public class TestClientUI {
	
	private TestClientUI()
	{}
	
	private static TestClientUI instance = new TestClientUI();
	
	public static TestClientUI FACE()
	{
		if (instance == null)
		{
			instance = new TestClientUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void printMenu()
	{
		System.out.println(TestClientMenu.MENU_HEAD);
		System.out.println(TestClientMenu.NOTIFY_BROADCASTLY);
		System.out.println(TestClientMenu.NOTIFY_UNICASTLY);
		System.out.println(TestClientMenu.NOTIFY_ANYCASTLY);
		System.out.println(TestClientMenu.REQUEST_BROADCASTLY);
		System.out.println(TestClientMenu.REQUEST_UNICASTLY);
		System.out.println(TestClientMenu.REQUEST_ANYCASTLY);
		System.out.println(TestClientMenu.NOTIFY_TEST);
		System.out.println(TestClientMenu.REQUEST_TEST);
		System.out.println(TestClientMenu.QUIT);
		System.out.println(TestClientMenu.MENU_TAIL);
		System.out.println(TestClientMenu.INPUT_PROMPT);
	}
	
	public void send(String NAME,int option)
	{
		ClientForBroadcastResponse broadcastResponse;
		ClientForUnicastResponse unicastResponse;
		ClientForAnycastResponse anycastResponse;
		TestResponse testresponse;
		
		switch(option)
		{
		case TestMenuOptions.NOTIFY_BROADCASTLY:
			ClientEventer.NOTIFY().notifyUnicastly(Constants.CLIENT_FOR_BROADCAST_NOTIFICATION);
			break;
		case TestMenuOptions.NOTIFY_UNICASTLY:
			ClientEventer.NOTIFY().notifyUnicastly(Constants.CLIENT_FOR_UNICAST_NOTIFICATION);
			break;	
		case TestMenuOptions.NOTIFY_ANYCASTLY:
			ClientEventer.NOTIFY().notifyUnicastly(Constants.CLIENT_FOR_ANYCAST_NOTIFICATION);
			break;
		case TestMenuOptions.REQUES_BROADCASTLY:
			broadcastResponse = ClientReader.requestBroadcastly(Constants.CLIENT_FOR_BROADCAST_REQUEST);
			System.out.println("anycastRespose="+broadcastResponse.getMessage());
			break;	
		case TestMenuOptions.REQUES_UNICASTLY:
			unicastResponse = ClientReader.requestUnicastly(Constants.CLIENT_FOR_UNICAST_REQUEST);
			System.out.println("unicastRespos="+unicastResponse.getMessage());
			break;	
		case TestMenuOptions.REQUES_ANYCASTLY:
			anycastResponse = ClientReader.requestAnycastly(Constants.CLIENT_FOR_ANYCAST_REQUEST);
			System.out.println("anycastRespose="+anycastResponse.getMessage());
			break;	
		case TestMenuOptions.NOTIFY_TEST:
			Scanner in = new Scanner(System.in);
			String message;
			System.out.println("Please Input your message:");
			message = in.nextLine();
			ClientEventer.NOTIFY().notifyTest(new FriendList(NAME, message));
			break;
		case TestMenuOptions.REQUEST_TEST:
			testresponse = TestClientReader.getFriendList();
			Vector<FriendList> friends = testresponse.getFriendList();
			//System.out.println("Friend List:"+friends.size());
			for(int i=0;i<friends.size();++i)
			{
				System.out.println(friends.get(i).getName()+":"+friends.get(i).getMessage());
			}
			break;
		case TestMenuOptions.QUIT:
			break;
		}
	}
}

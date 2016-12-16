package com.greatfree.testing.cluster.client;

import com.greatfree.testing.client.ClientEventer;
import com.greatfree.testing.client.ClientReader;
import com.greatfree.testing.data.Constants;
import com.greatfree.testing.message.ClientForAnycastResponse;
import com.greatfree.testing.message.ClientForBroadcastResponse;
import com.greatfree.testing.message.ClientForUnicastResponse;

/*
 * The class aims to print a menu list on the screen for users to interact with the client and communicate with the polling server. The menu is unique in the client such that it is implemented in the pattern of a singleton. 09/21/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class ClientUI
{
	/*
	 * Initialize. 09/21/2014, Bing Li
	 */
	private ClientUI()
	{
	}

	/*
	 * Initialize a singleton. 09/21/2014, Bing Li
	 */
	private static ClientUI instance = new ClientUI();
	
	public static ClientUI FACE()
	{
		if (instance == null)
		{
			instance = new ClientUI();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Print the menu list on the screen. 09/21/2014, Bing Li
	 */
	public void printMenu()
	{
		System.out.println(ClientMenu.MENU_HEAD);
		System.out.println(ClientMenu.NOTIFY_BROADCASTLY);
		System.out.println(ClientMenu.NOTIFY_UNICASTLY);
		System.out.println(ClientMenu.NOTIFY_ANYCASTLY);
		System.out.println(ClientMenu.REQUEST_BROARDCASTLY);
		System.out.println(ClientMenu.REQUEST_UNICASTLY);
		System.out.println(ClientMenu.REQUEST_ANYCASTLY);
		System.out.println(ClientMenu.QUIT);
		System.out.println(ClientMenu.MENU_TAIL);
		System.out.println(ClientMenu.INPUT_PROMPT);
	}

	/*
	 * Send the users' option to the polling server. 09/21/2014, Bing Li
	 */
	public void send(int option)
	{
		ClientForBroadcastResponse broadcastResponse;
		ClientForUnicastResponse unicastResponse;
		ClientForAnycastResponse anycastResponse;
		
		// Check the option to interact with the polling server. 09/21/2014, Bing Li
		switch (option)
		{
			case MenuOptions.NOTIFY_BROADCASTLY:
				ClientEventer.NOTIFY().notifyBroadcastly(Constants.CLIENT_FOR_BROADCAST_NOTIFICATION);
				break;
				
			case MenuOptions.NOTIFY_UNICASTLY:
				ClientEventer.NOTIFY().notifyUnicastly(Constants.CLIENT_FOR_UNICAST_NOTIFICATION);
				break;
				
			case MenuOptions.NOTIFY_ANYCASTLY:
				ClientEventer.NOTIFY().notifyAnycastly(Constants.CLIENT_FOR_ANYCAST_NOTIFICATION);
				break;
				
			case MenuOptions.REQUEST_BROADCASTLY:
				broadcastResponse = ClientReader.requestBroadcastly(Constants.CLIENT_FOR_BROADCAST_REQUEST);
				System.out.println("broadcastResponse = " + broadcastResponse.getMessage());
				break;
				
			case MenuOptions.REQUEST_UNICASTLY:
				unicastResponse = ClientReader.requestUnicastly(Constants.CLIENT_FOR_UNICAST_REQUEST);
				System.out.println("unicastResponse = " + unicastResponse.getMessage());
				break;
				
			case MenuOptions.REQUEST_ANYCASTLY:
				anycastResponse = ClientReader.requestAnycastly(Constants.CLIENT_FOR_ANYCAST_REQUEST);
				System.out.println("anycastResponse = " + anycastResponse.getMessage());
				break;
				
			case MenuOptions.QUIT:
				break;
		}
	}
}

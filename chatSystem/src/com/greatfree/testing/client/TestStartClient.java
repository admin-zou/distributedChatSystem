package com.greatfree.testing.client;

import java.io.IOException;
import java.util.Scanner;

import com.greatfree.testing.client.ClientServer;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.util.TerminateSignal;

public class TestStartClient 
{
	/*
	 * The starting point of the client. 09/21/2014, Bing Li
	 */
	public static void main(String[] args)
	{
		// Start the client server. 11/08/2014, Bing Li
		ClientServer.CLIENT().start(ServerConfig.CLIENT_PORT);
		
		String Myname = args[0]; //args[0] is your name
		
		// Initialize the option which represents a user's intents of operations. 09/21/2014, Bing Li
		int option = TestMenuOptions.NO_OPTION;
		
		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);
		String optionStr;

		// Keep the loop running to interact with users until an end option is selected. 09/21/2014, Bing Li
		while (option != TestMenuOptions.QUIT)
		{
			// Display the menu to users. 09/21/2014, Bing Li
			TestClientUI.FACE().printMenu();
			// Input a string that represents users' intents. 09/21/2014, Bing Li
			optionStr = in.nextLine();
			try
			{
				// Convert the input string to integer. 09/21/2014, Bing Li
				option = Integer.parseInt(optionStr);
				System.out.println("Your choice: " + option);
				// Send the option to the polling server. 09/21/2014, Bing Li
				TestClientUI.FACE().send(Myname,option);
			}
			catch (NumberFormatException e)
			{
				option = TestMenuOptions.NO_OPTION;
				System.out.println(TestClientMenu.WRONG_OPTION);
			}
		}

		// Set the terminating flag to true. 09/21/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();

		try
		{
			// Stop the client server. 11/08/2014, Bing Li
			ClientServer.CLIENT().stop();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		in.close();
	}
}


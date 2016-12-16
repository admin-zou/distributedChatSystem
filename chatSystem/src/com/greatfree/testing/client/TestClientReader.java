package com.greatfree.testing.client;

import java.io.IOException;

import com.greatfree.exceptions.RemoteReadException;
import com.greatfree.remote.RemoteReader;
import com.greatfree.testing.data.ServerConfig;
import com.greatfree.testing.message.ClientForAnycastRequest;
import com.greatfree.testing.message.ClientForAnycastResponse;
import com.greatfree.testing.message.ClientForBroadcastRequest;
import com.greatfree.testing.message.ClientForBroadcastResponse;
import com.greatfree.testing.message.ClientForUnicastRequest;
import com.greatfree.testing.message.ClientForUnicastResponse;
import com.greatfree.testing.message.MessageConfig;
import com.greatfree.testing.message.SignUpRequest;
import com.greatfree.testing.message.SignUpResponse;
import com.greatfree.testing.message.TestRequest;
import com.greatfree.testing.message.TestResponse;
import com.greatfree.util.NodeID;

public class TestClientReader 
{
	public static SignUpResponse signUP(String userName,String password)
	{
		try
		{
			return (SignUpResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT,new SignUpRequest(userName, password)));
		}
		catch(ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return MessageConfig.NO_SIGN_UP_RESPONSE;
	}
	
	public static ClientForBroadcastResponse requestBroadcastly(String message)
	{
		try
		{
			return (ClientForBroadcastResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT, new ClientForBroadcastRequest(message)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			// When the connection gets something wrong, a RemoteReadException and other exceptions must be raised. 09/22/2014, Bing Li
			e.printStackTrace();
		}
		// When reading gets something wrong, a null response is returned. 09/22/2014, Bing Li
		return MessageConfig.NO_CLIENT_FOR_BROADCAST_RESPONSE;
	}

	/*
	 * Send the request of ClientForUnicastRequest to the remote server and wait for the response, ClientForUnicastResponse. 09/22/2014, Bing Li
	 */
	public static ClientForUnicastResponse requestUnicastly(String message)
	{
		try
		{
			return (ClientForUnicastResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT, new ClientForUnicastRequest(message)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			// When the connection gets something wrong, a RemoteReadException and other exceptions must be raised. 09/22/2014, Bing Li
			e.printStackTrace();
		}
		// When reading gets something wrong, a null response is returned. 09/22/2014, Bing Li
		return MessageConfig.NO_CLIENT_FOR_UNICAST_RESPONSE;
	}

	/*
	 * Send the request of ClientForUnicastRequest to the remote server and wait for the response, ClientForUnicastResponse. 09/22/2014, Bing Li
	 */
	public static ClientForAnycastResponse requestAnycastly(String message)
	{
		try
		{
			return (ClientForAnycastResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT, new ClientForAnycastRequest(message)));
		}
		catch (ClassNotFoundException | RemoteReadException | IOException e)
		{
			// When the connection gets something wrong, a RemoteReadException and other exceptions must be raised. 09/22/2014, Bing Li
			e.printStackTrace();
		}
		// When reading gets something wrong, a null response is returned. 09/22/2014, Bing Li
		return MessageConfig.NO_CLIENT_FOR_ANYCAST_RESPONSE;
	}
	
	/*
	public static TestResponse requestTest(String request)
	{
		try
		{
			return (TestResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT,new TestRequest(request)));
		}
		catch(ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return MessageConfig.NO_TEST_RESPONSE;
	}
	*/
	public static TestResponse getFriendList()
	{
		try
		{
			return (TestResponse)(RemoteReader.REMOTE().read(NodeID.DISTRIBUTED().getKey(), ServerConfig.SERVER_IP, ServerConfig.SERVER_PORT,new TestRequest()));
		}
		catch(ClassNotFoundException | RemoteReadException | IOException e)
		{
			e.printStackTrace();
		}
		return MessageConfig.NO_TEST_RESPONSE;
	}
}

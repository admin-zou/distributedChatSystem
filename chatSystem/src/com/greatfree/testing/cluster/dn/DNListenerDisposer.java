package com.greatfree.testing.cluster.dn;

import com.greatfree.reuse.RunDisposable;

/*
 * The class is responsible for disposing the instance of DNListener by invoking its method of shutdown(). It works with the thread container, Threader or Runner. 11/23/2014, Bing Li
 */

// Created: 11/22/2016, Bing Li
public class DNListenerDisposer implements RunDisposable<DNListener>
{
	/*
	 * Dispose the instance of DNListener. 11/23/2014, Bing Li
	 */
	@Override
	public void dispose(DNListener r)
	{
		r.shutdown();
	}

	/*
	 * Dispose the instance of DNListener. The method does not make sense to DNListener. Just leave it here for the requirement of the interface, RunDisposable<DNListener>. 11/23/2014, Bing Li
	 */
	@Override
	public void dispose(DNListener r, long time)
	{
		r.shutdown();
	}

}

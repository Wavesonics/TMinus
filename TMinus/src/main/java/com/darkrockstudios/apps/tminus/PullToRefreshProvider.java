package com.darkrockstudios.apps.tminus;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * Created by Adam on 11/2/13.
 */
public interface PullToRefreshProvider
{
	public PullToRefreshAttacher getPullToRefreshAttacher();
}

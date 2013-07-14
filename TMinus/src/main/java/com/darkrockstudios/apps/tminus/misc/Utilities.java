package com.darkrockstudios.apps.tminus.misc;

import java.util.concurrent.TimeUnit;

/**
 * Created by Adam on 6/30/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class Utilities
{
	public static String getFormattedTime( final long timeMs )
	{
		final long day = TimeUnit.MILLISECONDS.toDays( timeMs );
		final long hr = TimeUnit.MILLISECONDS.toHours( timeMs - TimeUnit.DAYS.toMillis( day ) );
		final long min = TimeUnit.MILLISECONDS.toMinutes( timeMs - TimeUnit.DAYS
		                                                                   .toMillis( day ) - TimeUnit.HOURS
		                                                                                              .toMillis( hr ) );

		String timeStr = day + "d " + hr + "h " + min + "m";
		return timeStr;
	}
}

package com.darkrockstudios.apps.tminus.misc;

import android.content.Context;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Adam on 6/30/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class Utilities
{
	public static final String DATE_FORMAT = "HH:mm - dd MMM yyy";

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

	public static String getStatusText( Launch launch, Context context )
	{
		final String status;
		if( launch != null )
		{
			switch( launch.status )
			{
				case 1:
					status = context.getString( R.string.COUNTDOWN_launch_status_green );
					break;
				case 2:
					status = context.getString( R.string.COUNTDOWN_launch_status_red );
					break;
				case 3:
					status = context.getString( R.string.COUNTDOWN_launch_status_success );
					break;
				case 4:
					status = context.getString( R.string.COUNTDOWN_launch_status_fail );
					break;
				default:
					status = "";
			}
		}
		else
		{
			status = "";
		}

		return status;
	}

	public static String getDateText( Date date )
	{
		final SimpleDateFormat formatter = new SimpleDateFormat( DATE_FORMAT );
		return formatter.format( date );
	}

	// Takes in the ISO 3166-1 alpha-3 country code
	public static int getFlagResource( String countryCode )
	{
		int resourceId = R.drawable.flag_unknown;

		if( countryCode != null && countryCode.trim().length() > 0 )
		{
			if( countryCode.equalsIgnoreCase( "AUS" ) )
			{
				resourceId = R.drawable.flag_au;
			}
			else if( countryCode.equalsIgnoreCase( "BRA" ) )
			{
				resourceId = R.drawable.flag_br;
			}
			else if( countryCode.equalsIgnoreCase( "CHN" ) )
			{
				resourceId = R.drawable.flag_cn;
			}
			else if( countryCode.equalsIgnoreCase( "GBR" ) )
			{
				resourceId = R.drawable.flag_gb;
			}
			else if( countryCode.equalsIgnoreCase( "IND" ) )
			{
				resourceId = R.drawable.flag_in;
			}
			else if( countryCode.equalsIgnoreCase( "JPN" ) )
			{
				resourceId = R.drawable.flag_jp;
			}
			else if( countryCode.equalsIgnoreCase( "KAZ" ) )
			{
				resourceId = R.drawable.flag_kz;
			}
			else if( countryCode.equalsIgnoreCase( "RUS" ) )
			{
				resourceId = R.drawable.flag_ru;
			}
			else if( countryCode.equalsIgnoreCase( "USA" ) )
			{
				resourceId = R.drawable.flag_us;
			}
			else if( countryCode.equalsIgnoreCase( "UNK" ) )
			{
				resourceId = R.drawable.flag_unknown;
			}
		}

		return resourceId;
	}

	public static int getLaunchTypeResource( int type )
	{
		int resourceId = R.drawable.ic_launch_type_astrophysics;



		return resourceId;
	}
}

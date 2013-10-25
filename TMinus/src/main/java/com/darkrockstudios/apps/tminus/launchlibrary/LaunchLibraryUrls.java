package com.darkrockstudios.apps.tminus.launchlibrary;


/**
 * Created by Adam on 10/24/13.
 */
public class LaunchLibraryUrls
{
	private static final String SCHEME        = "http";
	private static final String HOST          = "launchlibrary.net";
	private static final String PLATFORM_STUB = "ll";

	private static final String FORMAT = "json";

	private static final String PAD_LIST = "padlist";
	private static final String ROCKETS  = "rockets";
	private static final String NEXT     = "next";

	private static String getBaseUrl()
	{
		return SCHEME + "://" + HOST + "/" + PLATFORM_STUB;
	}

	private static String getUrlForMethod( String method )
	{
		String baseUrl = getBaseUrl();
		baseUrl += "/" + FORMAT + "/" + method;
		return baseUrl;
	}

	public static String padList()
	{
		return getUrlForMethod( PAD_LIST );
	}

	public static String rockets()
	{
		return getUrlForMethod( ROCKETS );
	}

	public static String next( int n )
	{
		if( n <= 0 || n > 20 )
		{
			throw new IllegalArgumentException( "n must conform to: 0 > n <= 20" );
		}

		String methodUrl = getUrlForMethod( NEXT );
		methodUrl += "/" + n;

		return methodUrl;
	}
}

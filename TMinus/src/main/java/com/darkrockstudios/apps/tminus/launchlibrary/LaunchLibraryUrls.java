package com.darkrockstudios.apps.tminus.launchlibrary;


/**
 * Created by Adam on 10/24/13.
 */
public class LaunchLibraryUrls
{
	private static final String SCHEME           = "http";
	private static final String HOST             = "launchlibrary.net";
	private static final String PLATFORM_STUB    = "ll";
	private static final String API_VERSION_STUB = "dev";

	private static final String FORMAT = "json";

	private static final String LOCATIONS = "locations";
	private static final String ROCKETS   = "rockets";
	private static final String AGENCIES  = "agencies";
	private static final String NEXT      = "next";
	private static final String LAST      = "last";

	private static final int NEXT_LAUNCH_LIMIT = 20;
	private static final int LAST_LAUNCH_LIMIT = 20;

	private static String getBaseUrl()
	{
		return SCHEME + "://" + HOST + '/' + PLATFORM_STUB + '/' + API_VERSION_STUB;
	}

	private static String getUrlForMethod( final String method )
	{
		String baseUrl = getBaseUrl();
		baseUrl += "/" + FORMAT + "/" + method;
		return baseUrl;
	}

	public static String locations()
	{
		return getUrlForMethod( LOCATIONS );
	}

	public static String rockets()
	{
		return getUrlForMethod( ROCKETS );
	}

	public static String agencies()
	{
		return getUrlForMethod( AGENCIES );
	}

	public static String next( final int n )
	{
		if( n <= 0 || n > NEXT_LAUNCH_LIMIT )
		{
			throw new IllegalArgumentException( "n must conform to: 0 > n <= " + NEXT_LAUNCH_LIMIT );
		}

		String methodUrl = getUrlForMethod( NEXT );
		methodUrl += "/" + n;

		return methodUrl;
	}

	public static String last( final int n )
	{
		if( n <= 0 || n > LAST_LAUNCH_LIMIT )
		{
			throw new IllegalArgumentException( "n must conform to: 0 > n <= " + LAST_LAUNCH_LIMIT );
		}

		String methodUrl = getUrlForMethod( LAST );
		methodUrl += "/" + n;

		return methodUrl;
	}
}

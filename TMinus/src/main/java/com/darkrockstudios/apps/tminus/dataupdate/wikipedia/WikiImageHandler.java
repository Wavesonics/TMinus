package com.darkrockstudios.apps.tminus.dataupdate.wikipedia;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Adam on 2/9/14.
 */
public class WikiImageHandler
{
	private static final String TAG = WikiImageHandler.class.getSimpleName();

	public static String processImage( final JSONObject response )
	{
		String rocketThumbnailUrl = null;

		Log.d( TAG, "Received wiki IMAGE data, parsing..." );
		if( response != null )
		{
			try
			{
				JSONObject parse = response.getJSONObject( "query" );
				JSONArray pageIdsArray = parse.getJSONArray( "pageids" );

				if( pageIdsArray.length() == 1 )
				{
					final String pageId = pageIdsArray.getString( 0 );

					JSONObject pages = parse.getJSONObject( "pages" );
					JSONObject rocketPage = pages.getJSONObject( pageId );

					JSONObject rocketThumbnail = rocketPage.getJSONObject( "thumbnail" );
					rocketThumbnailUrl = rocketThumbnail.getString( "source" );
				}
			}
			catch( final JSONException e )
			{
				Log.d( TAG, "No Wiki image found" );
				rocketThumbnailUrl = null;
			}
		}

		return rocketThumbnailUrl;
	}
}

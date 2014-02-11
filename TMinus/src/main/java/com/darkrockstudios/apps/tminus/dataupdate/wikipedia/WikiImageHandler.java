package com.darkrockstudios.apps.tminus.dataupdate.wikipedia;

import android.content.Context;
import android.util.Log;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.database.tables.RocketDetail;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

/**
 * Created by Adam on 2/9/14.
 */
public class WikiImageHandler
{
	private static final String TAG = WikiImageHandler.class.getSimpleName();

	public static boolean processImage( final JSONObject response, final int rocketId, final Context context )
	{
		boolean success = false;

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
					String rocketThumbnailUrl = rocketThumbnail.getString( "source" );

					if( rocketThumbnailUrl != null )
					{
						if( saveToDatabase( rocketThumbnailUrl, rocketId, context ) )
						{
							success = true;
						}
						else
						{
							success = false;
						}
					}
				}
			}
			catch( final JSONException e )
			{
				e.printStackTrace();
				success = false;
			}
		}

		return success;
	}

	private static boolean saveToDatabase( final String rocketThumbnailUrl, final int rocketId, final Context context )
	{
		boolean success = false;

		final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( context, DatabaseHelper.class );
		if( databaseHelper != null )
		{
			try
			{
				Dao<RocketDetail, Integer> rocketDetailDao = databaseHelper.getDao( RocketDetail.class );
				RocketDetail rocketDetail = rocketDetailDao.queryForId( rocketId );
				if( rocketDetail == null )
				{
					rocketDetail = new RocketDetail();
					rocketDetail.rocketId = rocketId;
					rocketDetail.imageUrl = rocketThumbnailUrl;
					rocketDetailDao.create( rocketDetail );
					success = true;
				}
				else
				{
					rocketDetail.imageUrl = rocketThumbnailUrl;
					rocketDetailDao.update( rocketDetail );
					success = true;
				}
			}
			catch( final SQLException e )
			{
				e.printStackTrace();
			}

			OpenHelperManager.releaseHelper();
		}

		return success;
	}
}

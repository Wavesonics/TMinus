package com.darkrockstudios.apps.tminus.updatetasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.darkrockstudios.apps.tminus.TMinusApplication;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.misc.TminusUri;
import com.darkrockstudios.apps.tminus.updatetasks.misc.WikiArticleHandler;
import com.darkrockstudios.apps.tminus.updatetasks.misc.WikiImageHandler;
import com.j256.ormlite.dao.Dao;

import org.json.JSONObject;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Adam on 2/9/14.
 * This is a meta task that does two separate network requests
 */
public class RocketDetailUpdateTask extends UpdateTask
{
	private static final String TAG         = RocketDetailUpdateTask.class.getSimpleName();
	public static final  String UPDATE_TYPE = "rocket_details";

	public static final String ACTION_ROCKET_DETAILS_UPDATED       =
			RocketDetailUpdateTask.class.getPackage() + ".ACTION_ROCKET_DETAILS_UPDATED";
	public static final String ACTION_ROCKET_DETAILS_UPDATE_FAILED =
			RocketDetailUpdateTask.class.getPackage() + ".ACTION_ROCKET_DETAILS_UPDATE_FAILED";

	private final Uri m_data;

	public RocketDetailUpdateTask( final Context context, final Uri data )
	{
		super( context );

		m_data = data;
	}

	/**
	 * We are subverting the normal UpdateTask flow
	 */
	@Override
	public void run()
	{
		final boolean success;

		Log.d( TAG, "Began RocketDetail update" );

		Rocket rocket = getRocket();
		if( rocket != null )
		{
			String articleTitle = WikiArticleHandler.extractArticleTitle( rocket );

			if( articleTitle != null )
			{
				final Context context = getContext();
				Log.d( TAG, "Requesting Rocket Details from wiki article: " + articleTitle );
				if( requestRocketArticle( articleTitle, rocket.id, context ) )
				{
					requestRocketImage( articleTitle, rocket.id, context );
				}

				success = true;
			}
			else
			{
				Log.d( TAG, "Could not extract Wiki article for rocket." );
				Log.d( TAG, "Rocket: " + rocket.name );
				Log.d( TAG, "WikiURL: " + (rocket.wikiURL != null ? rocket.wikiURL : "null") );

				success = false;
			}
		}
		else
		{
			Log.w( TAG, "Not rocket found for URI: " + m_data + " aborting Rocket Details update" );
			success = false;
		}

		final Intent intent;
		if( success )
		{
			Log.d( TAG, "RocketDetails received successfully" );

			intent = new Intent( getSuccessIntentAction() );
			intent.setData( m_data );
		}
		else
		{
			Log.d( TAG, "Failed to retrieve RocketDetails" );

			intent = new Intent( getFailureIntentAction() );
			intent.setData( m_data );
		}
		getContext().sendBroadcast( intent );
	}

	/**
	 * We are now using the normal UpdateTask flow here
	 */
	@Override
	public boolean handleData( final JSONObject response )
	{
		return false;
	}

	/**
	 * We are now using the normal UpdateTask flow here
	 */
	@Override
	public String getRequestUrl()
	{
		return null;
	}

	@Override
	public String getSuccessIntentAction()
	{
		return ACTION_ROCKET_DETAILS_UPDATED;
	}

	@Override
	public String getFailureIntentAction()
	{
		return ACTION_ROCKET_DETAILS_UPDATE_FAILED;
	}

	private Rocket getRocket()
	{
		Rocket rocket = null;

		int rocketId = TminusUri.extractRocketId( m_data );

		DatabaseHelper databaseHelper = new DatabaseHelper( getContext() );
		try
		{
			Dao<Rocket, Integer> rocketDao = databaseHelper.getDao( Rocket.class );
			rocket = rocketDao.queryForId( rocketId );
		}
		catch( final SQLException e )
		{
			Log.d( TAG, "Failed to get rocket for detail update" );
		}
		finally
		{
			databaseHelper.close();
		}

		return rocket;
	}

	private static boolean requestRocketArticle( final String articleTitle, final int rocketId, final Context context )
	{
		boolean success = false;
		Log.d( TAG, "Requesting Rocket Article..." );

		final String baseUrl = "http://en.wikipedia.org";
		final String articleQuery =
				"/w/api.php?action=parse&format=json&prop=wikitext&section=0&contentformat=text%2Fx-wiki&contentmodel=wikitext&redirects=&page=";

		final String url = baseUrl + articleQuery + articleTitle;

		RequestFuture<JSONObject> future = RequestFuture.newFuture();

		JsonObjectRequest request = new JsonObjectRequest( url, null, future, future );
		request.setTag( context );
		TMinusApplication.getRequestQueue().add( request );

		try
		{
			JSONObject response = future.get();
			success = WikiArticleHandler.processWikiArticle( response, rocketId, context );
		}
		catch( InterruptedException | ExecutionException e )
		{
			success = false;
		}

		return success;
	}

	private static boolean requestRocketImage( final String articleTitle, final int rocketId, final Context context )
	{
		boolean success = false;
		Log.d( TAG, "Requesting Rocket Image..." );

		// This gives the "page image"
		// Action: query
		// prop=pageimages

		final String baseUrl = "http://en.wikipedia.org";
		final String imageQuery =
				"/w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail%7Cname&pithumbsize=512&pilimit=1&indexpageids=&redirects=&titles=";

		final String url = baseUrl + imageQuery + articleTitle;

		RequestFuture<JSONObject> future = RequestFuture.newFuture();

		JsonObjectRequest request = new JsonObjectRequest( url, null, future, future );
		request.setTag( context );
		TMinusApplication.getRequestQueue().add( request );

		try
		{
			JSONObject response = future.get();
			success = WikiImageHandler.processImage( response, rocketId, context );
		}
		catch( InterruptedException | ExecutionException e )
		{
			success = false;
		}

		return success;
	}
}

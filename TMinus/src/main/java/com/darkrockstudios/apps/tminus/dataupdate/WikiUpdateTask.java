package com.darkrockstudios.apps.tminus.dataupdate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.darkrockstudios.apps.tminus.TMinusApplication;
import com.darkrockstudios.apps.tminus.dataupdate.wikipedia.WikiArticleHandler;
import com.darkrockstudios.apps.tminus.dataupdate.wikipedia.WikiImageHandler;

import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

/**
 * Created by Adam on 2/15/14.
 */
public abstract class WikiUpdateTask extends UpdateTask
{
	private static final String TAG = WikiUpdateTask.class.getSimpleName();

	protected final Uri m_data;

	public WikiUpdateTask( final Context context, final Uri data )
	{
		super( context );

		m_data = data;
	}

	protected abstract int getId();

	protected abstract String getArticleTitle();

	protected abstract boolean saveArticleToDatabase( final String articleText, int id, Context context );

	protected abstract boolean saveImageToDatabase( final String thumbnailUrl, final int id, final Context context );

	/**
	 * We are subverting the normal UpdateTask flow
	 */
	@Override
	public final void run()
	{
		final boolean success;

		Log.d( TAG, "Began Wiki Detail update" );

		final int id = getId();
		if( id > -1 )
		{
			String articleTitle = getArticleTitle();

			if( articleTitle != null )
			{
				final Context context = getContext();
				Log.d( TAG, "Requesting Wiki Details from wiki article: " + articleTitle );
				String wikiArticle = requestArticle( articleTitle, context );
				if( saveArticleToDatabase( wikiArticle, id, context ) )
				{
					Log.d( TAG, "Wiki Article saved to DB, requesting images..." );
					String thumbnailUrl = requestImage( articleTitle, context );
					saveImageToDatabase( thumbnailUrl, id, context );
				}
				else
				{
					Log.d( TAG, "Failed to save Wiki article to DB: " + articleTitle );
				}

				success = true;
			}
			else
			{
				Log.d( TAG, "Could not extract Wiki article for id: " + id );

				success = false;
			}
		}
		else
		{
			Log.w( TAG, "Not item found for URI: " + m_data + " aborting Wiki Details update" );
			success = false;
		}

		final Intent intent;
		if( success )
		{
			Log.d( TAG, "Wiki Details received successfully" );

			intent = new Intent( getSuccessIntentAction() );
			intent.setData( m_data );
		}
		else
		{
			Log.d( TAG, "Failed to retrieve Wiki Details" );

			intent = new Intent( getFailureIntentAction() );
			intent.setData( m_data );
		}
		getContext().sendBroadcast( intent );
	}

	/**
	 * We are now using the normal UpdateTask flow here
	 */
	@Override
	public final boolean handleData( final JSONObject response )
	{
		return false;
	}

	/**
	 * We are now using the normal UpdateTask flow here
	 */
	@Override
	public final String getRequestUrl()
	{
		return null;
	}

	private static String requestArticle( final String articleTitle, final Context context )
	{
		String wikiArticle;
		Log.d( TAG, "Requesting Wiki Article..." );

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
			wikiArticle = WikiArticleHandler.processWikiArticle( response );
		}
		catch( InterruptedException | ExecutionException e )
		{
			wikiArticle = null;
		}

		return wikiArticle;
	}

	private static String requestImage( final String articleTitle, final Context context )
	{
		String thumbnailUrl;
		Log.d( TAG, "Requesting Wiki Image..." );

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
			thumbnailUrl = WikiImageHandler.processImage( response );
		}
		catch( InterruptedException | ExecutionException e )
		{
			thumbnailUrl = null;
		}

		return thumbnailUrl;
	}
}

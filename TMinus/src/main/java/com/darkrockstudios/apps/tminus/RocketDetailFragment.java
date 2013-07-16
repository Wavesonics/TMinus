package com.darkrockstudios.apps.tminus;


import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.darkrockstudios.apps.tminus.R.string;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.misc.DiskBitmapCache;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.SQLException;

/**
 * Created by Adam on 7/14/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class RocketDetailFragment extends DialogFragment
{
	public static final String TAG         = LaunchDetailFragment.class.getSimpleName();
	public static final String ARG_ITEM_ID = "item_id";
	private File             m_dataDirectory;
	private Rocket           m_rocket;
	private NetworkImageView m_rocketImage;
	private TextView         m_rocketName;
	private TextView         m_rocketConfiguration;

	public RocketDetailFragment()
	{
	}

	public static RocketDetailFragment newInstance( int rocketId )
	{
		RocketDetailFragment rocketDetailFragment = new RocketDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putInt( ARG_ITEM_ID, rocketId );
		rocketDetailFragment.setArguments( arguments );

		return rocketDetailFragment;
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState )
	{
		Dialog dialog = getDialog();
		if( dialog != null )
		{
			dialog.setTitle( string.ROCKETDETAIL_title );
		}

		View rootView = inflater.inflate( R.layout.fragment_rocket_detail, container, false );

		if( rootView != null )
		{
			m_rocketImage = (NetworkImageView)rootView.findViewById( R.id.ROCKETDETAIL_rocket_image );
			m_rocketName = (TextView)rootView.findViewById( R.id.ROCKETDETAIL_name );
			m_rocketConfiguration = (TextView)rootView.findViewById( R.id.ROCKETDETAIL_configuration );

			loadRocket();
		}

		return rootView;
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );

		String dataDirPath = activity.getApplicationInfo().dataDir;
		m_dataDirectory = new File( dataDirPath );
	}

	private void updateViews()
	{
		if( m_rocket != null )
		{
			m_rocketName.setText( m_rocket.name );
			m_rocketConfiguration.setText( m_rocket.configuration );
		}
	}

	private void loadRocket()
	{
		int rocketId = getRocketId();

		if( rocketId >= 0 )
		{
			RocketLoader rocketLoader = new RocketLoader();
			rocketLoader.execute( rocketId );
		}
	}

	public int getRocketId()
	{
		int rocketId = -1;

		final Bundle arguments = getArguments();
		if( arguments != null && arguments.containsKey( ARG_ITEM_ID ) )
		{
			rocketId = arguments.getInt( ARG_ITEM_ID );
		}

		return rocketId;
	}

	private void requestRocketImage()
	{
		// This gives the "page image"
		// Action: query
		// prop=pageimages

		final String hardCodedArticleTitle = "Falcon_9";

		final String baseUrl = "http://en.wikipedia.org";
		final String imageQuery = "/w/api.php?action=query&prop=pageimages&format=json&piprop=thumbnail%7Cname&pithumbsize=512&pilimit=1&indexpageids=&titles=";

		final String url = baseUrl + imageQuery + hardCodedArticleTitle;

		WikiImageListener listener = new WikiImageListener( m_rocketImage, m_dataDirectory );
		JsonObjectRequest request = new JsonObjectRequest( url, null, listener, listener );
		request.setTag( this );
		TMinusApplication.getRequestQueue().add( request );
	}

	// This Wiki api call will get the summary section for a given article
	// /w/api.php?action=parse&format=json&page=Falcon_9&prop=text&section=0

	private static class WikiImageListener implements Listener<JSONObject>, ErrorListener
	{
		private File             m_dataDirectory;
		private NetworkImageView m_rocketImage;

		public WikiImageListener( NetworkImageView rocketImage, File dataDirectory )
		{
			m_rocketImage = rocketImage;
			m_dataDirectory = dataDirectory;
		}

		@Override
		public void onResponse( JSONObject response )
		{
			Log.d( TAG, "Received wiki article data" );
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
							Log.d( TAG, "Preparing to load rocket image: " + rocketThumbnailUrl );
							ImageLoader imageLoader = new ImageLoader( TMinusApplication
									                                           .getRequestQueue(), new DiskBitmapCache( m_dataDirectory, 10485760 ) );
							m_rocketImage.setImageUrl( rocketThumbnailUrl, imageLoader );
						}
					}
				}
				catch( JSONException e )
				{
					e.printStackTrace();
				}

			}
		}

		@Override
		public void onErrorResponse( VolleyError error )
		{

		}
	}

	private class RocketLoader extends AsyncTask<Integer, Void, Rocket>
	{
		@Override
		protected Rocket doInBackground( Integer... ids )
		{
			Rocket rocket = null;

			Activity activity = getActivity();
			if( activity != null )
			{
				final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( activity, DatabaseHelper.class );
				if( databaseHelper != null )
				{
					try
					{
						Dao<Rocket, Integer> rocketDao = databaseHelper.getRocketDao();
						rocket = rocketDao.queryForId( ids[ 0 ] );
					}
					catch( SQLException e )
					{
						e.printStackTrace();
					}

					OpenHelperManager.releaseHelper();
				}
			}

			return rocket;
		}

		@Override
		protected void onPostExecute( Rocket result )
		{
			Log.d( TAG, "Rocket details loaded." );
			m_rocket = result;

			if( m_rocket != null )
			{
				requestRocketImage();

				updateViews();
			}
		}
	}
}

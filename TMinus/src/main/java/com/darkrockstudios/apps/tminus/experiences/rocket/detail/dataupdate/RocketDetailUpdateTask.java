package com.darkrockstudios.apps.tminus.experiences.rocket.detail.dataupdate;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.database.tables.RocketDetail;
import com.darkrockstudios.apps.tminus.dataupdate.WikiUpdateTask;
import com.darkrockstudios.apps.tminus.dataupdate.wikipedia.WikiArticleHandler;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.misc.TminusUri;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by Adam on 2/9/14.
 * This is a meta task that does two separate network requests
 */
public class RocketDetailUpdateTask extends WikiUpdateTask
{
	private static final String TAG         = RocketDetailUpdateTask.class.getSimpleName();
	public static final  String UPDATE_TYPE = "rocket_details";

	public static final String ACTION_ROCKET_DETAILS_UPDATED       =
			RocketDetailUpdateTask.class.getPackage() + ".ACTION_ROCKET_DETAILS_UPDATED";
	public static final String ACTION_ROCKET_DETAILS_UPDATE_FAILED =
			RocketDetailUpdateTask.class.getPackage() + ".ACTION_ROCKET_DETAILS_UPDATE_FAILED";


	public RocketDetailUpdateTask( final Context context, final Uri data )
	{
		super( context, data );
	}

	@Override
	protected int getId()
	{
		return TminusUri.extractRocketId( m_data );
	}

	@Override
	protected String getArticleTitle()
	{
		final String wikiUrl;

		Rocket rocket = getRocket();
		if( rocket != null )
		{
			wikiUrl = WikiArticleHandler.extractArticleTitle( rocket.wikiURL );
		}
		else
		{
			wikiUrl = null;
		}

		return wikiUrl;
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

	@Override
	protected boolean saveArticleToDatabase( final String articleText, final int id, final Context context )
	{
		boolean success = false;

		if( articleText != null )
		{
			final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( context, DatabaseHelper.class );
			if( databaseHelper != null )
			{
				try
				{
					Dao<RocketDetail, Integer> rocketDetailDao = databaseHelper.getDao( RocketDetail.class );
					RocketDetail rocketDetail = rocketDetailDao.queryForId( id );
					if( rocketDetail == null )
					{
						rocketDetail = new RocketDetail();
						rocketDetail.rocketId = id;
						rocketDetail.summary = articleText;
						rocketDetailDao.create( rocketDetail );
						success = true;
					}
					else
					{
						rocketDetail.summary = articleText;
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
		}

		return success;
	}

	@Override
	protected boolean saveImageToDatabase( final String thumbnailUrl, final int id, final Context context )
	{
		boolean success = false;

		final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( context, DatabaseHelper.class );
		if( databaseHelper != null )
		{
			try
			{
				Dao<RocketDetail, Integer> rocketDetailDao = databaseHelper.getDao( RocketDetail.class );
				RocketDetail rocketDetail = rocketDetailDao.queryForId( id );
				if( rocketDetail == null )
				{
					rocketDetail = new RocketDetail();
					rocketDetail.rocketId = id;
					rocketDetail.imageUrl = thumbnailUrl;
					rocketDetailDao.create( rocketDetail );
					success = true;
				}
				else
				{
					rocketDetail.imageUrl = thumbnailUrl;
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

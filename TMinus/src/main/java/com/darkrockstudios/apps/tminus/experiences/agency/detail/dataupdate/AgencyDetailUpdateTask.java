package com.darkrockstudios.apps.tminus.experiences.agency.detail.dataupdate;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.database.tables.AgencyDetail;
import com.darkrockstudios.apps.tminus.dataupdate.WikiUpdateTask;
import com.darkrockstudios.apps.tminus.dataupdate.wikipedia.WikiArticleHandler;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.misc.TminusUri;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by Adam on 2/15/14.
 */
public class AgencyDetailUpdateTask extends WikiUpdateTask
{
	private static final String TAG         = AgencyDetailUpdateTask.class.getSimpleName();
	public static final  String UPDATE_TYPE = "agency_details";

	public static final String ACTION_AGENCY_DETAILS_UPDATED       =
			AgencyDetailUpdateTask.class.getPackage() + ".ACTION_AGENCY_DETAILS_UPDATED";
	public static final String ACTION_AGENCY_DETAILS_UPDATE_FAILED =
			AgencyDetailUpdateTask.class.getPackage() + ".ACTION_AGENCY_DETAILS_UPDATE_FAILED";

	public AgencyDetailUpdateTask( final Context context, final Uri data )
	{
		super( context, data );
	}

	@Override
	protected int getId()
	{
		return TminusUri.extractAgencyId( m_data );
	}

	@Override
	protected String getArticleTitle()
	{
		final String wikiUrl;

		Agency agency = getAgency();
		if( agency != null )
		{
			wikiUrl = WikiArticleHandler.extractArticleTitle( agency.wikiURL );
		}
		else
		{
			wikiUrl = null;
		}

		return wikiUrl;
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
					Dao<AgencyDetail, Integer> agencyDetailDao = databaseHelper.getDao( AgencyDetail.class );
					AgencyDetail agencyDetail = agencyDetailDao.queryForId( id );
					if( agencyDetail == null )
					{
						agencyDetail = new AgencyDetail();
						agencyDetail.agencyId = id;
						agencyDetail.summary = articleText;
						agencyDetailDao.create( agencyDetail );
						success = true;
					}
					else
					{
						agencyDetail.summary = articleText;
						agencyDetailDao.update( agencyDetail );
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
				Dao<AgencyDetail, Integer> agencyDetailDao = databaseHelper.getDao( AgencyDetail.class );
				AgencyDetail agencyDetail = agencyDetailDao.queryForId( id );
				if( agencyDetail == null )
				{
					agencyDetail = new AgencyDetail();
					agencyDetail.agencyId = id;
					agencyDetail.imageUrl = thumbnailUrl;
					agencyDetailDao.create( agencyDetail );
					success = true;
				}
				else
				{
					agencyDetail.imageUrl = thumbnailUrl;
					agencyDetailDao.update( agencyDetail );
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

	@Override
	public String getSuccessIntentAction()
	{
		return ACTION_AGENCY_DETAILS_UPDATED;
	}

	@Override
	public String getFailureIntentAction()
	{
		return ACTION_AGENCY_DETAILS_UPDATE_FAILED;
	}

	private Agency getAgency()
	{
		Agency agency = null;

		int agencyId = TminusUri.extractAgencyId( m_data );

		DatabaseHelper databaseHelper = new DatabaseHelper( getContext() );
		try
		{
			Dao<Agency, Integer> agencyDao = databaseHelper.getDao( Agency.class );
			agency = agencyDao.queryForId( agencyId );
		}
		catch( final SQLException e )
		{
			Log.d( TAG, "Failed to get Agency for detail update" );
		}
		finally
		{
			databaseHelper.close();
		}

		return agency;
	}
}
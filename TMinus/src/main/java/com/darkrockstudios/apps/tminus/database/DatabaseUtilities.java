package com.darkrockstudios.apps.tminus.database;

import com.darkrockstudios.apps.tminus.database.tables.AgencyPad;
import com.darkrockstudios.apps.tminus.database.tables.AgencyRocket;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;
import com.darkrockstudios.apps.tminus.launchlibrary.Pad;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.launchlibrary.RocketFamily;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Adam on 2/18/14.
 */
public class DatabaseUtilities
{
	private static final String TAG = DatabaseUtilities.class.getSimpleName();

	public static void saveRocket( final Rocket rocket, final DatabaseHelper databaseHelper ) throws SQLException
	{
		final Dao<Rocket, Integer> rocketDao = databaseHelper.getDao( Rocket.class );
		final Dao<RocketFamily, Integer> rocketFamilyDao = databaseHelper.getDao( RocketFamily.class );
		final Dao<Agency, Integer> agencyDao = databaseHelper.getDao( Agency.class );
		final Dao<AgencyRocket, Integer> agencyRocketDao = databaseHelper.getDao( AgencyRocket.class );

		if( rocket.family != null )
		{
			if( rocket.family.agencies != null )
			{
				for( final Agency agency : rocket.family.agencies )
				{
					agencyDao.createOrUpdate( agency );

					AgencyRocket agencyRocket = new AgencyRocket( agency, rocket.family );

					PreparedQuery<AgencyRocket> preexistingCheck = agencyRocketDao.queryBuilder()
					                                                              .where()
					                                                              .eq( "agency_id", agency.id )
					                                                              .and()
					                                                              .eq( "rocketFamily_id", rocket.family.id )
					                                                              .prepare();
					List<AgencyRocket> existingRelationship = agencyRocketDao.query( preexistingCheck );
					if( existingRelationship == null || existingRelationship.size() == 0 )
					{
						agencyRocketDao.createOrUpdate( agencyRocket );
					}
				}
			}

			rocketFamilyDao.createOrUpdate( rocket.family );
		}

		rocketDao.createOrUpdate( rocket );
	}

	public static void saveLocation( final Location location, final DatabaseHelper databaseHelper ) throws SQLException
	{
		final Dao<Location, Integer> locationDao = databaseHelper.getDao( Location.class );
		final Dao<Pad, Integer> padDao = databaseHelper.getDao( Pad.class );
		final Dao<Agency, Integer> agencyDao = databaseHelper.getDao( Agency.class );
		final Dao<AgencyPad, Integer> agencyPadDao = databaseHelper.getDao( AgencyPad.class );

		locationDao.createOrUpdate( location );

		for( final Pad pad : location.pads )
		{
			// Ensure the parent/child relationship is setup for the DB
			pad.location = location;

			padDao.createOrUpdate( pad );

			if( pad.agencies != null )
			{
				for( final Agency agency : pad.agencies )
				{
					agencyDao.createOrUpdate( agency );

					AgencyPad agencyProperty = new AgencyPad( agency, pad );

					PreparedQuery<AgencyPad> preexistingCheck = agencyPadDao.queryBuilder()
					                                                        .where()
					                                                        .eq( "agency_id", agency.id )
					                                                        .and()
					                                                        .eq( "pad_id", pad.id )
					                                                        .prepare();

					List<AgencyPad> existingRelationship = agencyPadDao.query( preexistingCheck );
					if( existingRelationship == null || existingRelationship.size() == 0 )
					{
						agencyPadDao.createOrUpdate( agencyProperty );
					}
				}
			}
		}
	}
}

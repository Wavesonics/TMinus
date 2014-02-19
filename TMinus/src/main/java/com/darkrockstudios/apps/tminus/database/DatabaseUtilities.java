package com.darkrockstudios.apps.tminus.database;

import com.darkrockstudios.apps.tminus.database.tables.AgencyRocket;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
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
}

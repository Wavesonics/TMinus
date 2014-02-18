package com.darkrockstudios.apps.tminus.launchlibrary;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.database.tables.AgencyRocket;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 6/23/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */

@DatabaseTable(tableName = Rocket.TABLE_NAME)
public class Rocket implements Serializable
{
	public transient static final String TABLE_NAME = "Rocket";

	@DatabaseField(id = true)
	public int id;

	@DatabaseField
	public String name;

	@DatabaseField
	public String wikiURL;

	@DatabaseField
	public String infoURL;

	@DatabaseField
	public String configuration;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, maxForeignAutoRefreshLevel = 3)
	public RocketFamily family;

	public void refreshFamily( final DatabaseHelper databaseHelper )
	{
		if( family != null )
		{
			family.agencies = new ArrayList<>();

			try
			{
				Dao<AgencyRocket, Integer> agencyRocketDao = databaseHelper.getDao( AgencyRocket.class );
				QueryBuilder<AgencyRocket, Integer> builder = agencyRocketDao.queryBuilder();
				builder.where().eq( "rocketFamily_id", family.id );

				List<AgencyRocket> agencyProperties = builder.query();
				for( AgencyRocket agencyRocket : agencyProperties )
				{
					family.agencies.add( agencyRocket.agency );
				}
			}
			catch( SQLException e )
			{
				e.printStackTrace();
			}
		}
	}
}

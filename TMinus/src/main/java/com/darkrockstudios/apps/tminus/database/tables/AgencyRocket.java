package com.darkrockstudios.apps.tminus.database.tables;

import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.launchlibrary.RocketFamily;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Adam on 1/22/14.
 */
@DatabaseTable(tableName = AgencyRocket.TABLE_NAME)
public class AgencyRocket
{
	public static final String TABLE_NAME = "AgencyRocket";

	@DatabaseField(generatedId = true)
	public int id;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, uniqueCombo = true)
	public Agency agency;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, uniqueCombo = true)
	public RocketFamily rocketFamily;

	public AgencyRocket()
	{

	}

	public AgencyRocket( final Agency agency, final RocketFamily rocketFamily )
	{
		this.agency = agency;
		this.rocketFamily = rocketFamily;
	}
}

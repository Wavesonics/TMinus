package com.darkrockstudios.apps.tminus.database.tables;

import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.launchlibrary.Pad;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Adam on 1/24/14.
 */
@DatabaseTable(tableName = AgencyPad.TABLE_NAME)
public class AgencyPad
{
	public static final String TABLE_NAME = "AgencyPad";

	@DatabaseField(generatedId = true)
	public int id;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, uniqueCombo = true)
	public Agency agency;

	@DatabaseField(foreign = true, foreignAutoRefresh = true, uniqueCombo = true)
	public Pad pad;

	public AgencyPad()
	{

	}

	public AgencyPad( final Agency agency, final Pad pad )
	{
		this.agency = agency;
		this.pad = pad;
	}
}

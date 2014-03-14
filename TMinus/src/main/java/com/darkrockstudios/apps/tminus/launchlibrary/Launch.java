package com.darkrockstudios.apps.tminus.launchlibrary;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by Adam on 6/23/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */

@DatabaseTable(tableName = Launch.TABLE_NAME)
public class Launch implements Serializable
{
	public transient static final String TABLE_NAME  = "Launch";
	public transient static final String DATE_FORMAT = "MMM dd, yyyy HH:mm:ss zzz";

	@DatabaseField(id = true)
	public int id;

	@DatabaseField
	public int status;

	@DatabaseField(dataType = DataType.DATE_TIME)
	public DateTime windowstart;

	@DatabaseField(canBeNull = true, dataType = DataType.DATE_TIME)
	public DateTime windowend;

	@DatabaseField
	public String name;

	@DatabaseField
	public boolean inhold;

	@DatabaseField(dataType = DataType.DATE_TIME)
	public DateTime net;

	@ForeignCollectionField(eager = true)
	public Collection<Mission> missions;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	public Rocket rocket;

	@DatabaseField(canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	public Location location;

	public String toString()
	{
		return name;
	}
}

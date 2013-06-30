package com.darkrockstudios.apps.tminus.launchlibrary;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Adam on 6/23/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */

@DatabaseTable( tableName = Launch.TABLE_NAME )
public class Launch implements Serializable
{
	public transient static final String TABLE_NAME = "Launch";

	@DatabaseField( id = true )
	public int      id;

	@DatabaseField
	public int      status;

	@DatabaseField
	public String   windowstart;

	@DatabaseField
	public String   windowend;

	@DatabaseField
	public String   name;

	@DatabaseField
	public boolean  inhold;

	@DatabaseField
	public String   net;

	@DatabaseField( canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true )
	public Mission  mission;

	@DatabaseField( canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true )
	public Rocket   rocket;

	@DatabaseField( canBeNull = false, foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true )
	public Location location;

	public String toString()
	{
		return name;
	}
}

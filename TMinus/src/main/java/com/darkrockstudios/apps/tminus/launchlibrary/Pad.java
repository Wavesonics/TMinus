package com.darkrockstudios.apps.tminus.launchlibrary;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Adam on 6/23/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */

@DatabaseTable(tableName = Pad.TABLE_NAME)
public class Pad implements Serializable
{
	public transient static final String TABLE_NAME = "Pad";

	@DatabaseField(id = true)
	public int id;

	@DatabaseField
	public String name;

	@DatabaseField
	public boolean retired;

	@DatabaseField(foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
	public Location location;

	@DatabaseField(canBeNull = true)
	public Double longitude;

	@DatabaseField(canBeNull = true)
	public Double latitude;

	@DatabaseField
	public String wikiURL;

	@DatabaseField
	public String infoURL;

	@DatabaseField
	public String mapURL;
}

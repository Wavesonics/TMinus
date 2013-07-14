package com.darkrockstudios.apps.tminus.launchlibrary;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Adam on 6/23/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */

@DatabaseTable(tableName = Location.TABLE_NAME)
public class Location implements Serializable
{
	public transient static final String TABLE_NAME = "Location";

	@DatabaseField(id = true)
	public int id;

	@DatabaseField
	public String name;

	@DatabaseField
	public boolean retired;

	@DatabaseField
	public int locationid;

	@DatabaseField
	public String wikiURL;

	@DatabaseField
	public String infoURL;

	@DatabaseField
	public String mapURL;
}

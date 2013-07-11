package com.darkrockstudios.apps.tminus.launchlibrary;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

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
	public int familyID;

	@DatabaseField
	public String name;

    @DatabaseField
	public String wikiURL;

    @DatabaseField
	public String infoURL;

    @DatabaseField
	public String configuration;
}

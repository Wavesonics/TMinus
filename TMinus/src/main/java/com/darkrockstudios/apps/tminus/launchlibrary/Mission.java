package com.darkrockstudios.apps.tminus.launchlibrary;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * Created by Adam on 6/23/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */

@DatabaseTable( tableName = Mission.TABLE_NAME )
public class Mission implements Serializable
{
	public transient static final String TABLE_NAME = "Mission";

	@DatabaseField( id = true )
	public int id;

	@DatabaseField
	public int launchID;

	@DatabaseField
	public int type;

	@DatabaseField
	public String description;

	@DatabaseField
	public String name;

	public String wikiURL;
	public String infoURL;
}

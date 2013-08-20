package com.darkrockstudios.apps.tminus.launchlibrary;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Adam on 8/19/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
@DatabaseTable(tableName = LocInfo.TABLE_NAME)
public class LocInfo
{
	public transient static final String TABLE_NAME = "LocInfo";

	@DatabaseField( id = true )
	public int id;

	@DatabaseField
	public String name;

	@DatabaseField
	public String infoURL;

	@DatabaseField
	public String wikiURL;

	@DatabaseField
	public String countrycode;
}

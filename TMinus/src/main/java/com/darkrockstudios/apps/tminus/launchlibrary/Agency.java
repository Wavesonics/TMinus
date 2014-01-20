package com.darkrockstudios.apps.tminus.launchlibrary;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by adam on 1/19/14.
 */
@DatabaseTable(tableName = Agency.TABLE_NAME)
public class Agency
{
	public transient static final String TABLE_NAME = "Agency";

	@DatabaseField(id = true)
	public int id;

	@DatabaseField
	public String name;

	@DatabaseField
	public String abbrev;

	@DatabaseField
	public String countryCode;

	@DatabaseField
	public int type;

	@DatabaseField
	public String infoURL;

	@DatabaseField
	public String wikiURL;
}

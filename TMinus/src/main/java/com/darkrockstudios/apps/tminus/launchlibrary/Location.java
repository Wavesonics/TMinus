package com.darkrockstudios.apps.tminus.launchlibrary;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;

/**
 * Created by Adam on 8/19/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
@DatabaseTable(tableName = Location.TABLE_NAME)
public class Location
{
	public transient static final String TABLE_NAME = "Location";

	@DatabaseField(id = true)
	public int id;

	@DatabaseField
	public String name;

	@DatabaseField
	public String infoURL;

	@DatabaseField
	public String wikiURL;

	@DatabaseField
	public String countryCode;

	@ForeignCollectionField(eager = true)
	public Collection<Pad> pads;
}

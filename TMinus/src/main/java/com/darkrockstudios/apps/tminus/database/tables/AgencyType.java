package com.darkrockstudios.apps.tminus.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Adam on 2/15/14.
 */
@DatabaseTable(tableName = AgencyType.TABLE_NAME)
public class AgencyType
{
	public static final String TABLE_NAME = "AgencyType";

	@DatabaseField(id = true)
	public int id;

	@DatabaseField
	public String name;
}

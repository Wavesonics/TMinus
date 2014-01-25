package com.darkrockstudios.apps.tminus.launchlibrary;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.List;

/**
 * Created by Adam on 1/22/14.
 */
@DatabaseTable(tableName = RocketFamily.TABLE_NAME)
public class RocketFamily
{
	public transient static final String TABLE_NAME = "RocketFamily";

	@DatabaseField(id = true)
	public int id;

	@DatabaseField
	public String name;

	public List<Agency> agencies;
}

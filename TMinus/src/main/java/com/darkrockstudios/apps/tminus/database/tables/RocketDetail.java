package com.darkrockstudios.apps.tminus.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by adam on 7/21/13.
 */
@DatabaseTable(tableName = RocketDetail.TABLE_NAME)
public class RocketDetail
{
	public static final String TABLE_NAME = "RocketDetail";

	@DatabaseField(id = true)
	public int rocketId;

	@DatabaseField
	public String imageUrl;

	@DatabaseField
	public String summary;
}

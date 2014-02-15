package com.darkrockstudios.apps.tminus.database.tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Adam on 2/15/14.
 */
@DatabaseTable(tableName = AgencyDetail.TABLE_NAME)
public class AgencyDetail
{
	public static final String TABLE_NAME = "AgencyDetail";

	@DatabaseField(id = true)
	public int agencyId;

	@DatabaseField
	public String imageUrl;

	@DatabaseField
	public String summary;
}

package com.darkrockstudios.apps.tminus.database;

/**
 * Created by Adam on 6/29/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.darkrockstudios.apps.tminus.BuildConfig;
import com.darkrockstudios.apps.tminus.database.tables.AgencyPad;
import com.darkrockstudios.apps.tminus.database.tables.AgencyRocket;
import com.darkrockstudios.apps.tminus.database.tables.RocketDetail;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;
import com.darkrockstudios.apps.tminus.launchlibrary.Mission;
import com.darkrockstudios.apps.tminus.launchlibrary.Pad;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.launchlibrary.RocketFamily;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
	private static final String TAG              = DatabaseHelper.class.getSimpleName();
	private static final String DATABASE_NAME    = "TMinus.db";
	private static final int    DATABASE_VERSION = 8;

	private ConcurrentHashMap<Class, Dao> m_dao;

	public DatabaseHelper( Context context )
	{
		super( context,
		       (BuildConfig.DEBUG ? context.getExternalFilesDir( null ).getAbsolutePath() + File.separator + DATABASE_NAME :
		        DATABASE_NAME),
		       null,
		       DATABASE_VERSION );
	}

	@Override
	public void onCreate( SQLiteDatabase db, ConnectionSource connectionSource )
	{
		try
		{
			Log.i( TAG, "Creating Database Tables..." );
			TableUtils.createTable( connectionSource, Launch.class );
			TableUtils.createTable( connectionSource, Location.class );
			TableUtils.createTable( connectionSource, Pad.class );
			TableUtils.createTable( connectionSource, Mission.class );
			TableUtils.createTable( connectionSource, Rocket.class );
			TableUtils.createTable( connectionSource, RocketFamily.class );
			TableUtils.createTable( connectionSource, RocketDetail.class );
			TableUtils.createTable( connectionSource, Agency.class );
			TableUtils.createTable( connectionSource, AgencyRocket.class );
			TableUtils.createTable( connectionSource, AgencyPad.class );
		}
		catch( SQLException e )
		{
			Log.e( DatabaseHelper.class.getName(), "Can't create database", e );
			throw new RuntimeException( e );
		}
	}

	@Override
	public void onUpgrade( SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion )
	{
		try
		{
			Log.i( DatabaseHelper.class.getName(), "Upgrading Database Tables..." );
			TableUtils.dropTable( connectionSource, Launch.class, true );
			TableUtils.dropTable( connectionSource, Location.class, true );
			TableUtils.dropTable( connectionSource, Pad.class, true );
			TableUtils.dropTable( connectionSource, Mission.class, true );
			TableUtils.dropTable( connectionSource, Rocket.class, true );
			TableUtils.dropTable( connectionSource, RocketFamily.class, true );
			TableUtils.dropTable( connectionSource, RocketDetail.class, true );
			TableUtils.dropTable( connectionSource, Agency.class, true );
			TableUtils.dropTable( connectionSource, AgencyRocket.class, true );
			TableUtils.dropTable( connectionSource, AgencyPad.class, true );

			// after we drop the old databases, we create the new ones
			onCreate( db, connectionSource );
		}
		catch( SQLException e )
		{
			Log.e( DatabaseHelper.class.getName(), "Can't drop databases", e );
			throw new RuntimeException( e );
		}
	}

	@Override
	public void close()
	{
		super.close();
	}
}
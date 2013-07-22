package com.darkrockstudios.apps.tminus.database;

/**
 * Created by Adam on 6/29/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;
import com.darkrockstudios.apps.tminus.launchlibrary.Mission;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also provides
 * the DAOs used by the other classes.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper
{
	private static final String                 TAG              = DatabaseHelper.class.getSimpleName();
	private static final String                 DATABASE_NAME    = "TMinus.db";
	private static final int                    DATABASE_VERSION = 3;
	private              Dao<Launch, Integer>   m_launchDao      = null;
	private              Dao<Location, Integer> m_locationDao    = null;
	private              Dao<Mission, Integer>  m_missionDao     = null;
	private              Dao<Rocket, Integer>   m_rocketDao      = null;
    private              Dao<RocketDetail, Integer>   m_rocketDetailDao      = null;

	public DatabaseHelper( Context context )
	{
		super( context, DATABASE_NAME, null, DATABASE_VERSION );
	}

	@Override
	public void onCreate( SQLiteDatabase db, ConnectionSource connectionSource )
	{
		try
		{
			Log.i( TAG, "Creating Database Tables..." );
			TableUtils.createTable( connectionSource, Launch.class );
			TableUtils.createTable( connectionSource, Location.class );
			TableUtils.createTable( connectionSource, Mission.class );
			TableUtils.createTable( connectionSource, Rocket.class );
            TableUtils.createTable( connectionSource, RocketDetail.class );
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
			TableUtils.dropTable( connectionSource, Mission.class, true );
			TableUtils.dropTable( connectionSource, Rocket.class, true );
            TableUtils.dropTable( connectionSource, RocketDetail.class, true );
			// after we drop the old databases, we create the new ones
			onCreate( db, connectionSource );
		}
		catch( SQLException e )
		{
			Log.e( DatabaseHelper.class.getName(), "Can't drop databases", e );
			throw new RuntimeException( e );
		}
	}

	public Dao<Launch, Integer> getLaunchDao() throws SQLException
	{
		if( m_launchDao == null )
		{
			m_launchDao = getDao( Launch.class );
		}
		return m_launchDao;
	}

	public Dao<Location, Integer> getLocationDao() throws SQLException
	{
		if( m_locationDao == null )
		{
			m_locationDao = getDao( Location.class );
		}
		return m_locationDao;
	}

	public Dao<Mission, Integer> getMissionDao() throws SQLException
	{
		if( m_missionDao == null )
		{
			m_missionDao = getDao( Mission.class );
		}
		return m_missionDao;
	}

	public Dao<Rocket, Integer> getRocketDao() throws SQLException
	{
		if( m_rocketDao == null )
		{
			m_rocketDao = getDao( Rocket.class );
		}
		return m_rocketDao;
	}

    public Dao<RocketDetail, Integer> getRocketDetailDao() throws SQLException
    {
        if( m_rocketDetailDao == null )
        {
            m_rocketDetailDao = getDao( RocketDetail.class );
        }
        return m_rocketDetailDao;
    }

	@Override
	public void close()
	{
		super.close();
		m_launchDao = null;
		m_locationDao = null;
		m_missionDao = null;
		m_rocketDao = null;
        m_rocketDetailDao = null;
	}
}
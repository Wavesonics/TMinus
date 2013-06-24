package com.darkrockstudios.apps.tminus.launchlibrary;

import java.io.Serializable;

/**
 * Created by Adam on 6/23/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class Launch implements Serializable
{
	public int      id;
	public int      status;
	public String   windowstart;
	public String   windowend;
	public String   name;
	public boolean  inhold;
	public String   net;
	public Mission  mission;
	public Rocket   rocket;
	public Location location;

	public String toString()
	{
		return name;
	}
}

package com.darkrockstudios.apps.tminus.launchlibrary;

import java.io.Serializable;

/**
 * Created by Adam on 6/23/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class Location implements Serializable
{
	public boolean retired;
	public int     id;
	public int     locationid;
	public Object  wikiURL;
	public Object  infoURL;
	public Object  mapURL;
	public String  name;
}

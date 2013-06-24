package com.darkrockstudios.apps.tminus.launchlibrary;

import java.io.Serializable;

/**
 * Created by Adam on 6/23/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class Mission implements Serializable
{
	public int    id;
	public int    launchID;
	public Object wikiURL;
	public Object infoURL;
	public int    type;
	public String description;
	public String name;
}

package net.ark.tictachead.models;

import net.ark.tictachead.R;

public class Player {
	protected Player(int id, String name, String avatar, int resource) {
		//Save
		m_ID			= id;
		m_Name			= name;
		m_AvatarURL		= avatar;
		m_ResourceID	= resource;
	}
	
	public static Player create(int id) {
		//Initialize
		Player NewPlayer = null;
		if (id == DUMMY1) 		NewPlayer = new Player(id, "Chris Pruett", "chris.png", R.drawable.avatar);
		else if (id == DUMMY2)	NewPlayer = new Player(id, "Reto Meier", "vic.png", R.drawable.reto);
		else if (id == DUMMY3)	NewPlayer = new Player(id, "Christer Kaitila", "vic.png", R.drawable.christer);
		
		//Return
		return NewPlayer;
	}
	
	//Accessor
	public int getID()				{ return m_ID;			}
	public String getName() 		{ return m_Name; 		}
	public String getAvatarURL()	{ return m_AvatarURL;	}
	public int getResourceID()		{ return m_ResourceID;	}
	
	//Constants
	public static final int DUMMY1 = 1;
	public static final int DUMMY2 = 2;
	public static final int DUMMY3 = 3;
	
	//Data
	protected int		m_ID;
	protected String 	m_Name;
	protected String	m_AvatarURL;
	protected int 		m_ResourceID;
}

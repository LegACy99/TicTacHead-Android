package net.ark.tictachead.models;

import net.ark.tictachead.R;

public class Player {
	protected Player(String id, int rating, int resource) {
		//Save
		m_ID			= id;
		m_Name			= id;
		m_Rating		= rating;
		m_ResourceID	= resource;
		m_AvatarURL		= null;
	}
	
	public static Player create(String id) {
		//Initialize
		Player NewPlayer = null;
		if (id == DUMMY1) 		NewPlayer = new Player(id, 74, R.drawable.avatar);
		else if (id == DUMMY2)	NewPlayer = new Player(id, 42, R.drawable.reto);
		else if (id == DUMMY3)	NewPlayer = new Player(id, 103, R.drawable.christer);
		
		//Return
		return NewPlayer;
	}
	
	//Accessor
	public String getID()			{ return m_ID;			}
	public String getName() 		{ return m_Name; 		}
	public String getAvatarURL()	{ return m_AvatarURL;	}
	public int getResourceID()		{ return m_ResourceID;	}
	public int getRating()			{ return m_Rating;		}
	
	//Constants
	public static final String DUMMY1 = "c_pruett@email.com";
	public static final String DUMMY2 = "reto@google.com";
	public static final String DUMMY3 = "christer@kaitila.com";
	
	//Data
	protected String	m_ID;
	protected String 	m_Name;
	protected String	m_AvatarURL;
	protected int 		m_ResourceID;
	protected int		m_Rating;
}

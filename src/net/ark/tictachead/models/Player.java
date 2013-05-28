package net.ark.tictachead.models;

public class Player {
	protected Player(int id, String name, String avatar) {
		//Save
		m_ID		= id;
		m_Name		= name;
		m_AvatarURL	= avatar;
	}
	
	public static Player create(int id) {
		//Initialize
		Player NewPlayer = null;
		if (id == DUMMY1) 		NewPlayer = new Player(id, "Chris Pruett", "chris.png");
		else if (id == DUMMY2)	NewPlayer = new Player(id, "Vic Gundotra", "vic.png");
		
		//Return
		return NewPlayer;
	}
	
	//Accessor
	public int getID()				{ return m_ID;			}
	public String getName() 		{ return m_Name; 		}
	public String getAvatarURL()	{ return m_AvatarURL;	}
	
	//Constants
	public static final int DUMMY1 = 1;
	public static final int DUMMY2 = 2;
	
	//Data
	protected int		m_ID;
	protected String 	m_Name;
	protected String	m_AvatarURL;
}

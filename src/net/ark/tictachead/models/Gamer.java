package net.ark.tictachead.models;

import android.util.Log;
import net.ark.tictachead.R;
import net.gogo.server.onii.api.tictachead.model.Player;

public class Gamer {
	public Gamer(Player player) {
		//Save stuff
		m_ID 			= player.getPlayerID().toString();
		m_Name			= player.getUsername();
		m_ResourceID	= R.drawable.icon;
		m_AvatarURL		= null;
		
		//Check rating
		Integer Rating = player.getRating();
		if (Rating == null) m_Rating = 0;
		else 				m_Rating = Rating.intValue();
	}
	
	//Accessor
	public String getID()			{ return m_ID;			}
	public String getName() 		{ return m_Name; 		}
	public String getAvatarURL()	{ return m_AvatarURL;	}
	public int getResourceID()		{ return m_ResourceID;	}
	public int getRating()			{ return m_Rating;		}
	
	//Data
	protected String	m_ID;
	protected String 	m_Name;
	protected String	m_AvatarURL;
	protected int 		m_ResourceID;
	protected int		m_Rating;
}

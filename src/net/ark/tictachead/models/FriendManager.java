package net.ark.tictachead.models;

import java.util.*;

import net.ark.tictachead.helpers.RecordManager;


public class FriendManager {
	public FriendManager() {
		//Do nothings
	}

	public synchronized static FriendManager instance() {
		//Create state manager if doesn't exist
		if (s_Instance == null) s_Instance = new FriendManager();
		return s_Instance;
	}

	public List<Gamer> getFriends() {
		//Initialize
		List<Gamer> Friends = new ArrayList<Gamer>();

		//Get all values
		Enumeration<Gamer> Enum = RecordManager.instance().getPlayers().elements();
		while (Enum.hasMoreElements()) Friends.add(Enum.nextElement());

		//Return
		return Friends;
	}

	public Gamer getFriend(String id) {
		//Return player
		return RecordManager.instance().getPlayers().get(id);
	}

	public String[] getOpponents() {
		//Get opponents
		String[] Result 	= new String[RecordManager.instance().getOpponents().size()];
		Object[] Opponents	= RecordManager.instance().getOpponents().toArray();
		for (int i = 0; i < Opponents.length; i++) Result[i] = (String)Opponents[i];

		//Return
		return Result;
	}

	public String getActiveOpponent() {
		return RecordManager.instance().getActiveOpponent();
	}

	public void addOpponent(String opponent) 	{ RecordManager.instance().addOpponent(opponent);		}
	public void removeOpponent(String opponent) { RecordManager.instance().removeOpponent(opponent);	}
	public void setActiveOpponent(String id) 	{ RecordManager.instance().setActiveOpponent(id);		}
	
	//The only instance
	private static FriendManager s_Instance = null;
}

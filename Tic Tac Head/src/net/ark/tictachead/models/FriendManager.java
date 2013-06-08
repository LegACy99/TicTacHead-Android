package net.ark.tictachead.models;

import java.util.*;

import net.ark.tictachead.helpers.RecordManager;


public class FriendManager {
	public FriendManager() {
		//Do nothing
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

	public Gamer getFriend(long id) {
		//Return player
		return RecordManager.instance().getPlayers().get(Long.valueOf(id));
	}

	/*public long[] getOpponents() {
		//Get opponents
		long[] Result 		= new long[RecordManager.instance().getOpponents().size()];
		Object[] Opponents	= RecordManager.instance().getOpponents().toArray();
		for (int i = 0; i < Opponents.length; i++) Result[i] = ((Long)Opponents[i]).longValue();

		//Return
		return Result;
	}*/

	public long getActiveOpponent() {
		return RecordManager.instance().getActiveOpponent();
	}

	public void addOpponent(long id) 		{ RecordManager.instance().addOpponent(id);			}
	public void removeOpponent(long id) 	{ RecordManager.instance().removeOpponent(id);		}
	public void setActiveOpponent(long id) 	{ RecordManager.instance().setActiveOpponent(id);	}
	
	//The only instance
	private static FriendManager s_Instance = null;
}

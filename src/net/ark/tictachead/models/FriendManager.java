package net.ark.tictachead.models;

import java.util.*;


public class FriendManager {
	public FriendManager() {
		//Initialize
		m_Active	= -1;
		m_Friends 	= new Hashtable<Integer, Player>();
		m_Opponents = new HashSet<Integer>();

		//Add friends
		m_Friends.put(Integer.valueOf(Player.DUMMY1), Player.create(Player.DUMMY1));
		m_Friends.put(Integer.valueOf(Player.DUMMY2), Player.create(Player.DUMMY2));
		m_Friends.put(Integer.valueOf(Player.DUMMY3), Player.create(Player.DUMMY3));
	}

	public synchronized static FriendManager instance() {
		//Create state manager if doesn't exist
		if (s_Instance == null) s_Instance = new FriendManager();
		return s_Instance;
	}

	public List<Player> getFriends() {
		//Initialize
		List<Player> Friends = new ArrayList<Player>();

		//Get all values
		Enumeration<Player> Enum = m_Friends.elements();
		while (Enum.hasMoreElements()) Friends.add(Enum.nextElement());

		//Return
		return Friends;
	}

	public Player getFriend(int id) {
		//Return player
		return m_Friends.get(Integer.valueOf(id));
	}

	public int[] getOpponents() {
		//Get opponents
		int[] Result 		= new int[m_Opponents.size()];
		Object[] Opponents	= m_Opponents.toArray();
		for (int i = 0; i < Opponents.length; i++) Opponents[i] = ((Integer)Opponents[i]).intValue();

		//Return
		return Result;
	}

	public int getActiveOpponent() {
		return m_Active;
	}

	public void addOpponent(int id) {
		//Add
		m_Opponents.add(Integer.valueOf(id));
	}

	public void setActiveOpponent(int id) {
		//Set as active
		m_Active = id;
	}
	
	//The only instance
	private static FriendManager s_Instance = null;
	
	//Data
	protected int							m_Active;
	protected Set<Integer> 					m_Opponents;
	protected Hashtable<Integer, Player> 	m_Friends;
}

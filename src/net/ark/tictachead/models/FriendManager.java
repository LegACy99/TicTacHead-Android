package net.ark.tictachead.models;

import java.util.*;


public class FriendManager {
	public FriendManager() {
		//Initialize
		m_Active	= null;
		m_Friends 	= new Hashtable<String, Player>();
		m_Opponents = new HashSet<String>();

		//Add friends
		m_Friends.put(Player.DUMMY1, Player.create(Player.DUMMY1));
		m_Friends.put(Player.DUMMY2, Player.create(Player.DUMMY2));
		m_Friends.put(Player.DUMMY3, Player.create(Player.DUMMY3));
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

	public Player getFriend(String id) {
		//Return player
		return m_Friends.get(id);
	}

	public String[] getOpponents() {
		//Get opponents
		String[] Result 	= new String[m_Opponents.size()];
		Object[] Opponents	= m_Opponents.toArray();
		for (int i = 0; i < Opponents.length; i++) Result[i] = (String)Opponents[i];

		//Return
		return Result;
	}

	public String getActiveOpponent() {
		return m_Active;
	}

	public void addOpponent(String opponent) {
		//Skip if null
		if (opponent == null) return;

		//Add
		m_Opponents.add(opponent);
	}

	public void removeOpponent(String opponent) {
		//Skip if null
		if (opponent == null)                   return;
		if (!m_Opponents.contains(opponent))    return;

		//Remove
		m_Opponents.remove(opponent);
		if (m_Active != null && m_Active.equals(opponent)) m_Active = null;
	}

	public void setActiveOpponent(String id) {
		//Set as active
		m_Active = id;
	}
	
	//The only instance
	private static FriendManager s_Instance = null;
	
	//Data
	protected String						m_Active;
	protected Set<String> 					m_Opponents;
	protected Hashtable<String, Player> 	m_Friends;
}

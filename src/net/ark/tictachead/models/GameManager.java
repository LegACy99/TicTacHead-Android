package net.ark.tictachead.models;

import java.util.Hashtable;


public class GameManager {
	public GameManager() {
		//Initialize
		m_Games = new Hashtable<String, Tictactoe>();
	}

	public synchronized static GameManager instance() {
		//Create state manager if doesn't exist
		if (s_Instance == null) s_Instance = new GameManager();
		return s_Instance;
	}
	
	public Tictactoe getGame(String player) {
		//Find
		Tictactoe Result = m_Games.get(player);
		
		//If null
		if (Result == null) {
			//Create
			Result = new Tictactoe();
			m_Games.put(player, Result);
		}
		
		//Return
		return Result;
	}
	
	//The only instance
	private static GameManager s_Instance = null;
	
	//Data
	protected Hashtable<String, Tictactoe> m_Games;
}

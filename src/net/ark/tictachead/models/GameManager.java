package net.ark.tictachead.models;

import java.util.Hashtable;


public class GameManager {
	public GameManager() {
		//Initialize
		m_Games = new Hashtable<Integer, Tictactoe>();
	}

	public synchronized static GameManager instance() {
		//Create state manager if doesn't exist
		if (s_Instance == null) s_Instance = new GameManager();
		return s_Instance;
	}
	
	public Tictactoe getGame(int user) {
		//Find
		Tictactoe Result = m_Games.get(Integer.valueOf(user));
		
		//If null
		if (Result == null) {
			//Create
			Result = new Tictactoe();
			m_Games.put(Integer.valueOf(user), Result);
		}
		
		//Return
		return Result;
	}
	
	//The only instance
	private static GameManager s_Instance = null;
	
	//Data
	protected Hashtable<Integer, Tictactoe> m_Games;
}

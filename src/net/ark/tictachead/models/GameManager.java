package net.ark.tictachead.models;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;


public class GameManager {
	public GameManager() {
		//Initialize
		m_Games     = new Hashtable<String, Tictactoe>();
		m_Queueings = new HashSet<String>();
		m_Sendings  = new HashSet<String>();
		m_Loadings  = new HashSet<String>();
	}

	public synchronized static GameManager instance() {
		//Create state manager if doesn't exist
		if (s_Instance == null) s_Instance = new GameManager();
		return s_Instance;
	}

	public boolean isLoading(String player) {
		//Check
		boolean Loading = false;
		if (player != null) Loading = m_Loadings.contains(player);

		//Return
		return Loading;
	}

	public boolean isSending(String player) {
		//Check
		boolean Sending = false;
		if (player != null) Sending = m_Sendings.contains(player);

		//Return
		return Sending;
	}

	public boolean isQueueing(String player) {
		//Check
		boolean Queueing = false;
		if (player != null) Queueing = m_Queueings.contains(player);

		//Return
		return Queueing;
	}

	public Tictactoe getGame(String player) { return getGame(player, false); }
	public Tictactoe getGame(String player, boolean create) {
		//Initialize
		Tictactoe Result = null;
		if (player != null) {
			//Find
			Result = m_Games.get(player);
		
			//If null and should create
			if (Result == null && create) {
				//Create
				Result = new Tictactoe();
				m_Games.put(player, Result);
			}
		}
		
		//Return
		return Result;
	}

	public void loadGame(String player)     { m_Loadings.add(player);       }
	public void sendGame(String player)     { m_Sendings.add(player);       }
	public void queueGame(String player)    { m_Queueings.add(player);      }
	public void unloadGame(String player)   { m_Loadings.remove(player);    }
	public void unsendGame(String player)   { m_Sendings.remove(player);    }
	public void dequeueGame(String player)  { m_Queueings.remove(player);   }
	
	//The only instance
	private static GameManager s_Instance = null;
	
	//Data
	protected Hashtable<String, Tictactoe>  m_Games;
	protected Set<String>                   m_Queueings;
	protected Set<String>                   m_Sendings;
	protected Set<String>                   m_Loadings;
}

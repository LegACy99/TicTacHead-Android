package net.ark.tictachead.models;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class GameManager {
	public GameManager() {
		//Initialize
		m_Games     = new Hashtable<Long, Tictactoe>();
		m_Queueings = new HashSet<Long>();
		m_Sendings  = new HashSet<Long>();
		m_Loadings  = new HashSet<Long>();
	}

	public synchronized static GameManager instance() {
		//Create state manager if doesn't exist
		if (s_Instance == null) s_Instance = new GameManager();
		return s_Instance;
	}

	//Accessors
	public boolean isLoading(long player) 	{ return m_Loadings.contains(Long.valueOf(player));		}
	public boolean isSending(long player) 	{ return m_Sendings.contains(Long.valueOf(player)); 	}
	public boolean isQueueing(long player) 	{ return m_Queueings.contains(Long.valueOf(player)); 	}

	public Tictactoe getGame(long player) { return getGame(player, false); }
	public Tictactoe getGame(long player, boolean create) {
		//Find
		Tictactoe Result = m_Games.get(Long.valueOf(player));
	
		//If null and should create
		if (Result == null && create) {
			//Create
			Result = new Tictactoe(player);
			m_Games.put(Long.valueOf(player), Result);
		}
		
		//Return
		return Result;
	}

	public void putGame(Tictactoe game) {
		//Save
		if (game != null) {
			m_Games.put(Long.valueOf(game.getOpponent()), game);
		}
	}

	public void loadGame(long player)     { m_Loadings.add(Long.valueOf(player));       }
	public void sendGame(long player)     { m_Sendings.add(Long.valueOf(player));       }
	public void queueGame(long player)    { m_Queueings.add(Long.valueOf(player));      }
	public void unloadGame(long player)   { m_Loadings.remove(Long.valueOf(player));    }
	public void unsendGame(long player)   { m_Sendings.remove(Long.valueOf(player));    }
	public void dequeueGame(long player)  { m_Queueings.remove(Long.valueOf(player)); 	}

	public Hashtable<Long, Tictactoe> getAllGames() {
		//Return
		return m_Games;
	}
	
	//The only instance
	private static GameManager s_Instance = null;
	
	//Data
	protected Hashtable<Long, Tictactoe>  m_Games;
	protected Set<Long>                   m_Queueings;
	protected Set<Long>                   m_Sendings;
	protected Set<Long>                   m_Loadings;
}

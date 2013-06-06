package net.ark.tictachead.models;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;


public class GameManager {
	public GameManager() {
		//Initialize
		m_Games     = new Hashtable<String, Tictactoe>();
		m_Enemys    = new Hashtable<String, Tictactoe>();
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
				Result          = new Tictactoe(player);
				Tictactoe Clone = new Tictactoe(player);
				Clone.save(Result);

				//Save
				m_Games.put(player, Result);
				m_Enemys.put(player, Clone);
			}
		}
		
		//Return
		return Result;
	}

	public void putGame(Tictactoe game) {
		//Save
		if (game != null) {
			m_Games.put(String.valueOf(game.getOpponent()), game);
		}
	}

	public void loadGame(String player)     { m_Loadings.add(player);       }
	public void sendGame(String player)     { m_Sendings.add(player);       }
	public void queueGame(String player)    { m_Queueings.add(player);      }
	public void unloadGame(String player)   { m_Loadings.remove(player);    }
	public void unsendGame(String player)   { m_Sendings.remove(player);    }
	public void dequeueGame(String player)  {
		m_Queueings.remove(player);

		//Clone
		Tictactoe Clone = new Tictactoe(player);
		Clone.save(m_Games.get(player));
		m_Enemys.put(player, Clone);
	}

	public void randomSolve() {
		//Get games
		Tictactoe[] Games = getNewGames();
		if (Games.length > 0) {
			//Fix
			Tictactoe Fix = Games[new Random().nextInt(Games.length)];
			if (!Fix.isMyTurn()) {
				//Check result
				int Result = Fix.getResult();
				if (Result == Tictactoe.RESULT_INVALID) Fix.fill();
				else                                    Fix.reset();
			}
		}

		//Get game
		/*Tictactoe Game = GameManager.instance().getGame(Gamer.DUMMY1);
		if (Game == null) {
			//Create
			Game = new Tictactoe(Gamer.DUMMY1, false);
			m_Enemys.put(Gamer.DUMMY1, Game);
		}*/
	}

	public Hashtable<String, Tictactoe> getAllGames() {
		//Return
		return m_Games;
	}

	public Tictactoe[] getNewGames() {
		//Get
		int Index                       = 0;
		Tictactoe[] Result              = new Tictactoe[m_Enemys.size()];
		Enumeration<Tictactoe> Games    = m_Enemys.elements();
		while (Games.hasMoreElements()) {
			Result[Index] = Games.nextElement();
			Index++;
		}

		//Return
		return Result;
	}

	public void removeNewGame(String player) {
		//Remove
		m_Enemys.remove(player);
	}
	
	//The only instance
	private static GameManager s_Instance = null;
	
	//Data
	protected Hashtable<String, Tictactoe>  m_Games;
	protected Hashtable<String, Tictactoe>  m_Enemys;
	protected Set<String>                   m_Queueings;
	protected Set<String>                   m_Sendings;
	protected Set<String>                   m_Loadings;
}

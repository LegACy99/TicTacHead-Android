package net.ark.tictachead.helpers;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import net.ark.tictachead.models.Gamer;
import net.gogo.server.onii.api.tictachead.model.Player;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class RecordManager {
	protected RecordManager() {
		//Initializes
		m_Initialized = false;
		
		//Initialize data
		m_ID    	= -1;
		m_Email		= null;
		m_Login 	= false;
		m_Opponents = new HashSet<Long>();
		m_Friends 	= new Hashtable<Long, Gamer>();
		m_Opponent	= -1;
	}

	public synchronized static RecordManager instance() {
		//Create state manager if doesn't exist
		if (s_Instance == null) s_Instance = new RecordManager();
		return s_Instance;
	}
	
	public void initialize(Context context) {
		//Skip if already initialized
		if (m_Initialized) return;
		m_Initialized = true;
		
		//Get preference and load
		SharedPreferences Preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		loadMisc(Preference);
		loadPlayers(Preference);
	}
	
	//Accessors
	public boolean isLoggingIn()    { return m_Login;   }
	
	//Players accessors
	public long getID()       					{ return m_ID;      	}
	public String getEmail()    				{ return m_Email;   	}
	public long getActiveOpponent()				{ return m_Opponent;	}
	public Set<Long> getOpponents()				{ return m_Opponents;	}
	public Hashtable<Long, Gamer> getPlayers()	{ return m_Friends;		}
	
	//Games accessor

	
	public void login(Context context) {
		//Connecting
		m_Login = true;
		if (context != null) saveMisc(context);
	}
	
	public void stopLogin(Context context) {
		//No need to login anymore
		m_Login = false;
		if (context != null) saveMisc(context);
	}
	
	public void setPlayer(Player player, Context context) {
		//Skip if null
		if (player == null) return;
		
		//Set
		if (player.getPlayerID() != null) m_ID = player.getPlayerID().longValue();
		m_Email	= player.getUsername();
		
		//Save
		if (context != null) savePlayers(context);
	}
	
	public void setPlayers(List<Player> players, Context context) {
		//Skip if null
		if (players == null) return;
		
		//For each player
		for (int i = 0; i < players.size(); i++) {
			//Add
			Gamer NewPlayer = new Gamer(players.get(i));
			if (!m_Email.equals(NewPlayer.getName())) m_Friends.put(NewPlayer.getID(), NewPlayer);
		}
		
		//Save
		if (context != null) savePlayers(context);
	}

	public void setActiveOpponent(long opponent) {
		//Set as active opponent
		m_Opponent = opponent;
	}

	public void addOpponent(long opponent) {
		//Add
		m_Opponents.add(Long.valueOf(opponent));
	}

	public void removeOpponent(long opponent) {
		//Remove
		m_Opponents.remove(Long.valueOf(opponent));
		if (m_Opponent == opponent) m_Opponent = -1;
	}
	
	public void loadMisc(Context context) {
		//Load
		SharedPreferences Preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		loadMisc(Preference);
	}
	
	protected void loadMisc(SharedPreferences preference) {
		//Skip if empty
		if (preference == null)				return;
		if (!preference.contains(KEY_MISC))	return;
		
		//Get stored data
		String Data = preference.getString(KEY_MISC, null);
		if (Data != null) {
			try {
				//Get profile
				JSONObject JSON = new JSONObject(Data);
				m_Login         = JSON.getBoolean(JSON_LOGIN);
			} catch (JSONException e) {}
		}
	}
	
	protected void saveMisc(Context context) {
		//Skip if no context
		if (context == null) return;
		
		//Create json
		JSONObject JSON = new JSONObject();
		try {
			//Save
			JSON.put(JSON_LOGIN, m_Login);
		} catch (JSONException e) {}
		
		//Get access to preference
		/*SharedPreferences Preference 	= context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor Editor	= Preference.edit();
		
		//Save
		Editor.putString(KEY_MISC, JSON.toString());
		Editor.commit();*/
	}

	public void loadPlayers(Context context) {
		//Load
		SharedPreferences Preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		loadPlayers(Preference);
	}

	protected void loadPlayers(SharedPreferences preference) {
		//Skip if empty
		if (preference == null)				    return;
		if (!preference.contains(KEY_PLAYERS))	return;

		//Get stored data
		String Data = preference.getString(KEY_PLAYERS, null);
		if (Data != null) {
			try {
				//Get data
				JSONObject JSON = new JSONObject(Data);
				m_ID            = JSON.getLong(JSON_ID);
				m_Email         = JSON.getString(JSON_EMAIL);
			} catch (JSONException e) {}
		}
	}

	protected void savePlayers(Context context) {
		//Skip if no context
		if (context == null) return;

		//Create json
		JSONObject JSON = new JSONObject();
		try {
			//Save
			JSON.put(JSON_ID, m_ID);
			JSON.put(JSON_EMAIL, m_Email);
		} catch (JSONException e) {}

		//Get access to preference
		/*SharedPreferences Preference 	= context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor Editor	= Preference.edit();

		//Save
		Editor.putString(KEY_PLAYERS, JSON.toString());
		Editor.commit();*/
	}
	
	//Preference constants
	protected final static String PREFERENCE_NAME 	= "net.ark.tictachead";
	protected final static String KEY_PLAYERS 		= "players";
	protected final static String KEY_GAMES		    = "games";
	protected final static String KEY_MISC 		    = "misc";
	
	//Json constants
	protected final static String JSON_ID		= "ID";
	protected final static String JSON_LOGIN	= "Login";
	protected final static String JSON_EMAIL	= "Email";
	protected final static String JSON_RATING	= "Rating";
	
	//The only instance
	private static RecordManager s_Instance = null;
	
	//Misc data
	protected boolean   m_Login;
	
	//Player data
	protected long		   				m_ID;
	protected String    				m_Email;
	protected long						m_Opponent;
	protected Set<Long> 				m_Opponents;
	protected Hashtable<Long, Gamer> 	m_Friends;
	
	//Data
	protected boolean m_Initialized;
}

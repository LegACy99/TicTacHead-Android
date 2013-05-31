package net.ark.tictachead.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

public class RecordManager {
	protected RecordManager() {
		//Initializes
		m_Initialized = false;
		
		//Initialize data
		m_ID    = null;
		m_Email = null;
		m_Login = false;
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
	public String getID()       { return m_ID;      }
	public String getEmail()    { return m_Email;   }
	
	//Games accessor
	
	public void login(Context context) {
		//Connecting
		m_Login = true;
		if (context != null) saveMisc(context);
	}
	
	public void loggedIn(Context context) {
		//No need to login anymore
		m_Login = false;
		if (context != null) saveMisc(context);
	}
	
	public void setPlayer(String id, String email, Context context) {
		//Set
		m_ID 	= id;
		m_Email	= email;
		
		//Save
		if (context != null) saveMisc(context);
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
		SharedPreferences Preference 	= context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor Editor	= Preference.edit();
		
		//Save
		Editor.putString(KEY_MISC, JSON.toString());
		//Editor.commit();
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
				m_ID            = JSON.getString(JSON_ID);
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
		SharedPreferences Preference 	= context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor Editor	= Preference.edit();

		//Save
		Editor.putString(KEY_PLAYERS, JSON.toString());
		//Editor.commit();
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
	
	//Login data
	protected String    m_ID;
	protected String    m_Email;
	protected boolean   m_Login;
	
	//Data
	protected boolean m_Initialized;
}

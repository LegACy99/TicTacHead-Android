package net.ark.tictachead.services;

import java.io.IOException;
import java.util.List;

import net.ark.tictachead.helpers.RecordManager;
import net.gogo.server.onii.api.tictachead.Tictachead;
import net.gogo.server.onii.api.tictachead.model.CollectionResponsePlayer;
import net.gogo.server.onii.api.tictachead.model.Player;
import android.app.IntentService;
import android.content.Intent;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

public class PlayersService extends IntentService {
	public PlayersService() {
		super(SERVICE_NAME);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onHandleIntent(Intent intent) {	
		//Skip if no intent
		if (intent == null) return;

		//Load
		//RecordManager.instance().loadMisc(this);
		//RecordManager.instance().loadPlayers(this);
		
		//If no player
		if (RecordManager.instance().getPlayers().isEmpty()) {
			//Get connection
			Tictachead.Builder Builder 	= new Tictachead.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
			Tictachead Connection		= Builder.build();
			
			try {
				//Get all players
				CollectionResponsePlayer Result = Connection.listPlayer().execute();
				if (Result != null) {
					//Get players
					Object Items = Result.get(KEY_PLAYERS);
					if (Items != null && Items instanceof List<?>) RecordManager.instance().setPlayers((List<Player>) Items, this);
				}
			} catch (IOException e1) {
				//Stop logging in
				RecordManager.instance().stopLogin(this);
			}			
		}

		//Send broadcast saying request is done
		Intent Broadcast = new Intent(ACTION);
		sendBroadcast(Broadcast);
	}
	
	//Constants
	protected static final String KEY_PLAYERS 	= "items";
	protected static final String SERVICE_NAME 	= "net.ark.tictachead.PlayersService";
	public static final String ACTION 			= "net.ark.tictachead.Players";
}

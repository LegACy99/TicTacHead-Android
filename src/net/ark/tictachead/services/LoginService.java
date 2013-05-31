package net.ark.tictachead.services;

import java.io.IOException;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

import net.ark.tictachead.helpers.RecordManager;
import net.gogo.server.onii.api.tictachead.Tictachead;
import net.gogo.server.onii.api.tictachead.model.Player;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class LoginService extends IntentService {
	public LoginService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {	
		//Skip if no intent
		if (intent == null) return;

		//Load
		//RecordManager.instance().loadMisc(this);
		//RecordManager.instance().loadPlayers(this);

		//If no email
		if (RecordManager.instance().getEmail() == null) {
			//Get data
			String Email = intent.getStringExtra(EXTRA_EMAIL);
			if (Email != null) {
				//Get connection
				Tictachead.Builder Builder 	= new Tictachead.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Tictachead Connection		= Builder.build();
				
				//Create player
				Player NewPlayer = new Player();
				NewPlayer.setUsername(Email);
				
				try {
					//Register
					Player Result = Connection.insertPlayer(NewPlayer).execute();
					Log.e("aaa", "Result: Username" + Result.getUsername() + " id: " + Result.getPlayerID());
					RecordManager.instance().setPlayer(Email, Email, this);
				} catch (IOException e) {}

				//Load
				//Counter++;
				//if (Counter % 2 == 0) 	
				//else					RecordManager.instance().loggedIn(this);
			}
		}

		//If have email
		if (RecordManager.instance().getEmail() != null) {
			//Start player listing service
			Intent PlayersIntent = new Intent(this, PlayersService.class);
			startService(PlayersIntent);
		}

		//Send broadcast telling login is done
		Intent Broadcast = new Intent(ACTION);
		sendBroadcast(Broadcast);
	}

	protected static int Counter = 0;
	
	//Constants
	public static final String EXTRA_EMAIL		= "email";
	protected static final String SERVICE_NAME 	= "net.ark.tictachead.LoginService";
	public static final String ACTION 			= "net.ark.tictachead.Login";
}

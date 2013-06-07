package net.ark.tictachead.services;

import java.io.IOException;

import net.ark.tictachead.helpers.RecordManager;
import net.gogo.server.onii.api.tictachead.Tictachead;
import net.gogo.server.onii.api.tictachead.model.Player;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

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
					if (Result != null) {
						//Save
						RecordManager.instance().setPlayer(Result, this);
						
						GoogleCloudMessaging GCM = GoogleCloudMessaging.getInstance(this);
						Log.e("aaa", "Starting GCM");
						if (GCM != null) {
							//Registers
							Log.e("aaa", "GCM started");
							String GCMID = GCM.register("862363578865");
							Log.e("aaa", "GCMID " + GCMID);
							if (GCMID != null) {
								Result.setGcmid(GCMID);
								Log.e("aaa", "Update ID: "+ Connection.updatePlayer(Result).execute().getPlayerID());
							}
						}
					}
				} catch (IOException e) {
					//No longer logging in
					RecordManager.instance().stopLogin(this);
				}
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
	
	//Constants
	public static final String EXTRA_EMAIL		= "email";
	protected static final String SERVICE_NAME 	= "net.ark.tictachead.LoginService";
	public static final String ACTION 			= "net.ark.tictachead.Login";
}

package net.ark.tictachead.services;

import android.app.IntentService;
import android.content.Intent;
import net.ark.tictachead.activities.LoginActivity;
import net.ark.tictachead.models.GameManager;
import net.ark.tictachead.models.Tictactoe;

public class LoginService extends IntentService {
	public LoginService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {	
		//Skip if no intent
		if (intent == null) return;

		//If no email
		if (LoginActivity.Email == null) {
			//Get data
			String Email = intent.getStringExtra(EXTRA_EMAIL);
			if (Email != null) {
				//Sleep
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}

				//Load
				Counter++;
				if (Counter % 2 == 1) 	LoginActivity.Email = Email;
				else					LoginActivity.Connecting = false;
			}
		}

		//If have email
		if (LoginActivity.Email != null) {
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

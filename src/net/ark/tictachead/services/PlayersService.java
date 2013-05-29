package net.ark.tictachead.services;

import android.app.IntentService;
import android.content.Intent;
import net.ark.tictachead.activities.LoginActivity;

public class PlayersService extends IntentService {
	public PlayersService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {	
		//Skip if no intent
		if (intent == null) return;

		//If no player
		if (!LoginActivity.Players) {
			//Sleep
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}

			//Load players
			Counter++;
			if (Counter % 2 == 0) 	LoginActivity.Players = true;
			else 					LoginActivity.Connecting = false;
		}

		//Send broadcast saying request is done
		Intent Broadcast = new Intent(ACTION);
		sendBroadcast(Broadcast);
	}

	protected static int Counter = 0;
	
	//Constants
	protected static final String SERVICE_NAME 	= "net.ark.tictachead.PlayersService";
	public static final String ACTION 			= "net.ark.tictachead.Players";
}

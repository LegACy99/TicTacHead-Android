package net.ark.tictachead.services;

import net.ark.tictachead.activities.LoginActivity;
import net.ark.tictachead.helpers.RecordManager;
import android.app.IntentService;
import android.content.Intent;

public class PlayersService extends IntentService {
	public PlayersService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {	
		//Skip if no intent
		if (intent == null) return;

		//Load
		//RecordManager.instance().loadMisc(this);
		//RecordManager.instance().loadPlayers(this);
		
		//If no player
		if (!LoginActivity.Players) {
			//Sleep
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}

			//Load players
			Counter++;
			if (Counter % 3 == 0) 	LoginActivity.Players = true;
			else 					RecordManager.instance().loggedIn(this);
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

package net.ark.tictachead.services;

import android.app.IntentService;
import android.content.Intent;

import net.ark.tictachead.activities.GameActivity;
import net.ark.tictachead.activities.LoginActivity;
import net.ark.tictachead.models.GameManager;

public class RoomService extends IntentService {
	public RoomService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {	
		//Skip if no intent
		if (intent == null) return;

		//Get data
		String Opponent = intent.getStringExtra(EXTRA_OPPONENT);
		if (Opponent != null) {
			//Sleep
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}

			//Get game
			GameManager.instance().getGame(Opponent, true);
			GameManager.instance().unloadGame(Opponent);
		}

		//Send broadcast telling room is get
		Intent Broadcast = new Intent(GameActivity.GAME_CHANGED);
		Broadcast.putExtra(EXTRA_OPPONENT, Opponent);
		sendBroadcast(Broadcast);
	}
	
	//Constants
	public static final String EXTRA_OPPONENT	= "opponent";
	protected static final String SERVICE_NAME 	= "net.ark.tictachead.RoomService";
}

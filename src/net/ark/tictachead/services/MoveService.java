package net.ark.tictachead.services;

import android.app.IntentService;
import android.content.Intent;

import net.ark.tictachead.activities.GameActivity;
import net.ark.tictachead.models.GameManager;
import net.ark.tictachead.models.Tictactoe;

public class MoveService extends IntentService {
	public MoveService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {	
		//Skip if no intent
		if (intent == null) return;

		//Get data
		String Opponent = intent.getStringExtra(EXTRA_OPPONENT);
		if (Opponent != null) {
			//Get game
			Tictactoe Game = GameManager.instance().getGame(Opponent);
			if (Game != null) {
				//Sleep
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {}

				//Game is sent
				GameManager.instance().unsendGame(Opponent);
				GameManager.instance().dequeueGame(Opponent);

				//Send broadcast telling to refresh game
				Intent Broadcast = new Intent(GameActivity.GAME_CHANGED);
				Broadcast.putExtra(EXTRA_OPPONENT, Opponent);
				sendBroadcast(Broadcast);
			}
		}
	}
	
	//Constants
	public static final String EXTRA_OPPONENT	= "opponent";
	protected static final String SERVICE_NAME 	= "net.ark.tictachead.MoveService";
}

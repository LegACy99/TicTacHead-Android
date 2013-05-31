package net.ark.tictachead.services;

import android.app.IntentService;
import android.content.Intent;

import net.ark.tictachead.activities.GameActivity;
import net.ark.tictachead.models.GameManager;
import net.ark.tictachead.models.Tictactoe;

import java.util.ArrayList;
import java.util.Hashtable;

public class RoomsService extends IntentService {
	public RoomsService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {	
		//Skip if no intent
		if (intent == null) return;

		//Sleep
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}

		//Get rooms from server
		ArrayList<String> Changes   = new ArrayList<String>();
		Tictactoe[] NewGames        = GameManager.instance().getNewGames();
		if (NewGames != null && NewGames.length > 0) {
			//get current games
			Hashtable<String, Tictactoe> Games = GameManager.instance().getAllGames();
			for (int i = 0; i < NewGames.length; i++) {
				//Get stuff
				Tictactoe NewGame   = NewGames[i];
				Tictactoe Game      = Games.get(NewGame.getOpponent());
				boolean IsSent      = !GameManager.instance().isQueueing(NewGame.getOpponent());

				//If game is already sent, and mine say enemy turn + server said my turn
				if (IsSent && !Game.isMyTurn() && NewGame.isMyTurn()) {
					//Save
					Changes.add(NewGame.getOpponent());
					GameManager.instance().getGame(NewGame.getOpponent()).save(NewGame);
				}
			}
		}

		//If there's changes
		if (!Changes.isEmpty()) {
			//Create array
			String[] Opponents = new String[Changes.size()];
			for (int i = 0; i < Opponents.length; i++) Opponents[i] = Changes.get(i);

			//Send broadcast telling there's change
			Intent Broadcast = new Intent(GameActivity.GAME_CHANGED);
			Broadcast.putExtra(EXTRA_OPPONENTS, Opponents);
			sendBroadcast(Broadcast);
		}
	}
	
	//Constants
	public static final String EXTRA_OPPONENTS	= "opponents";
	protected static final String SERVICE_NAME 	= "net.ark.tictachead.RoomsService";
}

package net.ark.tictachead.services;

import android.app.IntentService;
import android.content.Intent;

import net.ark.tictachead.activities.GameActivity;
import net.ark.tictachead.models.FriendManager;
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
		ArrayList<String> New       = new ArrayList<String>();
		ArrayList<String> Changes   = new ArrayList<String>();
		Tictactoe[] NewGames        = GameManager.instance().getNewGames();
		if (NewGames != null && NewGames.length > 0) {
			//get current games
			Hashtable<String, Tictactoe> Games = GameManager.instance().getAllGames();
			for (int i = 0; i < NewGames.length; i++) {
				//Get existing games
				Tictactoe NewGame   = NewGames[i];
				Tictactoe Game      = Games.get(NewGame.getOpponent());
				if (Game != null) {
					//If game is already sent, and mine say enemy turn + server said my turn
					boolean IsSent = !GameManager.instance().isQueueing(NewGame.getOpponent());
					if (IsSent && !Game.isMyTurn() && NewGame.isMyTurn()) {
						//Save
						Changes.add(NewGame.getOpponent());
						GameManager.instance().getGame(NewGame.getOpponent()).save(NewGame);
					}
				} else {
					//Clone game
					Tictactoe Clone = new Tictactoe(NewGame.getOpponent());
					Clone.save(NewGame);

					//Add new game
					New.add(NewGame.getOpponent());
					GameManager.instance().putGame(Clone);
					FriendManager.instance().addOpponent(NewGame.getOpponent());
					FriendManager.instance().setActiveOpponent(NewGame.getOpponent());
				}
			}

			//Remove
			for (int i = 0; i < New.size(); i++)        GameManager.instance().removeNewGame(New.get(i));
			for (int i = 0; i < Changes.size(); i++)    GameManager.instance().removeNewGame(Changes.get(i));
		}

		//If there's update game
		if (!Changes.isEmpty() || !New.isEmpty()) {
			//Show and create head
			Intent HeadIntent = new Intent(this, HeadService.class);
			HeadIntent.putExtra(HeadService.EXTRA_CREATE, true);
			startService(HeadIntent);

			//Send broadcast telling there's change
			Intent Broadcast = new Intent(GameActivity.GAME_CHANGED);
			Broadcast.putStringArrayListExtra(EXTRA_CHALLENGES, New);
			Broadcast.putStringArrayListExtra(EXTRA_OPPONENTS, Changes);
			sendBroadcast(Broadcast);
		}
	}
	
	//Constants
	public static final String EXTRA_OPPONENTS	= "opponents";
	public static final String EXTRA_CHALLENGES = "challenges";
	protected static final String SERVICE_NAME 	= "net.ark.tictachead.RoomsService";
	public static final String ACTION       	= "net.ark.tictachead.Rooms";
}

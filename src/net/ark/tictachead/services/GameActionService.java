package net.ark.tictachead.services;

import net.ark.tictachead.models.GameManager;
import net.ark.tictachead.models.Tictactoe;
import android.app.IntentService;
import android.content.Intent;

public class GameActionService extends IntentService {
	public GameActionService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {	
		//Skip if no intent
		if (intent == null) return;
		
		//Get data
		int X 			= intent.getIntExtra(EXTRA_X, -1);
		int Y 			= intent.getIntExtra(EXTRA_Y, -1);
		String UserID 	= intent.getStringExtra(EXTRA_USER);
		if (X >= 0 && Y >= 0 && UserID != null) {
			//Get game
			Tictactoe Game = GameManager.instance().getGame(UserID);
			if (Game != null) {
				//Move
				Game.fill(X, Y);
				
				//Send broadcast that game has been changed
				Intent Broadcast = new Intent(Tictactoe.CHANGE_BROADCAST);
				Broadcast.putExtra(Tictactoe.EXTRA_USER, UserID);
				sendBroadcast(Broadcast);
			}
		}
	}
	
	//Constants
	public static final String EXTRA_X			= "x";
	public static final String EXTRA_Y			= "y";
	public static final String EXTRA_USER		= "user";
	protected static final String SERVICE_NAME 	= "net.ark.tictachead.GameMoveService";
}

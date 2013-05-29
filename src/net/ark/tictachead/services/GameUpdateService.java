package net.ark.tictachead.services;

import net.ark.tictachead.models.GameManager;
import net.ark.tictachead.models.Tictactoe;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class GameUpdateService extends Service {
	@Override
	public IBinder onBind(Intent intent) {
		//Do not bind
		return null;
	}

	@Override
	public void onCreate() {
		//Super
		super.onCreate();

		//Create receiver
		IntentFilter DummyFilter = new IntentFilter();
		DummyFilter.addAction(DUMMY_BROADCAST);
		registerReceiver(m_UpdateReceiver, DummyFilter);
	}

    protected BroadcastReceiver m_UpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//If no intent
			if (intent == null) return;
			
			//Get user
			String UserID = intent.getStringExtra(EXTRA_USER);
			if (UserID != null) {
				//Get game
				Tictactoe Game = GameManager.instance().getGame(UserID);
				if (Game != null) {
					//Move
					Game.fill();
					
					//Send broadcast that game has been changed
					Intent Broadcast = new Intent(Tictactoe.CHANGE_BROADCAST);
					Broadcast.putExtra(Tictactoe.EXTRA_USER, UserID);
					sendBroadcast(Broadcast);
				}
			}
		}
	};
	
	//Constants
	public static final String EXTRA_USER 		= "user";
	public static final String DUMMY_BROADCAST 	= "net.ark.tictachead.GameUpdate";
}

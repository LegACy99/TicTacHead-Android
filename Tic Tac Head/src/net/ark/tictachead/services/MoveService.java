package net.ark.tictachead.services;

import java.io.IOException;

import net.ark.tictachead.activities.GameActivity;
import net.ark.tictachead.helpers.RecordManager;
import net.ark.tictachead.models.GameManager;
import net.ark.tictachead.models.Tictactoe;
import net.gogo.server.onii.api.tictachead.Tictachead;
import net.gogo.server.onii.api.tictachead.model.Room;
import android.app.IntentService;
import android.content.Intent;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

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
				//Get connection
				Tictachead.Builder Builder 	= new Tictachead.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
				Tictachead Connection		= Builder.build();
				
				try {
					//Get room
					Room Result = Connection.updateRoom(Game.createRoom()).execute();
					if (Result != null && Result.getFinished().booleanValue()) {
						//Get room
						Room NewRoom = Connection.getCoupleRoom(Long.valueOf(RecordManager.instance().getID()), Long.valueOf(Opponent)).execute();
						if (NewRoom != null) GameManager.instance().putGame(new Tictactoe(NewRoom));
					}
				} catch (IOException e) {}
				
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

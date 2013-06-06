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
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

public class RoomService extends IntentService {
	public RoomService() {
		super(SERVICE_NAME);
	}

	@Override
	protected void onHandleIntent(Intent intent) {	
		//Skip if no intent
		if (intent == null) return;

		//Get data
		long RoomID		= intent.getLongExtra(EXTRA_ROOM, -1);
		String Opponent = intent.getStringExtra(EXTRA_OPPONENT);
		if (Opponent != null || RoomID >= 0) {
			//Get connection
			Tictachead.Builder Builder 	= new Tictachead.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), null);
			Tictachead Connection		= Builder.build();
			
			try {
				//Get room
				Room NewRoom = null;
				Log.e("aaa", "Read Room: " + RoomID);
				if (Opponent != null) NewRoom = Connection.getCoupleRoom(Long.valueOf(RecordManager.instance().getID()), Long.valueOf(Opponent)).execute();
				else if (RoomID >= 0) NewRoom = Connection.getRoom(Long.valueOf(RoomID)).execute();
				
				Log.e("aaa", "NewRoom " + NewRoom.getGameState());
				
				if (NewRoom != null && !NewRoom.getFinished().booleanValue()) {
					Tictactoe Result = new Tictactoe(NewRoom);
					GameManager.instance().putGame(Result);
					Opponent = String.valueOf(Result.getOpponent());
				}
			} catch (IOException e) {}

			//Stop loading
			GameManager.instance().unloadGame(Opponent);
		}

		//Send broadcast telling room is get
		Intent Broadcast = new Intent(GameActivity.GAME_CHANGED);
		Broadcast.putExtra(EXTRA_OPPONENT, Opponent);
		sendBroadcast(Broadcast);
	}
	
	//Constants
	public static final String EXTRA_ROOM		= "room";
	public static final String EXTRA_OPPONENT	= "opponent";
	protected static final String SERVICE_NAME 	= "net.ark.tictachead.RoomService";
}

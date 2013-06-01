package net.ark.tictachead.receivers;

import net.ark.tictachead.services.RoomService;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GCMReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		//Return if no intent
		if (intent == null) return;
		
		//Get GCM
		Log.e("aaa", "GCM receive");
		GoogleCloudMessaging GCM = GoogleCloudMessaging.getInstance(context);
		if (GCM != null) {
			//Check message
			String Type = GCM.getMessageType(intent);
			if (Type != null && Type.equals(GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE)) {
				//Check
				String DataType = intent.getStringExtra("type");
				if (DataType != null && DataType.equals("room")) {
					//Get room ID
					String RoomID = intent.getStringExtra("roomID");
					Log.e("aaa", "GCM ROOM: " + RoomID);
					if (RoomID != null) {
						//Start service
						Intent RoomIntent = new Intent(context, RoomService.class);
						RoomIntent.putExtra(RoomService.EXTRA_ROOM, Long.valueOf(RoomID).longValue());
						context.startService(RoomIntent);
					}
				}
			}
		}
	}

}

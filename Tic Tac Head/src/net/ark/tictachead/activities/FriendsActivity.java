package net.ark.tictachead.activities;

import net.ark.tictachead.R;
import net.ark.tictachead.adapters.FriendsAdapter;
import net.ark.tictachead.models.FriendManager;
import net.ark.tictachead.models.Gamer;
import net.ark.tictachead.services.HeadService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class FriendsActivity extends Activity implements OnItemClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		
		//Set layout
		setContentView(R.layout.friends_layout);
		
		//Get list view
		View FriendList = findViewById(R.id.list_friends);
		if (FriendList != null && FriendList instanceof ListView) {
			//Set adapter
			FriendsAdapter Adapter = new FriendsAdapter(this, FriendManager.instance().getFriends());
			((ListView)FriendList).setOnItemClickListener(this);
			((ListView)FriendList).setAdapter(Adapter);
		}
	}

	@Override
	protected void onResume() {
		//Super
		super.onResume();

		//Register game receiver
		IntentFilter GameFilter = new IntentFilter();
		GameFilter.addAction(GameActivity.GAME_CHANGED);
		registerReceiver(m_GameReceiver, GameFilter);

		//Hide head
		Intent HeadIntent = new Intent(this, HeadService.class);
		HeadIntent.putExtra(HeadService.EXTRA_SHOW, false);
		startService(HeadIntent);
	}

	@Override
	protected void onPause() {
		//Super
		super.onPause();

		//Remove receivers
		unregisterReceiver(m_GameReceiver);
	}

	@Override
	protected void onStop() {
		//Super
		super.onStop();

		//Show head again
		Intent HeadIntent = new Intent(this, HeadService.class);
		HeadIntent.putExtra(HeadService.EXTRA_SHOW, true);
		startService(HeadIntent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		//Get opponent
		Gamer Opponent = FriendManager.instance().getFriends().get(position);
		if (Opponent != null) {
			//Add
			FriendManager.instance().addOpponent(Opponent.getID());
			FriendManager.instance().setActiveOpponent(Opponent.getID());

			//Create head
			Intent HeadIntent = new Intent(this, HeadService.class);
			HeadIntent.putExtra(HeadService.EXTRA_CREATE, true);
			startService(HeadIntent);

			//Open game
			startActivity(new Intent(this, GameActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}

	protected BroadcastReceiver m_GameReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//Skip if no intent
			if (intent == null) return;

			//Hide head
			Intent HeadIntent = new Intent(context, HeadService.class);
			HeadIntent.putExtra(HeadService.EXTRA_SHOW, false);
			startService(HeadIntent);
		}
	};
}

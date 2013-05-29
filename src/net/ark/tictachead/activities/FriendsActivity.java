package net.ark.tictachead.activities;

import java.util.ArrayList;

import net.ark.tictachead.R;
import net.ark.tictachead.adapters.FriendsAdapter;
import net.ark.tictachead.models.FriendManager;
import net.ark.tictachead.models.Player;
import net.ark.tictachead.services.HeadService;
import android.app.Activity;
import android.content.Intent;
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
			((ListView)FriendList).setAdapter(Adapter);
		}
	}

	@Override
	protected void onStart() {
		//Super
		super.onStart();

		//Hide head
		Intent HeadIntent = new Intent(this, HeadService.class);
		HeadIntent.putExtra(HeadService.EXTRA_SHOW, false);
		startService(HeadIntent);
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
		Player Opponent = FriendManager.instance().getFriends().get(position);
		if (Opponent != null) {
			//Add
			FriendManager.instance().addOpponent(Opponent.getID());

			//Start head service
			Intent HeadIntent = new Intent(this, HeadService.class);
			HeadIntent.putExtra(HeadService.EXTRA_CREATE, true);
			startService(HeadIntent);

			//Open game
			startActivity(new Intent(this, GameActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}
}

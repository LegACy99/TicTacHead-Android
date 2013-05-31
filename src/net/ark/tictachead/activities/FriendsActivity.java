package net.ark.tictachead.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import net.ark.tictachead.R;
import net.ark.tictachead.adapters.FriendsAdapter;
import net.ark.tictachead.models.FriendManager;
import net.ark.tictachead.models.GameManager;
import net.ark.tictachead.models.Player;
import net.ark.tictachead.services.HeadService;
import net.ark.tictachead.services.RoomsService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

		ScheduledExecutorService RoomsScheduler = Executors.newScheduledThreadPool(2);
		RoomsScheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				//Run rooms service
				startService(new Intent(getApplicationContext(), RoomsService.class));
			}
		}, 3, 3, TimeUnit.SECONDS);

		ScheduledExecutorService GameAIScheduler = Executors.newScheduledThreadPool(2);
		GameAIScheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				//Solve
				GameManager.instance().randomSolve();
			}
		}, 6, 6, TimeUnit.SECONDS);
	}

	@Override
	protected void onResume() {
		//Super
		super.onResume();

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
			FriendManager.instance().setActiveOpponent(Opponent.getID());

			//Create head
			Intent HeadIntent = new Intent(this, HeadService.class);
			HeadIntent.putExtra(HeadService.EXTRA_CREATE, true);
			startService(HeadIntent);

			//Open game
			startActivity(new Intent(this, GameActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}
}

package net.ark.tictachead.activities;

import net.ark.tictachead.R;
import net.ark.tictachead.services.HeadService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class FriendsActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		
		//Set layout
		setContentView(R.layout.friends_layout);

        //Get button
		View FriendButton = findViewById(R.id.button_friend);
		if (FriendButton != null) FriendButton.setOnClickListener(this);
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
	public void onClick(View v) {
		//Skip if no view
		if (v == null) return;
		
		//Check id
		if (v.getId() == R.id.button_friend) {
			//Start service
			Intent HeadIntent = new Intent(this, HeadService.class);
			HeadIntent.putExtra(HeadService.EXTRA_CREATE, true);
			startService(HeadIntent);
		}
	}
}

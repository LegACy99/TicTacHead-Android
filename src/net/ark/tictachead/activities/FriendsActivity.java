package net.ark.tictachead.activities;

import net.ark.tictachead.R;
import net.ark.tictachead.services.HeadService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class FriendsActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		
		//Set layout
		setContentView(R.layout.friends_layout);
		
		//Get button
		View FriendButton = findViewById(R.id.button_friend);
		if (FriendButton != null && FriendButton instanceof Button) ((Button)FriendButton).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		//Skip if no view
		if (v == null) return;
		
		//Check id
		if (v.getId() == R.id.button_friend) {
			//Statt service
			startService(new Intent(this, HeadService.class));
		}
	}
}

package net.ark.tictachead.activities;

import net.ark.tictachead.R;
import android.app.Activity;
import android.os.Bundle;

public class FriendsActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		
		//Set layout
		setContentView(R.layout.friends_layout);
	}
}

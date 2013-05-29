package net.ark.tictachead.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import net.ark.tictachead.R;
import net.ark.tictachead.models.Tictactoe;
import net.ark.tictachead.services.LoginService;
import net.ark.tictachead.services.PlayersService;

public class LoginActivity extends Activity implements View.OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		
		//Set layout
		setContentView(R.layout.login_layout);

		//Set listener
		View ButtonLogin = findViewById(R.id.button_login);
		if (ButtonLogin != null) ButtonLogin.setOnClickListener(this);

		//If logging in
		if (Connecting) {
			//Connect
			showConnecting();
			if (Email != null) {
				//Request player
				showListing();
				if (Players) goToPlayers();
			}
		}
	}

	@Override
	protected void onResume() {
		//Super
		super.onResume();

		//Register login receiver
		IntentFilter LoginFilter = new IntentFilter();
		LoginFilter.addAction(LoginService.ACTION);
		registerReceiver(m_LoginReceiver, LoginFilter);

		//Register player list receiver
		IntentFilter PlayersFilter = new IntentFilter();
		PlayersFilter.addAction(PlayersService.ACTION);
		registerReceiver(m_PlayersReceiver, PlayersFilter);
	}

	@Override
	protected void onPause() {
		//Super
		super.onPause();

		//Remove receivers
		unregisterReceiver(m_LoginReceiver);
		unregisterReceiver(m_PlayersReceiver);
	}

	@Override
	public void onClick(View view) {
		//Skip if no view
		if (view == null) return;

		//If login
		if (view.getId() == R.id.button_login) {
			//Connect
			Connecting = true;
			showConnecting();

			//Start service
			Intent LoginIntent = new Intent(this, LoginService.class);
			LoginIntent.putExtra(LoginService.EXTRA_EMAIL, "email@email.com");
			startService(LoginIntent);
		}
	}

	protected void showConnecting() {
		//Get button
		View ButtonLogin = findViewById(R.id.button_login);
		if (ButtonLogin != null && ButtonLogin instanceof Button) {
			//Configure
			((Button)ButtonLogin).setText(R.string.login_connecting);
			ButtonLogin.setEnabled(false);
		}

		//Hide instruction
		View LabelInstruction = findViewById(R.id.label_instruction);
		if (LabelInstruction != null) LabelInstruction.setVisibility(View.INVISIBLE);

		//Show progress wheel
		View ProgressWheel = findViewById(R.id.progress_connect);
		if (ProgressWheel != null && ProgressWheel instanceof ProgressBar) ProgressWheel.setVisibility(View.VISIBLE);
	}

	protected void showListing() {
		//Configure button
		View ButtonLogin = findViewById(R.id.button_login);
		if (ButtonLogin != null && ButtonLogin instanceof Button) ((Button)ButtonLogin).setText(R.string.login_listing);
	}

	protected void reset() {
		//Get button
		View ButtonLogin = findViewById(R.id.button_login);
		if (ButtonLogin != null && ButtonLogin instanceof Button) {
			//Configure
			((Button)ButtonLogin).setText(R.string.login_connect);
			ButtonLogin.setEnabled(true);
		}

		//Hide instruction
		View LabelInstruction = findViewById(R.id.label_instruction);
		if (LabelInstruction != null) LabelInstruction.setVisibility(View.VISIBLE);

		//Show progress wheel
		View ProgressWheel = findViewById(R.id.progress_connect);
		if (ProgressWheel != null && ProgressWheel instanceof ProgressBar) ProgressWheel.setVisibility(View.INVISIBLE);
	}

	protected void goToPlayers() {
		//Go to class activity
		startActivity(new Intent(this, FriendsActivity.class));

		//Kill self
		finish();
	}

	protected BroadcastReceiver m_LoginReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//If email successful, show player listing, otherwise reset
			if (Email != null) 	showListing();
			else 				reset();
		}
	};

	protected BroadcastReceiver m_PlayersReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//If player is got, go to player list, otherwise reset
			if (Players) 	goToPlayers();
			else 			reset();
		}
	};

	public static String Email 			= null;
	public static boolean Connecting 	= false;
	public static boolean Players 		= false;
}

package net.ark.tictachead.activities;

import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.ark.tictachead.R;
import net.ark.tictachead.helpers.RecordManager;
import net.ark.tictachead.services.LoginService;
import net.ark.tictachead.services.PlayersService;
import android.accounts.AccountManager;
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

public class LoginActivity extends Activity implements View.OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		
		//Initialize record manager
		RecordManager.instance().initialize(this);

		//Set listener
		View ButtonLogin = findViewById(R.id.button_login);
		if (ButtonLogin != null) ButtonLogin.setOnClickListener(this);

		//If logging in
		if (RecordManager.instance().isLoggingIn()) {
			//Connect
			showConnecting();
			if (RecordManager.instance().getEmail() != null) {
				//Request player
				showListing();
				if (!RecordManager.instance().getPlayers().isEmpty()) goToPlayers();
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
			//Check google play service
			int Status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
			if (Status == ConnectionResult.SUCCESS) {
				//Open account picker
				Intent AccountIntent = AccountPicker.newChooseAccountIntent(null, null, ACCOUNT_TYPES, false, null, null, null, null);
				if (AccountIntent != null) startActivityForResult(AccountIntent, REQUEST_ACCOUNT);	
			} else Log.e("Google Play", GooglePlayServicesUtil.getErrorString(Status));
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Super
		super.onActivityResult(requestCode, resultCode, data);
		
		//If request is fine
		if (requestCode == REQUEST_ACCOUNT && resultCode == RESULT_OK) {
			//If data exist
			if (data != null) {
				//Get name
				String Name = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
				if (Name != null) {
					//Connect
					RecordManager.instance().login(this);
					showConnecting();
	
					//Start service
					Intent LoginIntent = new Intent(this, LoginService.class);
					LoginIntent.putExtra(LoginService.EXTRA_EMAIL, Name);
					startService(LoginIntent);
				}
			}
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
			if (RecordManager.instance().getEmail() != null) 	showListing();
			else 												reset();
		}
	};

	protected BroadcastReceiver m_PlayersReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//If player is got, go to player list, otherwise reset
			if (!RecordManager.instance().getPlayers().isEmpty()) 	goToPlayers();
			else 													reset();
		}
	};

	//Constants
	protected static final String[] ACCOUNT_TYPES	= { "com.google" };
	protected static final int REQUEST_ACCOUNT		= 1000;
}

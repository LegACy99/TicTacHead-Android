package net.ark.tictachead.activities;

import net.ark.tictachead.R;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

public class GameActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		
		//No title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//Set layout
		setContentView(R.layout.game_layout);
		
		//findViewById(R.id.layout_game).setOnClickListener(this);
		findViewById(R.id.button_close).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		//
		Log.e("aaa", "View:" + v.getClass());
		finish();
	}
}

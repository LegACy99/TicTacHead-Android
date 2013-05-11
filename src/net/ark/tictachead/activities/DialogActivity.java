package net.ark.tictachead.activities;

import net.ark.tictachead.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class DialogActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//Set layout
		setContentView(R.layout.dialog_layout);
	}
}

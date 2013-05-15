package net.ark.tictachead.activities;

import net.ark.tictachead.R;
import net.ark.tictachead.services.HeadService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class GameActivity extends Activity implements OnClickListener, OnTouchListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		
		//Set layout
		setContentView(R.layout.game_layout);
		
		//Set listeners
		findViewById(R.id.layout_game).setOnTouchListener(this);
		findViewById(R.id.image_head1).setOnClickListener(this);
		findViewById(R.id.image_head2).setOnClickListener(this);
		findViewById(R.id.button_close).setOnClickListener(this);
		
		//Initialize
		m_TouchedOutside = false;
	}

	@Override
	protected void onStop() {
		//Super
		super.onStop();
		
		//Show head again
		Intent HeadIntent = new Intent(this, HeadService.class);
		HeadIntent.putExtra(HeadService.EXTRA_HEAD, true);
		startService(HeadIntent);
	}

	@Override
	public void onClick(View v) {
		//Skip no view 
		if (v == null) return;
		
		//Check view ID
		if (v.getId() == R.id.button_close || v.getId() == R.id.image_head1 || v.getId() == R.id.image_head2) {
			//Done
			finish();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//Initialize
		boolean Consume = false;
		
		//If root
		if (v.getId() == R.id.layout_game && v instanceof ViewGroup) {
			//If down
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				//Get position
				float X = event.getRawX();
				float Y = event.getRawY();
				
				//While not consuming and not all children
				int i 	= 0;
				Consume	= true;
				while (i < ((ViewGroup)v).getChildCount() && Consume) {
					//Get child
					View Child = ((ViewGroup)v).getChildAt(i);
					if (Child != null) {
                        //Get position
                        int[] Position	= new int[] { 0, 0 };
                        Child.getLocationInWindow(Position);

                        //If inside view, do not consume
                        if (X >= Position[0] && Y >= Position[1] && X <= Position[0] + Child.getWidth() && Y <= Position[1] + Child.getHeight()) Consume = false;
                    }
					
					//Next
					i++;
				}
				
				//If consumed, outside
				if (Consume) m_TouchedOutside = true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				//If touched outside
				if (m_TouchedOutside) {
					//TODO: Check if released outside
					finish();
				}
				
				//Not touching outside
				m_TouchedOutside = false;
			}
		}
		
		//Return
		return Consume;
	}
	
	//Data
	protected boolean m_TouchedOutside;
}

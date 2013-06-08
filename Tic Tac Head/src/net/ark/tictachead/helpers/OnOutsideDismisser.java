package net.ark.tictachead.helpers;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class OnOutsideDismisser implements OnTouchListener {
	public OnOutsideDismisser(Activity activity) {
		//Save activity
		m_Activity = activity;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//Initialize
		boolean Consume = false;
		
		//If root
		if (m_Activity != null && v != null && v instanceof ViewGroup) {
			//If down
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				//Get position
				float X = event.getRawX();
				float Y = event.getRawY();
				
				//If outside, consumed
				m_TouchedOutside = !isInsideView(X, Y, (ViewGroup)v);
				if (m_TouchedOutside) Consume = true;
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				//If touched outside
				if (m_TouchedOutside) {
					//Consume
					Consume = true;

					//If outside, finish
					float X = event.getRawX();
					float Y = event.getRawY();
					if (!isInsideView(X, Y, (ViewGroup)v)) m_Activity.finish();
				}
				
				//Not touching outside
				m_TouchedOutside = false;
			}
		}
		
		//Return
		return Consume;
	}

	protected boolean isInsideView(float x, float y, ViewGroup parent) {
		//Initialize
		boolean Inside = false;

		//While not consuming and not all children
		int i 	= 0;
		while (i < parent.getChildCount() && !Inside) {
			//Get child
			View Child = parent.getChildAt(i);
			if (Child != null) {
				//Get position
				int[] Position	= new int[] { 0, 0 };
				Child.getLocationInWindow(Position);

				//If inside view, do not consume
				if (x >= Position[0] && y >= Position[1] && x <= Position[0] + Child.getWidth() && y <= Position[1] + Child.getHeight()) Inside = true;
			}

			//Next
			i++;
		}

		//Return
		return Inside;
	}
	
	//Data
	protected Activity 	m_Activity;
	protected boolean 	m_TouchedOutside;
}

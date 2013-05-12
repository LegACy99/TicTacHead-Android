package net.ark.tictachead.services;

import net.ark.tictachead.R;
import net.ark.tictachead.activities.GameActivity;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;

public class HeadService extends Service implements OnClickListener, OnTouchListener {
	@Override
	public IBinder onBind(Intent intent) {
		//Do nothing
		return null;
	}

	@Override
	public void onCreate() {
		//Super
		super.onCreate();
		
		//Get window manager
		WindowManager Manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		if (Manager != null) {
			//Create layour parameter
			WindowManager.LayoutParams Parameters = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT, 
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT
			);
			
			//Configure parameter
			Parameters.gravity 	= Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			Parameters.y		= 100;
			
			//Create head
			ImageView Head = new ImageView(this);
			Head.setImageResource(R.drawable.ic_launcher);
			Head.setOnClickListener(this);
			
			//Add to window
			m_Head = Head;
			Manager.addView(m_Head, Parameters);
		}
	}
	
	@Override
	public void onDestroy() {
		//Super
		super.onDestroy();

		//Get window manager
		WindowManager Manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		if (Manager != null) {
			//Remove head
			if (m_Head != null) Manager.removeView(m_Head);
		}
	}
	@Override
	public void onClick(View v) {
		//If no view, return
		if (v == null) return;
		
		//If head is clicked
		if (v == m_Head) {
			//Open dialog activity
			startActivity(new Intent(this, GameActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		//Initialize
		boolean Consume = false;
		
		//If view exist
		if (v != null && v == m_Head) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
			}
		}
		
		//Return
		return Consume;
	}
	
	//Data
	protected View m_Head;
}

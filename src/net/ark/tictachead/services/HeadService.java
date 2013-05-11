package net.ark.tictachead.services;

import net.ark.tictachead.activities.DialogActivity;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

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
			//Parameters.gravity = Gravity.TOP | Gravity.LEFT;
			
			//Create head
			TextView Head = new TextView(this);	
			Head.setOnClickListener(this);
			Head.setText("A HEAD! \\o/");
			
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
			//Create dialog
			Toast.makeText(this, "A toast!", Toast.LENGTH_SHORT).show();
			
			//
			startActivity(new Intent(this, DialogActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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
	protected View 		m_Head;
}

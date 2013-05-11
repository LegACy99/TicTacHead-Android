package net.ark.tictachead.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class HeadService extends Service {
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
			Parameters.gravity = Gravity.TOP | Gravity.LEFT;
			
			//Create head
			TextView Head = new TextView(this);	
			Head.setText("HEAD!");
			
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
	
	//Data
	protected View m_Head;
}

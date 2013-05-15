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
		//Do not bind
		return null;
	}

	@Override
	public void onCreate() {
		//Super
		super.onCreate();
		
		//Get window manager
		WindowManager Manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		if (Manager != null) {
			//Create and add head
			if (m_Head == null || m_Parameters == null) createHead();
			Manager.addView(m_Head, m_Parameters);
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		//Super
		super.onStartCommand(intent, flags, startID);
		
		//If intent exist
		if (intent != null) {
			//Show or hide head?
			boolean Show = intent.getBooleanExtra(EXTRA_HEAD, true);
			if (Show) 	showHead();
			else		hideHead();
		}
		
		//Started as sticky
		return Service.START_STICKY;
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

	protected void createHead() {		
		//Create head
		ImageView Head = new ImageView(this);
		Head.setImageResource(R.drawable.ic_launcher);
		Head.setOnClickListener(this);
		m_Head = Head;
		
		//Create layout parameters
		m_Parameters = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT, 
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT
		);
		
		//Configure parameters
		m_Parameters.gravity 	= Gravity.TOP | Gravity.CENTER_HORIZONTAL;
		m_Parameters.y			= 200;
	}
	
	protected void showHead() {
		m_Head.setVisibility(View.VISIBLE);
	}
	
	protected void hideHead() {
		m_Head.setVisibility(View.GONE);
	}
	
	@Override
	public void onClick(View v) {
		//If no view, return
		if (v == null) return;
		
		//If head is clicked
		if (v == m_Head) {
			//Open dialog activity
			startActivity(new Intent(this, GameActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			hideHead();
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
	
	//Constants
	public static final String EXTRA_HEAD = "Head";
	
	//Data
	protected View 							m_Head;
	protected WindowManager.LayoutParams 	m_Parameters;
}

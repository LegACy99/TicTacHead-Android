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

		//Initialize
		m_Head          = null;
		m_Parameters    = null;
		m_Dragging      = false;
		m_InitialX      = 0;
		m_InitialY      = 0;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		//Super
		super.onStartCommand(intent, flags, startID);
		
		//If intent exist
		if (intent != null) {
			//Has create data?
			if (intent.hasExtra(EXTRA_CREATE)) {
				//If should create
				if (intent.getBooleanExtra(EXTRA_CREATE, false)) {
					//Get window manager
					WindowManager Manager = (WindowManager) getSystemService(WINDOW_SERVICE);
					if (Manager != null) {
						//Create and add head
						if (m_Head == null || m_Parameters == null) createHead();
						Manager.addView(m_Head, m_Parameters);
					}
				}
			}

			//If head exist
			if (m_Head != null && m_Parameters != null) {
				//Show or hide head?
				boolean Show = intent.getBooleanExtra(EXTRA_SHOW, true);
				if (Show) 	showHead();
				else		hideHead();
			}
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
			//Check touch event
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				//Save initial position
				m_InitialX  = event.getRawX();
				m_InitialY  = event.getRawY();
				m_OffsetX   = m_InitialX - m_Parameters.x;
				m_OffsetY   = m_InitialY - m_Parameters.y;

				//Not dragging
				m_Dragging = false;
			} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
				//Get current position
				float CurrentX  = event.getRawX();
				float CurrentY  = event.getRawY();

				//If not dragging yet
				if (!m_Dragging) {
					//Check offset
					float OffsetX = CurrentX - m_InitialX;
					float OffsetY = CurrentY - m_InitialY;
					if ((OffsetX * OffsetX) + (OffsetY * OffsetY) >= MINIMUM_OFFSET * MINIMUM_OFFSET) m_Dragging = true;
				}

				//If dragging
				if (m_Dragging) {
					//Get window manager
					WindowManager Manager = (WindowManager) getSystemService(WINDOW_SERVICE);
					if (Manager != null) {
						//Set position
						m_Parameters.x = (int)(CurrentX - m_OffsetX);
						m_Parameters.y = (int)(CurrentY - m_OffsetY);

						//Refresh
						Manager.updateViewLayout(m_Head, m_Parameters);
					}
				}
			}

			//If dragging, consume
			if (m_Dragging) Consume = true;
		}

		//Return
		return Consume;
	}

	protected void createHead() {
		//Create head
		ImageView Head = new ImageView(this);
		Head.setImageResource(R.drawable.ic_launcher);
		Head.setOnTouchListener(this);
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
		m_Parameters.gravity 	= Gravity.TOP | Gravity.LEFT;
		m_Parameters.y			= 200;
	}
	
	protected void showHead() {
		//Show
		if (m_Head != null) m_Head.setVisibility(View.VISIBLE);
	}
	
	protected void hideHead() {
		//Hide
		if (m_Head != null) m_Head.setVisibility(View.GONE);
	}
	
	//Constants
	public static final String EXTRA_SHOW       = "Show";
	public static final String EXTRA_CREATE     = "Create";
	protected static final float MINIMUM_OFFSET = 8f;
	
	//Data
	protected View 							m_Head;
	protected float                         m_OffsetX;
	protected float                         m_OffsetY;
	protected float                         m_InitialX;
	protected float                         m_InitialY;
	protected boolean                       m_Dragging;
	protected WindowManager.LayoutParams 	m_Parameters;
}

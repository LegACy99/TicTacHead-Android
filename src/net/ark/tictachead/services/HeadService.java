package net.ark.tictachead.services;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import net.ark.tictachead.R;
import net.ark.tictachead.activities.GameActivity;

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
		m_Delete		= null;
		m_HeadParams    = null;
		m_DeleteParams	= null;
		m_Dragging      = false;
		m_Deleting		= false;
		m_InitialX      = 0;
		m_InitialY      = 0;
		m_OriginX     	= 0;
		m_OriginY      	= 0;

		//Get resources
		Resources Resource = getResources();
		if (Resource != null) {
			//Calculate
			m_MinOffset 	= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MINIMUM_OFFSET, Resource.getDisplayMetrics());
			m_MinDistance 	= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, MINIMUM_DISTANCE, Resource.getDisplayMetrics());
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startID) {
		//Super
		super.onStartCommand(intent, flags, startID);
		
		//If intent exist
		if (intent != null) {
			//If create
			if (intent.getBooleanExtra(EXTRA_CREATE, false)) {
				//If no head
				if (m_Head == null || m_Delete == null || m_HeadParams == null || m_DeleteParams == null) {
					//Create and add head
					createDelete();
					createHead();
				}
			}

			//If kill
			if (intent.getBooleanExtra(EXTRA_KILL, false)) stopSelf();

			//If head exist
			if (m_Head != null && m_HeadParams != null) {
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
			//Remove views
			if (m_Head != null) 	Manager.removeView(m_Head);
			if (m_Delete != null)	Manager.removeView(m_Delete);
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
				m_OriginX	= m_HeadParams.x;
				m_OriginY	= m_HeadParams.y;
				m_InitialX  = event.getRawX();
				m_InitialY  = event.getRawY();

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
					if ((OffsetX * OffsetX) + (OffsetY * OffsetY) >= m_MinOffset * m_MinOffset) {
						//Start dragging
						m_Dragging = true;
						showDelete();
					}
				}

				//If dragging
				if (m_Dragging) {
					//Get window manager
					WindowManager Manager = (WindowManager) getSystemService(WINDOW_SERVICE);
					if (Manager != null) {
						//Set position
						m_HeadParams.x 						= (int)(m_OriginX + (CurrentX - m_InitialX));
						m_HeadParams.y 						= (int)(m_OriginY - (CurrentY - m_InitialY));
						WindowManager.LayoutParams Params 	= m_HeadParams;

						//If close to the delete region
						float DistanceX = m_HeadParams.x - m_DeleteParams.x;
						float DistanceY = m_HeadParams.y - m_DeleteParams.y;
						if ((DistanceX * DistanceX) + (DistanceY * DistanceY) <= m_MinDistance * m_MinDistance) {
							//Deleting
							m_Deleting 	= true;
							Params 		= m_DeleteParams;
						} else m_Deleting = false;

						//Refresh
						Manager.updateViewLayout(m_Head, Params);
					}
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				//Hide delete
				hideDelete();

				//Kill if deleting
				if (m_Deleting) stopSelf();
				m_Deleting = false;
			}

			//If dragging
			if (m_Dragging) {
				//Consume
				Consume = true;

				//If touch is removed, no more dragging
				if (event.getAction() == MotionEvent.ACTION_UP) m_Dragging = false;
			}
		}

		//Return
		return Consume;
	}

	protected void createHead() {
		//Get window manager
		WindowManager Manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		if (Manager != null) {
			//Create head
			ImageView Head = new ImageView(this);
			Head.setImageResource(R.drawable.icon);
			Head.setOnTouchListener(this);
			Head.setOnClickListener(this);
			m_Head = Head;

			//Create layout parameters
			m_HeadParams = new WindowManager.LayoutParams(
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_PHONE,
					WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
					PixelFormat.TRANSLUCENT
			);

			//Get screen size
			Point ScreenSize		= new Point();
			Display ScreenDisplay	= Manager.getDefaultDisplay();
			if (ScreenDisplay != null) ScreenDisplay.getSize(ScreenSize);

			//Configure parameters
			m_HeadParams.gravity 	= Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
			m_HeadParams.y			= (int) (ScreenSize.y * 0.67f);

			//Add
			Manager.addView(m_Head, m_HeadParams);
		}
	}

	protected void createDelete() {
		//Get window manager
		WindowManager Manager = (WindowManager) getSystemService(WINDOW_SERVICE);
		if (Manager != null) {
			//Create head
			ImageView Delete = new ImageView(this);
			Delete.setImageResource(R.drawable.delete);
			Delete.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
			m_Delete = Delete;

			//Create layout parameters
			m_DeleteParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT
			);

			//Configure parameters
			m_DeleteParams.gravity 	= Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
			m_DeleteParams.y		= (int) m_MinDistance;

			//Add
			Manager.addView(m_Delete, m_DeleteParams);
			hideDelete();
		}
	}
	
	protected void showHead() {
		//Show
		if (m_Head != null) m_Head.setVisibility(View.VISIBLE);
	}

	protected void showDelete() {
		if (m_Delete != null) m_Delete.setVisibility(View.VISIBLE);
	}
	
	protected void hideHead() {
		//Hide
		if (m_Head != null) m_Head.setVisibility(View.GONE);
	}

	protected void hideDelete() {
		if (m_Delete != null) m_Delete.setVisibility(View.GONE);
	}
	
	//Constants
	public static final String EXTRA_SHOW       	= "Show";
	public static final String EXTRA_KILL       	= "Kill";
	public static final String EXTRA_CREATE       	= "Create";
	protected static final float MINIMUM_DISTANCE	= 56;
	protected static final float MINIMUM_OFFSET 	= 4f;
	
	//Views
	protected View 							m_Head;
	protected View 							m_Delete;
	protected WindowManager.LayoutParams 	m_HeadParams;
	protected WindowManager.LayoutParams 	m_DeleteParams;

	//Data
	protected float 	m_OriginX;
	protected float     m_OriginY;
	protected float     m_InitialX;
	protected float     m_InitialY;
	protected float		m_MinOffset;
	protected float		m_MinDistance;
	protected boolean   m_Dragging;
	protected boolean	m_Deleting;
}

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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends Activity implements OnClickListener, OnTouchListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		
		//Set layout
		setContentView(R.layout.game_layout);
		
		//Set listeners
		findViewById(R.id.layout_game).setOnTouchListener(this);
		findViewById(R.id.button_close).setOnClickListener(this);
		findViewById(R.id.image_friends).setOnClickListener(this);
		
		//Initialize
		m_Heads             = new ArrayList<ImageView>();
		m_TouchedOutside    = false;
		addHead();
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
		switch (v.getId()) {
		case R.id.button_close:
			//Add new head
			addHead();
			break;

		case R.id.image_friends:
			//Go to friends activity
			addHead();

		default:
			//Remove head
			removeHead(v.getId() - 1000);
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

	protected void addHead() {
		//Get base layout
		View Root = findViewById(R.id.layout_game);
		if (Root != null && Root instanceof  RelativeLayout) {
			//Create head
			ImageView Head = new ImageView(this);
			Head.setImageResource(R.drawable.ic_launcher);
			Head.setId(m_Heads.size() + 1000);
			Head.setOnClickListener(this);

			//TODO: Generate ID
			//TODO: Calculate margin with DP

			//Create parameters
			int Wrap = RelativeLayout.LayoutParams.WRAP_CONTENT;
			RelativeLayout.LayoutParams Parameters = new RelativeLayout.LayoutParams(Wrap, Wrap);
			Parameters.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			Parameters.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			Parameters.setMargins(0, 0, 8, 0);

			//Get left view
			View Left = findViewById(R.id.image_friends);
			if (!m_Heads.isEmpty()) Left = m_Heads.get(m_Heads.size() - 1);

			//Add
			m_Heads.add(Head);
			((RelativeLayout)Root).addView(Head, Parameters);

			//If there's a left view
			if (Left != null) {
				//Get params
				Parameters = (RelativeLayout.LayoutParams)Left.getLayoutParams();
				if (Parameters != null) {
					//Configure
					Parameters.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
					Parameters.addRule(RelativeLayout.LEFT_OF, Head.getId());
					Parameters.setMargins(0, 0, 4, 0);
				}
			}
		}
	}

	protected void removeHead(int index) {
		//Skip if index not valid
		if (index < 0)                  return;
		if (index >= m_Heads.size())    return;

		//Get base layout
		View Root = findViewById(R.id.layout_game);
		if (Root != null && Root instanceof ViewGroup) {
			//Remove view
			((ViewGroup) Root).removeView(m_Heads.get(index));
			m_Heads.remove(index);

			//Get right device
			View Right = null;
			if (index < m_Heads.size()) Right = m_Heads.get(index);
			if (Right != null) {
				//Get left
				View Left = findViewById(R.id.image_friends);
				if (index > 0) Left = m_Heads.get(index - 1);
				if (Left != null) {
					//Configure params
					RelativeLayout.LayoutParams Parameters = (RelativeLayout.LayoutParams)Left.getLayoutParams();
					if (Parameters != null) Parameters.addRule(RelativeLayout.LEFT_OF, Right.getId());
				}
			} else {
				//Find the correct right
				if (m_Heads.isEmpty())  Right = findViewById(R.id.image_friends);
				else                    Right = m_Heads.get(index - 1);

				//If exist
				if (Right != null) {
					//Get params
					RelativeLayout.LayoutParams Parameters = (RelativeLayout.LayoutParams)Right.getLayoutParams();
					if (Parameters != null) {
						//Configure
						Parameters.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
						Parameters.addRule(RelativeLayout.LEFT_OF, 0);
						Parameters.setMargins(0, 0, 8, 0);
					}
				}
			}
		}
	}
	
	//Data
	protected boolean           m_TouchedOutside;
	protected List<ImageView>   m_Heads;
}

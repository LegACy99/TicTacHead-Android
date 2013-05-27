package net.ark.tictachead.activities;

import net.ark.tictachead.R;
import net.ark.tictachead.models.Tictactoe;
import net.ark.tictachead.services.HeadService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.content.res.Resources;
import android.util.TypedValue;
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

		//Initialize UI
		m_Heads             = new ArrayList<ImageView>();
		m_TouchedOutside	= false;

		//Create game
		m_Game = new Tictactoe();
		if (!m_Game.isMyTurn()) m_Game.fill();

		//Create board
		m_Board = new View[][]{
			new View[]{ findViewById(R.id.view_cell00), findViewById(R.id.view_cell01), findViewById(R.id.view_cell02)  },
			new View[]{ findViewById(R.id.view_cell10), findViewById(R.id.view_cell11), findViewById(R.id.view_cell12)  },
			new View[]{ findViewById(R.id.view_cell20), findViewById(R.id.view_cell21), findViewById(R.id.view_cell22)  }
		};

		//Initialize game
		addHead();
		configureBoard();
		
		//Set listeners
		findViewById(R.id.layout_game).setOnTouchListener(this);
		findViewById(R.id.image_friends).setOnClickListener(this);
		for (int i = 0; i < m_Board.length; i++) for (int j = 0; j < m_Board[i].length; j++) m_Board[i][j].setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		//Super
		super.onStart();

		//Hide head
		Intent HeadIntent = new Intent(this, HeadService.class);
		HeadIntent.putExtra(HeadService.EXTRA_SHOW, false);
		startService(HeadIntent);
	}

	@Override
	protected void onStop() {
		//Super
		super.onStop();
		
		//Show head again
		Intent HeadIntent = new Intent(this, HeadService.class);
		HeadIntent.putExtra(HeadService.EXTRA_SHOW, true);
		startService(HeadIntent);
	}

	@Override
	public void onClick(View v) {
		//Skip no view 
		if (v == null) return;
		
		//Check view ID
		switch (v.getId()) {
		case R.id.image_friends:
			//Go to friends activity
			startActivity(new Intent(this, FriendsActivity.class));
			break;

		case R.id.view_cell00:
		case R.id.view_cell10:
		case R.id.view_cell20:
		case R.id.view_cell01:
		case R.id.view_cell11:
		case R.id.view_cell21:
		case R.id.view_cell02:
		case R.id.view_cell12:
		case R.id.view_cell22:
			//If not full
			if (!m_Game.isFull()) {
				//Get X,Y, and if empty
				int Y = getCellRow(v);
				int X = getCellColumn(v);
				if (m_Game.getStatus(X, Y) == Tictactoe.EMPTY_CELL) {
					//Do game stuff
					m_Game.fill(X, Y);
					if (!m_Game.isFull()) m_Game.fill();

					//Draw
					configureBoard();
				}
			}
			break;

		default:
			//Remove head
			removeHead(v.getId() - 1000);
			break;
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
		if (Root != null && Root instanceof RelativeLayout) {
			//Initialize margin
			float MarginGap     = 0;
			float MarginOffset  = 0;
			if (getResources() != null && getResources().getDisplayMetrics() != null) {
				//Calculate
				MarginGap     = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
				MarginOffset  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
			}

			//Create head
			ImageView Head = new ImageView(this);
			Head.setImageResource(R.drawable.ic_launcher);
			Head.setId(m_Heads.size() + 1000);
			Head.setOnClickListener(this);

			//TODO: Generate ID

			//Create parameters
			int Wrap = RelativeLayout.LayoutParams.WRAP_CONTENT;
			RelativeLayout.LayoutParams Parameters = new RelativeLayout.LayoutParams(Wrap, Wrap);
			Parameters.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
			Parameters.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			Parameters.setMargins(0, 0, (int)MarginOffset, 0);

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
					Parameters.setMargins(0, 0, (int)MarginGap, 0);
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
			//Calculate margin
			float Margin = 0;
			if (getResources() != null && getResources().getDisplayMetrics() != null)Margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

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
						Parameters.setMargins(0, 0, (int)Margin, 0);
					}
				}
			}
		}
	}

	protected void configureBoard() {
		//Get resources
		Resources Resource = getResources();
		if (Resource == null) return;

		//Get game status
		int[][] Status = m_Game.getStatus();
		for (int x = 0; x < m_Board.length; x++) {
			for (int y = 0; y < m_Board.length; y++) {
				//Check status
				int ColorID = android.R.color.transparent;
				if (Status[x][y] == Tictactoe.SELF_CELL) 		ColorID = android.R.color.holo_green_light;
				else if (Status[x][y] == Tictactoe.ENEMY_CELL) 	ColorID = android.R.color.holo_red_light;

				//Set color
				if (m_Board[x][y] != null) m_Board[x][y].setBackgroundColor(Resource.getColor(ColorID));
			}
		}
	}

	protected int getCellColumn(View cell) {
		//Initialize
		int Column = -1;

		//For each cell
		for (int x = 0; x < m_Board.length && Column == -1; x++) {
			for (int y = 0; y < m_Board.length && Column == -1; y++) {
				//If same, get column
				if (m_Board[x][y] == cell) Column = x;
			}
		}

		//Return
		return Column;
	}

	protected int getCellRow(View cell) {
		//Initialize
		int Row = -1;

		//For each cell
		for (int x = 0; x < m_Board.length && Row == -1; x++) {
			for (int y = 0; y < m_Board.length && Row == -1; y++) {
				//If same, get row
				if (m_Board[x][y] == cell) Row = y;
			}
		}

		//Return
		return Row;
	}
	
	//Data
	protected Tictactoe			m_Game;
	protected List<ImageView>   m_Heads;
	protected View[][]			m_Board;
	protected boolean           m_TouchedOutside;
}

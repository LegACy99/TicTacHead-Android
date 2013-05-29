package net.ark.tictachead.activities;

import java.util.ArrayList;
import java.util.List;

import net.ark.tictachead.R;
import net.ark.tictachead.models.FriendManager;
import net.ark.tictachead.models.GameManager;
import net.ark.tictachead.models.Player;
import net.ark.tictachead.models.Tictactoe;
import net.ark.tictachead.services.GameActionService;
import net.ark.tictachead.services.GameUpdateService;
import net.ark.tictachead.services.HeadService;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends Activity implements OnClickListener, OnTouchListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		
		//Set layout
		setContentView(R.layout.game_layout);

		//Initialize
		m_TouchedOutside	= false;
		m_Heads             = new ArrayList<ImageView>();
		m_Board 			= new View[][]{
			new View[]{ findViewById(R.id.view_cell00), findViewById(R.id.view_cell01), findViewById(R.id.view_cell02)  },
			new View[]{ findViewById(R.id.view_cell10), findViewById(R.id.view_cell11), findViewById(R.id.view_cell12)  },
			new View[]{ findViewById(R.id.view_cell20), findViewById(R.id.view_cell21), findViewById(R.id.view_cell22)  }
		};
		
		//Set listeners
		findViewById(R.id.layout_game).setOnTouchListener(this);
		findViewById(R.id.button_play).setOnClickListener(this);
		findViewById(R.id.image_friends).setOnClickListener(this);
		for (int i = 0; i < m_Board.length; i++) for (int j = 0; j < m_Board[i].length; j++) m_Board[i][j].setOnClickListener(this);

		//Add opponents
		int[] Opponents = FriendManager.instance().getOpponents();
		for (int i = 0; i < Opponents.length; i++) addHead(Opponents[i]);
		setActiveUser(FriendManager.instance().getActiveOpponent());
		
		//Start game service
		startService(new Intent(this, GameUpdateService.class));
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
	protected void onResume() {
		//Super
		super.onResume();
		
		//Register board receiver
		IntentFilter ChangeFilter = new IntentFilter();
		ChangeFilter.addAction(Tictactoe.CHANGE_BROADCAST);
		registerReceiver(m_ChangeReceiver, ChangeFilter);
	}

	@Override
	protected void onPause() {
		//Super
		super.onPause();
		
		//Remove receivers
		unregisterReceiver(m_ChangeReceiver);
	}

	@Override
	public void onClick(View v) {
		//Skip no view 
		if (v == null) return;
		
		//Get game
		Tictactoe Game = GameManager.instance().getGame(m_ActiveUser);
		
		//Check view ID
		switch (v.getId()) {
		case R.id.image_friends:
			//Go to friends activity
			startActivity(new Intent(this, FriendsActivity.class));
			break;

		case R.id.button_play:
			//If game exist
			if (Game != null) {
				//Reset game
				Game.reset();
				if (!Game.isMyTurn()) Game.fill();

				//Redraw canvas
				refreshDisplay(Game);
			}

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
			//If game exist and not full yet
			if (Game != null && Game.isMyTurn() && !Game.isFull()) {
				//Get X,Y, and if empty
				int Y = getCellRow(v);
				int X = getCellColumn(v);
				if (Game.getStatus(X, Y) == Tictactoe.EMPTY_CELL) {
					//TODO: Ensure game cannot be filled
					
					//Create intent
					Intent MoveIntent = new Intent(this, GameActionService.class);
					MoveIntent.putExtra(GameActionService.EXTRA_USER, m_ActiveUser);
					MoveIntent.putExtra(GameActionService.EXTRA_Y, Y);
					MoveIntent.putExtra(GameActionService.EXTRA_X, X);
					startService(MoveIntent);
				}
			}
			break;

		default:
			//Change user
			setActiveUser(v.getId() - 1000);
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

	protected void addHead(int user) {
		//Get base layout
		View Root = findViewById(R.id.layout_game);
		if (Root != null && Root instanceof RelativeLayout) {
			//Get user
			Player Data = FriendManager.instance().getFriend(user);
			if (Data != null) {
				//Initialize margin
				float MarginGap     = 0;
				float MarginOffset  = 0;
				if (getResources() != null && getResources().getDisplayMetrics() != null) {
					//Calculate
					MarginGap     = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
					MarginOffset  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
				}

				//Create game
				GameManager.instance().getGame(user);

				//Create head
				ImageView Head = new ImageView(this);
				Head.setImageResource(Data.getResourceID());
				Head.setOnClickListener(this);
				Head.setId(1000 + user);

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
	
	protected void setActiveUser(int user) {
		//Save
		m_ActiveUser = user;
		FriendManager.instance().setActiveOpponent(user);

		//Get user name
		String Name 	= "Nobody";
		Player Opponent = FriendManager.instance().getFriend(user);
		if (Opponent != null) Name = Opponent.getName();

		//Set title
		View LabelTitle = findViewById(R.id.label_title);
		if (LabelTitle != null && LabelTitle instanceof TextView) ((TextView)LabelTitle).setText("Game with " + Name);

		//Get game
		Tictactoe Game = GameManager.instance().getGame(m_ActiveUser);
		if (Game != null) refreshDisplay(Game);
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
	
	protected void refreshDisplay(Tictactoe game) {
		//Skip if no game
		if (game == null) return;
		
		//Process result
		int Result 			= game.getResult();
		View BoardLayout    = findViewById(R.id.layout_board);
		View ResultLayout   = findViewById(R.id.layout_result);
		if (BoardLayout != null)    BoardLayout.setVisibility(Result == Tictactoe.RESULT_INVALID ? View.VISIBLE : View.GONE);
		if (ResultLayout != null)   ResultLayout.setVisibility(Result == Tictactoe.RESULT_INVALID ? View.GONE : View.VISIBLE);
		
		//Redraw board if invalid
		if (Result == Tictactoe.RESULT_INVALID) drawBoard(game);
		else {
			//Set text
			StringBuilder Builder = new StringBuilder();
			switch (Result) {
			case Tictactoe.RESULT_WIN:
				Builder.append("You win!");
				break;

			case Tictactoe.RESULT_LOSE:
				Builder.append("You lose!");
				break;

			case Tictactoe.RESULT_DRAW:
				Builder.append("Draw!");
				break;
			}

			//Set text
			View ResultLabel = findViewById(R.id.label_result);
			if (ResultLabel != null && ResultLabel instanceof TextView) ((TextView) ResultLabel).setText(Builder.toString());
		}
	}

	protected void drawBoard(Tictactoe game) {
		//Get resources
		Resources Resource = getResources();
		if (Resource == null) 	return;
		if (game == null)		return;

		//Get game status
		int[][] Status = game.getStatus();
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

    protected BroadcastReceiver m_ChangeReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//Skip if no intent
			if (intent == null) return;
			
			//Get User
			int UserID = intent.getIntExtra(Tictactoe.EXTRA_USER, -1);
			if (UserID >= 0) {
				//Get game
				Tictactoe Game = GameManager.instance().getGame(UserID);
				if (Game != null) {
					//Update if user
					if (m_ActiveUser == UserID) refreshDisplay(Game);
					
					//If enemy turn
					if (!Game.isMyTurn() && Game.getResult() == Tictactoe.RESULT_INVALID) {
						//Broadcast asking for movement
						Intent Broadcast = new Intent(GameUpdateService.DUMMY_BROADCAST);
						Broadcast.putExtra(GameUpdateService.EXTRA_USER, UserID);
						sendBroadcast(Broadcast);
					}
				}
			}
		}
	};
	
	//Data
	protected List<ImageView>   m_Heads;
	protected View[][]			m_Board;
	protected boolean           m_TouchedOutside;
	protected int				m_ActiveUser;
}

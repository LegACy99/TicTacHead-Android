package net.ark.tictachead.activities;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import net.ark.tictachead.R;
import net.ark.tictachead.fragments.TictactoeFragment;
import net.ark.tictachead.helpers.OnOutsideDismisser;
import net.ark.tictachead.helpers.RecordManager;
import net.ark.tictachead.helpers.Utilities;
import net.ark.tictachead.models.FriendManager;
import net.ark.tictachead.models.GameManager;
import net.ark.tictachead.models.Gamer;
import net.ark.tictachead.models.Tictactoe;
import net.ark.tictachead.services.HeadService;
import net.ark.tictachead.services.RoomService;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameActivity extends Activity implements OnClickListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Super
		super.onCreate(savedInstanceState);
		
		//Set layout
		setContentView(R.layout.game_layout);

		//If no opponent, finish
		if (RecordManager.instance().getActiveOpponent() == RecordManager.NO_USER) finish();
		else {
			//Initialize
			m_HeadsTable		= new Hashtable<Long, View>();
			m_UsersTable        = new Hashtable<View, Long>();
			m_Opponents         = new ArrayList<Long>();

			//Set listeners
			findViewById(R.id.image_friends).setOnClickListener(this);
			findViewById(R.id.layout_game).setOnTouchListener(new OnOutsideDismisser(this));

			//Add opponents
			long[] Opponents = RecordManager.instance().getOpponents();
			for (int i = Opponents.length - 1; i >= 0; i--) addUser(Opponents[i]);

			//Should load game?
			boolean Load    = false;
			long Active		= FriendManager.instance().getActiveOpponent();
			Tictactoe Game  = GameManager.instance().getGame(Active);
			if (Game == null)           Load = true;
			else if (!Game.isMyTurn())  Load = true;

			//If load
			if (Load && !GameManager.instance().isLoading(Active)) {
				//Start service
				Intent RoomIntent = new Intent(this, RoomService.class);
				RoomIntent.putExtra(RoomService.EXTRA_OPPONENT, Active);
				startService(RoomIntent);

				//Load
				GameManager.instance().loadGame(Active);
			}

			//Set user
			setActiveUser(Active);
		}
	}
	
	@Override
	protected void onResume() {
		//Super
		super.onResume();

		//Register game receiver
		IntentFilter GameFilter = new IntentFilter();
		GameFilter.addAction(GAME_CHANGED);
		registerReceiver(m_GameReceiver, GameFilter);

		//Hide head
		Intent HeadIntent = new Intent(this, HeadService.class);
		HeadIntent.putExtra(HeadService.EXTRA_SHOW, false);
		startService(HeadIntent);
	}

	@Override
	protected void onPause() {
		//Super
		super.onPause();
		
		//Remove receivers
		unregisterReceiver(m_GameReceiver);

		//Show head again
		Intent HeadIntent = new Intent(this, HeadService.class);
		HeadIntent.putExtra(HeadService.EXTRA_SHOW, true);
		startService(HeadIntent);
	}

	@Override
	public void onClick(View v) {
		//Skip no view 
		if (v == null) return;
		
		//Set user
		Long User = m_UsersTable.get(v);
		if (User != null) setActiveUser(User.longValue());
	}

	protected void addUser(long user) {
		//Get base layout
		View Root = findViewById(R.id.layout_game);
		if (Root != null && Root instanceof RelativeLayout) {
			//Get user
			Gamer Data = FriendManager.instance().getFriend(user);
			if (Data != null) {
				//Initialize margin
				float MarginGap     = 0;
				float MarginOffset  = 0;
				if (getResources() != null && getResources().getDisplayMetrics() != null) {
					//Calculate
					MarginGap     = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
					MarginOffset  = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
				}

				//Create game
				GameManager.instance().getGame(user);

				//Create head
				ImageView Head = new ImageView(this);
				Head.setImageResource(Data.getResourceID());
				Head.setId(Utilities.generateViewID());
				Head.setOnClickListener(this);

				//Create parameters
				int Wrap = RelativeLayout.LayoutParams.WRAP_CONTENT;
				RelativeLayout.LayoutParams Parameters = new RelativeLayout.LayoutParams(Wrap, Wrap);
				Parameters.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
				Parameters.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				Parameters.setMargins(0, 0, (int) MarginOffset, 0);
				((RelativeLayout)Root).addView(Head, Parameters);

				//Get left view
				View Left = findViewById(R.id.image_friends);
				if (!m_Opponents.isEmpty()) Left = m_HeadsTable.get(m_Opponents.get(m_Opponents.size() - 1));

				//Save
				m_Opponents.add(Long.valueOf(user));
				m_UsersTable.put(Head, Long.valueOf(user));
				m_HeadsTable.put(Long.valueOf(user), Head);

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
	
	public void removeOpponent() {
		//Remove
		removeUser(m_ActiveUser);
		if (m_ActiveUser == NO_USER) {
			//Kill head
			Intent HeadIntent = new Intent(this, HeadService.class);
			HeadIntent.putExtra(HeadService.EXTRA_KILL, true);
			startService(HeadIntent);

			//Done
			finish();
		}
	}

	protected void removeUser(long user) {
		//Get head and root
		View Head = m_HeadsTable.get(Long.valueOf(user));
		View Root = findViewById(R.id.layout_game);
		if (Head != null && Root != null && Root instanceof ViewGroup) {
			//Calculate margin
			float Margin = 0;
			if (getResources() != null && getResources().getDisplayMetrics() != null)Margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());

			//Get index
			int Index = -1;
			for (int i = 0; i < m_Opponents.size() && Index < 0; i++) if (m_Opponents.get(i).longValue() == user) Index = i;

			//Remove
			m_UsersTable.remove(Head);
			m_Opponents.remove(Long.valueOf(user));
			m_HeadsTable.remove(Long.valueOf(user));
			((ViewGroup) Root).removeView(Head);
			FriendManager.instance().removeOpponent(user);
			if (m_ActiveUser == user) m_ActiveUser = NO_USER;

			//Get right view
			View Right = null;
			if (Index < m_Opponents.size()) Right = m_HeadsTable.get(m_Opponents.get(Index));
			if (Right != null) {
				//If no user and there's a head
				if (m_ActiveUser == NO_USER && m_UsersTable.contains(Right)) {
					//Get user and set active
					long NewUser = m_UsersTable.get(Right).longValue();
					setActiveUser(NewUser);
				}

				//Get left
				View Left = findViewById(R.id.image_friends);
				if (Index > 0) Left = m_HeadsTable.get(m_Opponents.get(Index - 1));
				if (Left != null) {
					//Configure params
					RelativeLayout.LayoutParams Parameters = (RelativeLayout.LayoutParams)Left.getLayoutParams();
					if (Parameters != null) Parameters.addRule(RelativeLayout.LEFT_OF, Right.getId());
				}
			} else {
				//Find the correct right
				if (m_Opponents.isEmpty())  Right = findViewById(R.id.image_friends);
				else                        Right = m_HeadsTable.get(m_Opponents.get(Index - 1));

				//If exist
				if (Right != null) {
					//If no user and there's a head
					if (m_ActiveUser == NO_USER && m_UsersTable.contains(Right)) {
						//Get user and set active
						long NewUser = m_UsersTable.get(Right).longValue();
						setActiveUser(NewUser);
					}

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
	
	protected void setActiveUser(long user) {
		//Save
		m_ActiveUser = user;
		FriendManager.instance().setActiveOpponent(Long.valueOf(user).longValue());

		//Get user name
		String Name 	= "Nobody";
		Gamer Opponent = FriendManager.instance().getFriend(Long.valueOf(user).longValue());
		if (Opponent != null) Name = Opponent.getName();

		//Set title
		View LabelTitle = findViewById(R.id.label_title);
		if (LabelTitle != null && LabelTitle instanceof TextView) ((TextView)LabelTitle).setText(Name);

		//Get game
		Tictactoe Game 			= GameManager.instance().getGame(m_ActiveUser);
		Fragment GameFragment 	= getFragmentManager().findFragmentById(R.id.game_fragment);
		if (GameFragment != null && GameFragment instanceof TictactoeFragment) ((TictactoeFragment)GameFragment).refreshDisplay(Game);
	}

	protected BroadcastReceiver m_GameReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//Skip if no intent
			if (intent == null)         	return;
			if (m_ActiveUser == NO_USER)   return;

			//Get opponent
			long Opponent = intent.getLongExtra(RoomService.EXTRA_OPPONENT, NO_USER);
			if (Opponent != NO_USER && Opponent == m_ActiveUser) {
				//Refresh game display
				Tictactoe Game 			= GameManager.instance().getGame(Opponent);
				Fragment GameFragment 	= getFragmentManager().findFragmentById(R.id.game_fragment);
				if (GameFragment != null && GameFragment instanceof TictactoeFragment) ((TictactoeFragment)GameFragment).refreshDisplay(Game);

				//Hide head
				Intent HeadIntent = new Intent(context, HeadService.class);
				HeadIntent.putExtra(HeadService.EXTRA_SHOW, false);
				startService(HeadIntent);
			}
		}
	};

	//Constants
	protected static final long NO_USER 	= -1;
	public static final String GAME_CHANGED = "net.ark.tictachead.game";

	//Data
	protected List<Long>   			m_Opponents;
	protected Hashtable<Long, View>	m_HeadsTable;
	protected Hashtable<View, Long>	m_UsersTable;
	protected long					m_ActiveUser;
}

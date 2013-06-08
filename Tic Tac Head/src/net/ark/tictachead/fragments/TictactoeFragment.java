package net.ark.tictachead.fragments;

import net.ark.tictachead.R;
import net.ark.tictachead.activities.GameActivity;
import net.ark.tictachead.helpers.RecordManager;
import net.ark.tictachead.models.GameManager;
import net.ark.tictachead.models.Tictactoe;
import net.ark.tictachead.services.MoveService;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class TictactoeFragment extends Fragment implements OnClickListener {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//Initialize
		m_View = null;
		if (inflater != null) {
			//Inflate view
			m_View = inflater.inflate(R.layout.tictactoe_layout, container, false);
			if (m_View != null) {
				//Get board views
				m_Board = new View[][]{
					new View[]{ m_View.findViewById(R.id.view_cell00), m_View.findViewById(R.id.view_cell01), m_View.findViewById(R.id.view_cell02)  },
					new View[]{ m_View.findViewById(R.id.view_cell10), m_View.findViewById(R.id.view_cell11), m_View.findViewById(R.id.view_cell12)  },
					new View[]{ m_View.findViewById(R.id.view_cell20), m_View.findViewById(R.id.view_cell21), m_View.findViewById(R.id.view_cell22)  }
				};

				//Set listeners
				m_View.findViewById(R.id.button_play).setOnClickListener(this);
				m_View.findViewById(R.id.button_close).setOnClickListener(this);
				for (int i = 0; i < m_Board.length; i++) for (int j = 0; j < m_Board[i].length; j++) m_Board[i][j].setOnClickListener(this);	
			}
		}
		
		//Return
		return m_View;
	}

	@Override
	public void onClick(View view) {
		//Skip no view 
		if (view == null) return;
		
		//Get game
		Tictactoe Game = GameManager.instance().getGame(RecordManager.instance().getActiveOpponent());
		
		//Check view ID
		switch (view.getId()) {
		case R.id.button_play:
			//If game exist
			if (Game != null) {
				//If my turn and host exist
				if ((Game.isMyTurn()) && getActivity() != null) {
					//Request server to recreate game
					Intent MoveIntent = new Intent(getActivity(), MoveService.class);
					MoveIntent.putExtra(MoveService.EXTRA_OPPONENT, Game.getOpponent());
					getActivity().startService(MoveIntent);
				}

				//Redraw canvas
				refreshDisplay(Game);
			}

			break;

		case R.id.button_close:
			//Remove
			if (getActivity() != null && getActivity() instanceof GameActivity) ((GameActivity)getActivity()).removeOpponent();
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
				int Y = getCellRow(view);
				int X = getCellColumn(view);
				if (Game.getStatus(X, Y) == Tictactoe.EMPTY_CELL) {
					//Move
					Game.fill(X, Y);
					GameManager.instance().queueGame(Game.getOpponent());
					GameManager.instance().sendGame(Game.getOpponent());

					//If host activity exist
					if (getActivity() != null) {
						//Send move via service
						Intent MoveIntent = new Intent(getActivity() , MoveService.class);
						MoveIntent.putExtra(MoveService.EXTRA_OPPONENT, Game.getOpponent());
						getActivity().startService(MoveIntent);						
					}

					//Redraw
					refreshDisplay(Game);
				}
			}
			break;
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
	
	public void refreshDisplay(Tictactoe game) {
		//Skip if no view
		if (m_View == null) return;
		
		//Get views
		View BoardLayout   	= m_View.findViewById(R.id.layout_board);
		View ResultLayout   = m_View.findViewById(R.id.layout_result);
		View LoadingLayout  = m_View.findViewById(R.id.layout_loading);

		//if no game
		if (game == null) {
			//Show loading
			if (BoardLayout != null)    BoardLayout.setVisibility(View.GONE);
			if (ResultLayout != null)   ResultLayout.setVisibility(View.GONE);
			if (LoadingLayout != null)  LoadingLayout.setVisibility(View.VISIBLE);
		} else {
			//Process result
			int Result 			= game.getResult();
			if (BoardLayout != null)    BoardLayout.setVisibility(Result == Tictactoe.RESULT_INVALID ? View.VISIBLE : View.GONE);
			if (ResultLayout != null)   ResultLayout.setVisibility(Result == Tictactoe.RESULT_INVALID ? View.GONE : View.VISIBLE);
			if (LoadingLayout != null)  LoadingLayout.setVisibility(View.GONE);

			//If invalid
			if (Result == Tictactoe.RESULT_INVALID) {
				//Redraw board
				drawBoard(game);

				//Get turn text
				View TurnLabel = m_View.findViewById(R.id.label_turn);
				if (TurnLabel != null && TurnLabel instanceof TextView) {
					//Set text
					((TextView) TurnLabel).setText(game.isMyTurn() ? R.string.game_turn_self : R.string.game_turn_enemy);
					if (GameManager.instance().isQueueing(game.getOpponent())) ((TextView) TurnLabel).setText(R.string.game_turn_self);
				}

				//If not updating
				View StatusLabel    = m_View.findViewById(R.id.label_status);
				View Progress       = m_View.findViewById(R.id.progress_updating);
				if (!GameManager.instance().isQueueing(game.getOpponent()) && !GameManager.instance().isLoading(game.getOpponent())) {
					//Hide
					if (Progress != null)       Progress.setVisibility(View.INVISIBLE);
					if (StatusLabel != null)    StatusLabel.setVisibility(View.INVISIBLE);
				} else {
					//Show
					if (Progress != null) Progress.setVisibility(View.VISIBLE);
					if (StatusLabel != null) {
						//Set text
						StatusLabel.setVisibility(View.VISIBLE);
						if (StatusLabel instanceof  TextView) ((TextView)StatusLabel).setText(GameManager.instance().isQueueing(game.getOpponent()) ? R.string.game_sending : R.string.game_updating);
					}
				}
			} else {
				//Set text
				int TextID = -1;
				switch (Result) {
					case Tictactoe.RESULT_WIN:
						TextID = R.string.game_win;
						break;

					case Tictactoe.RESULT_LOSE:
						TextID = R.string.game_lose;
						break;

					case Tictactoe.RESULT_DRAW:
						TextID = R.string.game_draw;
						break;
				}

				//Set result
				View ResultLabel = m_View.findViewById(R.id.label_result);
				if (ResultLabel != null && ResultLabel instanceof TextView) ((TextView) ResultLabel).setText(TextID);

				//Set visibility
				View ButtonRetry    = m_View.findViewById(R.id.button_play);
				View LabelWaiting   = m_View.findViewById(R.id.label_waiting);
				if (ButtonRetry != null)    ButtonRetry.setVisibility(Result == Tictactoe.RESULT_WIN ? View.INVISIBLE : View.VISIBLE);
				if (LabelWaiting != null)   LabelWaiting.setVisibility(Result == Tictactoe.RESULT_WIN ? View.VISIBLE : View.INVISIBLE);
			}
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
				int BackgroundID = game.isMyTurn() ? R.drawable.item_background_holo_light : android.R.color.transparent;
				if (Status[x][y] == Tictactoe.SELF_CELL) 		BackgroundID = android.R.color.holo_green_light;
				else if (Status[x][y] == Tictactoe.ENEMY_CELL) 	BackgroundID = android.R.color.holo_red_light;

				//Set color
				if (m_Board[x][y] != null) m_Board[x][y].setBackgroundResource(BackgroundID);
			}
		}
	}

	//Data
	protected View		m_View;
	protected View[][]	m_Board;
}

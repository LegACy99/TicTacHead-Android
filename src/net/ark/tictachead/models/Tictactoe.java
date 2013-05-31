package net.ark.tictachead.models;

import java.util.Random;

public class Tictactoe {
	public Tictactoe(String opponent) {
		//Initialize
		m_Opponent  = opponent;
		m_Turn      = true;
		reset();
	}

	public void reset() {
		//Recreate game board
		m_Game 	= new int[][] {
			new int[] { EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
			new int[] { EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
			new int[] { EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
		};
	}

	public boolean isMyTurn() 	{ return m_Turn; 	    }
	public int[][] getStatus()	{ return m_Game;	    }
	public String getOpponent() { return m_Opponent;    }

	public int getStatus(int x, int y) {
		//Get status
		int Status = EMPTY_CELL;
		if (x >= 0 && y >= 0 && x <m_Game.length && y < m_Game[x].length) Status = m_Game[x][y];

		//Return
		return Status;
	}

	public boolean isFull() {
		//Initialize
		boolean Full = true;
		for (int i = 0; i < m_Game.length && Full; i++) {
			for (int j = 0; j < m_Game[i].length && Full; j++) {
				//If there's an empty cell, not full
				if (m_Game[i][j] == EMPTY_CELL) Full = false;
			}
		}

		//Return
		return Full;
	}

	public int getResult() {
		//Initialize
		int Result = RESULT_INVALID;

		//Check
		for (int i = 0; i < m_Game.length && Result == RESULT_INVALID; i++) {
			//Check row
			int Counter = 0;
			for (int j = 0; j < m_Game[i].length; j++) Counter += m_Game[j][i];

			//Check counter
			if (Counter >= m_Game.length)       Result = RESULT_WIN;
			else if (Counter <= -m_Game.length) Result = RESULT_LOSE;

			//If no result, check next
			if (Result == RESULT_INVALID) {
				//Check column
				Counter = 0;
				for (int j = 0; j < m_Game[i].length; j++) Counter += m_Game[i][j];

				//Check counter
				if (Counter >= m_Game.length)       Result = RESULT_WIN;
				else if (Counter <= -m_Game.length) Result = RESULT_LOSE;
			}
		}

		//If no result
		if (Result == RESULT_INVALID) {
			//Check diagonal 1
			int Counter = m_Game[0][0] + m_Game[1][1] + m_Game[2][2];
			if (Counter >= m_Game.length)       Result = RESULT_WIN;
			else if (Counter <= -m_Game.length) Result = RESULT_LOSE;

			//If no result
			if (Result == RESULT_INVALID) {
				//Check diagonal 2
				Counter = m_Game[2][0] + m_Game[1][1] + m_Game[0][2];
				if (Counter >= m_Game.length)       Result = RESULT_WIN;
				else if (Counter <= -m_Game.length) Result = RESULT_LOSE;
			}
		}

		//If full and no winner, draw
		if (Result == RESULT_INVALID && isFull()) Result = RESULT_DRAW;

		//Return
		return Result;
	}

	public int fill(int x, int y) {
		//Validate
		if (x < 0 || y < 0) 			return RESULT_INVALID;
		if (x >= m_Game.length)			return RESULT_INVALID;
		if (y >= m_Game[x].length)		return RESULT_INVALID;
		if (m_Game[x][y] != EMPTY_CELL)	return RESULT_INVALID;

		//Fill and change turn
		m_Game[x][y]    = m_Turn ? SELF_CELL : ENEMY_CELL;
		m_Turn          = !m_Turn;

		//Return
		return getResult();
	}

	public int fill() {
		//Skip if full
		if (isFull()) return getResult();

		//Initialize
		boolean Done 		= false;
		Random Generator	= new Random();
		while (!Done) {
			//Randomize
			int X = Generator.nextInt(m_Game.length);
			int Y = Generator.nextInt(m_Game[X].length);

			//Fill
			boolean Old = m_Turn;
			fill(X, Y);

			//Done if turn changed
			if (m_Turn != Old) Done = true;
		}

		//Return
		return getResult();
	}

	public void save(Tictactoe game) {
		//Validate
		if (game == null) return;

		//Copy
		m_Turn      = game.isMyTurn();
		m_Opponent  = game.getOpponent();
		for (int i = 0; i < m_Game.length; i++) {
			for (int j = 0; j < m_Game[i].length; j++) {
				m_Game[i][j] = game.getStatus()[i][j];
			}
		}
	}

	//Constant
	public static final int SELF_CELL 	    = 1;
	public static final int EMPTY_CELL 	    = 0;
	public static final int ENEMY_CELL 	    = -1;
	public static final int RESULT_INVALID 	= -1;
	public static final int RESULT_DRAW     = 0;
	public static final int RESULT_LOSE     = 2;
	public static final int RESULT_WIN 	    = 1;

	//Data
	protected int[][] 	m_Game;
	protected boolean	m_Turn;
	protected String    m_Opponent;
}

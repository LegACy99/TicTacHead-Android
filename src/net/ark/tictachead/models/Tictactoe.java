package net.ark.tictachead.models;

import java.util.Random;

public class Tictactoe {
	public Tictactoe() {
		//Initialize
		m_Turn	= true;
		m_Game 	= new int[][] {
			new int[] { EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
			new int[] { EMPTY_CELL, EMPTY_CELL, EMPTY_CELL},
			new int[] { EMPTY_CELL, EMPTY_CELL, EMPTY_CELL}
		};
	}

	public boolean isMyTurn() 	{ return m_Turn; 	}
	public int[][] getStatus()	{ return m_Game;	}

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

	public void fill(int x, int y) {
		//Validate
		if (x < 0 || y < 0) 			return;
		if (x >= m_Game.length)			return;
		if (y >= m_Game[x].length)		return;
		if (m_Game[x][y] != EMPTY_CELL)	return;

		//Fill
		m_Game[x][y] = m_Turn ? SELF_CELL : ENEMY_CELL;

		//Change turn
		m_Turn = !m_Turn;
	}

	public void fill() {
		//Skip if full
		if (isFull()) return;

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
	}

	//Constant
	public static final int SELF_CELL 	= 1;
	public static final int EMPTY_CELL 	= 0;
	public static final int ENEMY_CELL 	= -1;

	//Data
	protected int[][] 	m_Game;
	protected boolean	m_Turn;
}

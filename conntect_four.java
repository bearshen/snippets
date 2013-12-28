import java.util.ArrayList;

public class ConnectFourGameBoard {
	
    public static final int PAUSE = 0;
    public static final int READY = 1;
    public static String DATA_KEY = "status-key";
	public final static int board_width = 7;
	public final static int board_height = 6;
	public final static int board_finished_length = 4;
	public final int BOARD_STATUS_EMPTY = -1;
	public final int BOARD_COL_FULL = -1;
	public final int BOARD_COL_EMPTY = -1;
	
	boolean undoable = false;
	boolean finished = false;
	int mode = READY;
	ArrayList<Integer> piece_list = new ArrayList<Integer>();
	public int[][] board_status = new int[board_width][board_height];
	
	public Bundle saveState() {
        Bundle map = new Bundle();
        map.putIntegerArrayList(DATA_KEY, piece_list);
        return map;
    }
	
	public void start() {
		board_status = new int[board_width][board_height];
		ClearBoard();
		finished = false;
	}
	
	public void ClearBoard() {
		for (int i = 0; i < board_width; ++i) {
			for (int j = 0; j < board_height; ++j) {
				board_status[i][j] = BOARD_STATUS_EMPTY;
			}
		}
		piece_list.clear();
	}
	
	ConnectFourGameBoard() {
		start();
	}
	
	ConnectFourGameBoard(ArrayList<Integer> ai) {
		resume (ai);
	}

	public void resume (ArrayList<Integer> piece_list) {
		ArrayList<Integer> tmp = new ArrayList<Integer> (piece_list);
		ClearBoard();
		for (int i : tmp) {
			setNextMove (i);
		}
	}

	public boolean checkUndoability () {
		if (piece_list.size() > 0) {
			undoable = true;
		}
		else {
			undoable = false;
		}
		return undoable;
	}

	public boolean setNextMove(int xpos) {
		if (xpos >= board_width || xpos < 0) 
			return false;
		
		if (finished)
			return false;
		int ypos = getNextYPos (xpos);
		
		if (ypos == BOARD_COL_FULL) {
			return false;
		}
		else {
			board_status [xpos][ypos] = piece_list.size();
			piece_list.add(xpos);
			if (testSuccess()) {
				finished = true;
			}
			return true;
		}
	}
	
	public boolean getColor(int index) {
		return (index % 2 != 0 );
	}
	
	public boolean getNextColor () {
		return (piece_list.size() % 2 != 0 );
	}
	
	public boolean getPreviousColor () {
		return (piece_list.size() % 2 != 1 );
	}
	
	private int getNextYPos(int xpos) {
		for (int i = 0; i < board_height; ++i) {
			if (board_status[xpos][i] == BOARD_STATUS_EMPTY) {
				return i;
			}
		}
		return BOARD_COL_FULL;
	}
	
	private int getPreviousYPos(int xpos) {
		for (int i = board_height - 1; i >= 0; --i) {
			if (board_status[xpos][i] != BOARD_STATUS_EMPTY) {
				return i;
			}
		}
		return BOARD_COL_EMPTY;
	}

	public boolean undoPreviousMove() {
		finished = false;
		if (piece_list.size() == 0) {
			return false; // no steps to undo
		}
		int xpos = piece_list.get(piece_list.size() - 1);
		int ypos = getPreviousYPos(xpos);
		piece_list.remove(piece_list.size() - 1);
		board_status[xpos][ypos] = BOARD_STATUS_EMPTY;
		return true;
	}

	public boolean testSuccess() {
		if (piece_list.size() == 0) {
			return false; // no steps to undo
		}
		int xpos = piece_list.get(piece_list.size() - 1);
		int ypos = getPreviousYPos(xpos);
		boolean color = getPreviousColor();
		// left:
		boolean successflag = true;
		for (int i = 1; i < board_finished_length; ++i) {
			int testxpos = xpos - i;
			int testypos = ypos;
			if (testxpos < 0) {
				successflag = false;
				break;
			}
			int tmp_piece = board_status[testxpos][testypos];
			
			if (tmp_piece == BOARD_STATUS_EMPTY || getColor(tmp_piece) != color) {
				successflag = false;
				break;
			}
		}

		if (successflag) {
			return true;
		}

		// right
		successflag = true;
		for (int i = 1; i < board_finished_length; ++i) {
			// here test validity.
			int testxpos = xpos + i;
			int testypos = ypos;
			if (testxpos >= board_width) {
				successflag = false;
				break;
			}
			int tmp_piece = board_status[testxpos][testypos];
			if (tmp_piece == BOARD_STATUS_EMPTY || getColor(tmp_piece) != color) {
				successflag = false;
				break;
			}
		}
		if (successflag) {
			return true;
		}

		// up
		successflag = true;
		for (int i = 1; i < board_finished_length; ++i) {
			// here test validity.
			int testxpos = xpos;
			int testypos = ypos + i;
			if (testypos >= board_height) {
				successflag = false;
				break;
			}
			int tmp_piece = board_status[testxpos][testypos];
			if (tmp_piece == BOARD_STATUS_EMPTY || getColor(tmp_piece) != color) {
				successflag = false;
				break;
			}
		}
		if (successflag) {
			return true;
		}
		
		// down 
		successflag = true;
		for (int i = 1; i < board_finished_length; ++i) {
			// here test validity.
			int testxpos = xpos;
			int testypos = ypos - i;
			if (testypos < 0) {
				successflag = false;
				break;
			}
			int tmp_piece = board_status[testxpos][testypos];
			if (tmp_piece == BOARD_STATUS_EMPTY || getColor(tmp_piece) != color) {
				successflag = false;
				break;
			}
		}
		if (successflag) {
			return true;
		}
		
		// left-up
		successflag = true;
		for (int i = 1; i < board_finished_length; ++i) {
			// here test validity.
			int testxpos = xpos - i;
			int testypos = ypos + i;
			if (testxpos < 0 || testypos >= board_height) {
				successflag = false;
				break;
			}
			int tmp_piece = board_status[testxpos][testypos];
			if (tmp_piece == BOARD_STATUS_EMPTY || getColor(tmp_piece) != color) {
				successflag = false;
				break;
			}
		}
		if (successflag) {
			return true;
		}
		
		// left-down
		successflag = true;
		for (int i = 1; i < board_finished_length; ++i) {
			// here test validity.
			int testxpos = xpos - i;
			int testypos = ypos - i;
			if (testxpos < 0 || testypos < 0) {
				successflag = false;
				break;
			}
			int tmp_piece = board_status[testxpos][testypos];
			if (tmp_piece == BOARD_STATUS_EMPTY || getColor(tmp_piece) != color) {
				successflag = false;
				break;
			}
		}
		if (successflag) {
			return true;
		}
		
		// right-down
		successflag = true;
		for (int i = 1; i < board_finished_length; ++i) {
			// here test validity.
			int testxpos = xpos + i;
			int testypos = ypos - i;
			if (testxpos >= board_width || testypos < 0) {
				successflag = false;
				break;
			}
			int tmp_piece = board_status[testxpos][testypos];
			if (tmp_piece == BOARD_STATUS_EMPTY || getColor(tmp_piece) != color) {
				successflag = false;
				break;
			}
		}
		if (successflag) {
			return true;
		}
		
		// right-up
		successflag = true;
		for (int i = 1; i < board_finished_length; ++i) {
			// here test validity.
			int testxpos = xpos + i;
			int testypos = ypos + i;
			if (testxpos >= board_width || testypos >= board_height) {
				successflag = false;
				break;
			}
			int tmp_piece = board_status[testxpos][testypos];
			if (tmp_piece == BOARD_STATUS_EMPTY || getColor(tmp_piece) != color) {
				successflag = false;
				break;
			}
		}
		if (successflag) {
			return true;
		}
		
		return false;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public boolean isUndoable () {
		return undoable;
	}
}
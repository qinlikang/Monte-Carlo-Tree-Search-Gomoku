package gomoku;

import java.util.ArrayList;
import java.util.Random;

public class GomokuState {
	public static int boardSize = 8;
	
	public int board[][];
	private boolean startTurn = TURN_WHITE;
	
	public static final boolean TURN_WHITE = true;
	public static final boolean TURN_BLACK = false;
	
	public static final int TOKEN_WALL = 2;
	public static final int TOKEN_WHITE = 1;
	public static final int TOKEN_BLACK = -1;
	public static final int TOKEN_NONE = 0;
	
	public static final int WON_WHITE = 0;
	public static final int WON_BLACK = 1;
	public static final int WON_DRAW = 2;
	public static final int WON_NONE = 3;
	
	private static final int inRow = 4;
	
	public Position lastMove = null; //new Position(-1,-1);
	
	Random rnd = new Random();
	
	public class Position {
		public int x, y;
		public int diff2RowO = 0, diff2_RowC = 0, diff_2_RowC = 0, diff3RowC = 0, diff_1_RowO = 0;
		public double diffLen = 0;
		public Position(int x, int y) {
			this.x = x; this.y = y;
		}
		public String toString() {
			return "(" + x + "," + y + ")";
		}
		public boolean equals(Position p) {
			return (x == p.x && y == p.y);
		}
		public Position duplicate() {
			return new Position(x, y);
		}
	}
	
	class Token {
		public int x, y, colour;
		public Token(int x, int y, int colour) {
			this.x = x; this.y = y; this.colour = colour;
		}
		
		public boolean equals(Token t) {
			return t.x == x && t.y == y && t.colour == colour;
		}
	}
	
	public GomokuState() {
		this.board =  new int[boardSize][boardSize];
	}
	
	public GomokuState(String s) {
		s = s.substring(1);
		int a1 = s.indexOf("],");
		String s1 = s.substring(0,a1);
		
		int a2 = s1.indexOf(",");
		String board = s1.substring(0, a2);
		String s12 = s1.substring(a2+1);
		s12 = s12.substring(1, s12.length()-1);
		
		int a3 = s12.indexOf(",");
		int x = Integer.parseInt(s12.substring(0, a3));
		int y = Integer.parseInt(s12.substring(a3+1));
		
		this.board =  (new GomokuState(board, x, y)).board;
		this.lastMove = new Position(x, y);
	}
	
	public GomokuState(String s, int pos_x, int pos_y) {
		this.board =  new int[boardSize][boardSize];
		lastMove  = new Position(pos_x, pos_y);
		
		int y = 0;
		int x = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == 'W') board[y][x] = TOKEN_WHITE;
			else if (c == 'B') board[y][x] = TOKEN_BLACK;
			else if (c == ' ')	board[y][x] = TOKEN_NONE;
		
			if (c == '|') {
				x = 0;
				y++;
			}
			else {
				x ++;
			}
		}
	}
	
	
	public static double parseClass(String s) {
		int a1 = s.indexOf("],");
		String s2 = s.substring(a1+2);
		
		double r = Double.parseDouble(s2);
		return r;
	}
	
	public int getPieces() {
		int c = 0;
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] != TOKEN_NONE) c++;
			}
		}
		return c;
	}
	
	public boolean equals3(GomokuState state) {
		
		//TODO
		boolean b1 = state.lastMove.equals(this.lastMove);
		boolean b2 = equals(state);
		assert (b1 == b2) : "Invalid: " + b1 + " " + b2;
		
		if (state.lastMove.equals(this.lastMove)) return true;
		return false;
	}
	
	public boolean equals(GomokuState state) {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (state.board[i][j] != board[i][j]) return false;
			}
		}
		return true;
	}
	
//	public boolean sameMove(GomokuState state) {
//		return lastMove.equals(state.lastMove);
//	}
	
	
	public GomokuState duplicate() {
		GomokuState result = new GomokuState();
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				result.board[i][j] = board[i][j];
			}
		}
		if (lastMove != null) result.lastMove = lastMove.duplicate();

		return result;
	}
	
	public ArrayList<Position> choices() {
		ArrayList<Position> result = new ArrayList<Position>();
		
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == TOKEN_NONE) result.add(new Position(i, j));
			}
		}
		return result;
	}
	
	public Position randomChoice() throws Exception {
		ArrayList<Position> lst = choices();
		if (lst.size() == 0) throw new Exception("No empty fields!");
		return lst.get(rnd.nextInt(lst.size()));
	}
	
	public GomokuState makeMove(Position p) throws Exception {
		if (board[p.x][p.y] != TOKEN_NONE) throw new Exception("Already token at (" + p.x + "," + p.y + ")");
		GomokuState result = duplicate();
		result.board[p.x][p.y] = tokenColour(getTurn());
		result.lastMove = p;
		
		return result;
	}
	
	public GomokuState undoMove(Position p) {
		GomokuState result = duplicate();
		result.board[p.x][p.y] = TOKEN_NONE;
		return result;
	}
	
	public GomokuState makeRandomMove() throws Exception  {
		Position p = randomChoice();
		return makeMove(p);
	}
	
	private int tokenColour(boolean turn) {
		if (turn == TURN_WHITE) return TOKEN_WHITE;
		return TOKEN_BLACK;
	}
	
	public int monteCarlo() {
		GomokuState tmp = this;
		while (tmp.hasWon() == WON_NONE) {
			try {tmp = tmp.makeRandomMove();} catch (Exception e) {return WON_DRAW;}
		}
		
		return tmp.hasWon();
	}
	
	private int returnWinTurn() {
		if (lastMoved() == TURN_BLACK) return WON_BLACK;
		return WON_WHITE;
	}
	

    private ArrayList<Position> winPos;

    public int hasWon() {
    	
    	if (lastMove == null) return WON_NONE;
    	
    	Position pos = lastMove;
        winPos = new ArrayList<Position>();

        int count = leftRight(pos, -1, -1) + leftRight(pos, 1, boardSize) - 1;
        if(count >= inRow) {
            winPos.remove(pos);
            return returnWinTurn();
        }

        winPos = new ArrayList<Position>();
        count = upDown(pos, -1, -1) + upDown(pos, 1, boardSize) - 1;
        if(count >= inRow) {
            winPos.remove(pos);
            return returnWinTurn();
        }

        winPos = new ArrayList<Position>();
        count = diag(pos, -1, -1, -1, -1) + diag(pos, 1, 1, boardSize, boardSize) - 1;
        if(count >= inRow) {
            winPos.remove(pos);
            return returnWinTurn();
        }

        winPos = new ArrayList<Position>();
        count = diag(pos, 1, -1, boardSize, -1) + diag(pos, -1, 1, -1, boardSize) - 1;
        if(count >= inRow) {
            winPos.remove(pos);
            return returnWinTurn();
        }

        if (getPieces() == boardSize*boardSize) return WON_DRAW;
        return WON_NONE;
    }

    public int leftRight(Position pos, int offsetX, int maxX) {
        if(pos.x == maxX || board[pos.x][pos.y] != tokenColour(lastMoved())) return 0;
        else {
            winPos.add(pos);
            return 1 + leftRight(new Position(pos.x + offsetX, pos.y), offsetX, maxX);
        }
    }

    public int upDown(Position pos, int offsetY, int maxY) {
        if(pos.y == maxY || board[pos.x][pos.y] != tokenColour(lastMoved())) return 0;
        else {
            winPos.add(pos);
            return 1 + upDown(new Position(pos.x, pos.y + offsetY), offsetY, maxY);
        }
    }

    public int diag(Position pos, int offsetX, int offsetY, int maxX, int maxY) {
        if(pos.x == maxX || pos.y == maxY || board[pos.x][pos.y] != tokenColour(lastMoved())) return 0;
        else {
            winPos.add(pos);
            return 1 + diag(new Position(pos.x + offsetX, pos.y + offsetY), offsetX, offsetY, maxX, maxY);
        }
    }
    
    public boolean getTurn() {
    	//return turn;
    	if (lastMove == null) return startTurn;
    	return !lastMoved();
    }
    
    public void naiveChangeTurn() {
		f: for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == tokenColour(getTurn()) && board[i][j] != TOKEN_NONE) {
					lastMove = new Position(i, j);
					break f;
				}
			}
		}
    }
    
    public boolean lastMoved() {
    	int x = lastMove.x;
    	int y = lastMove.y;
//    	assert (board[x][y] != TOKEN_NONE);
    	if (board[x][y] == TOKEN_BLACK) return TURN_BLACK;
    	return TURN_WHITE;
    }
    
    public String getTurnS() {
    	if (getTurn() == TURN_BLACK) return "black";
    	return "white";
    }
    
    public static float getScore(int won, boolean turn_) {
    	if (won == WON_WHITE && turn_ == TURN_WHITE) return 1;
    	else if (won == WON_WHITE && turn_ == TURN_BLACK) return -1;
    	
    	else if (won == WON_BLACK && turn_ == TURN_WHITE) return -1;
    	else if (won == WON_BLACK && turn_ == TURN_BLACK) return 1;
    	
    	else if (won == WON_DRAW) return 0;
    	
    	return 0;
    }
    
    public void fill(int colour) {
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				board[i][j] = colour;
			}
		}
		//pieces = boardSize*boardSize;
    }
    
    public GomokuState randomMoves(int n) throws Exception {
    	f: while (true) {
    		GomokuState res = this;
	    	for (int i = 0; i < n-1; i++) {
	    		res = res.makeRandomMove();
	    		if (res.hasWon() != WON_NONE) continue f;
	    	}
	    	res = res.makeRandomMove();
	    	if (res.hasWon() != WON_NONE && Main.afterMove == false) continue f;
	    	return res;
    	}
    }
    
    public GomokuState randomMovesW() throws Exception {
    	f: while (true) {
    		GomokuState res = this;
	    	s: while (true) {
	    		res = res.makeRandomMove();
	    		if (res.hasWon() == WON_BLACK || res.hasWon() == WON_WHITE) break;
	    		if (res.hasWon() == WON_DRAW) continue f;
	    	}
	    	return res;
    	}
    }
    
    public void print() {
    	System.out.print("  ");
    	for (int i = 0; i < boardSize; i++) {
    		System.out.print(i + " ");
    	}
    	System.out.println();
		for (int i = 0; i < boardSize; i++) {
			System.out.print(i + " ");
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == TOKEN_WHITE) System.out.print("W ");
				else if (board[i][j] == TOKEN_BLACK) System.out.print("B ");
				else  System.out.print("  ");
			}
			System.out.println();
		}
    }
    
    public String toString() {
    	String s = "";
		for (int i = 0; i < boardSize; i++) {
			for (int j = 0; j < boardSize; j++) {
				if (board[i][j] == TOKEN_WHITE) s += "W";
				else if (board[i][j] == TOKEN_BLACK) s += "B";
				else  s += " ";
			}
			if (i != boardSize - 1) s += "|";
		}
		return "[" + s + "," + lastMove + "]";
    }
    
	
    /* pattern:
     * 	N - not empty
     * 	_ - empty
     * 	B - black
     * 	W - white
     * 	R - doesn't matter
     * 
     * 	C - token of current player
     * 	O - token of opposite player
     * 	L - wall token
     */
	public int countPattern(String p) {
		int counter = 0;
		
		/* horizontal */
		for (int y = 0; y < boardSize; y++) {
			for (int x = 0-1; x < boardSize - p.length() + 1+1; x++) {
				boolean matching = true;
				for (int i = 0; i < p.length(); i++) {
					char c = p.charAt(i);

					int t = TOKEN_WALL;
					try {
						t = board[y][x+i];
					}
					catch (Exception e) {}
					
					if (!(countPatternHelper(t,c))) matching = false;
				}
				if (matching) {
					counter ++;
//					System.out.println("(" + y + "," + x + " - " + (x+p.length()-1) + ")");
				}
			}
		}
		
		/* vertical */
		for (int x = 0; x < boardSize; x++) {
			for (int y = 0-1; y < boardSize - p.length() + 1+1; y++) {
				boolean matching = true;
				for (int i = 0; i < p.length(); i++) {
					char c = p.charAt(i);

					int t = TOKEN_WALL;
					try {
						t = board[y+i][x];
					}
					catch (Exception e) {}
					
					if (!(countPatternHelper(t,c))) matching = false;
				}
				if (matching) {
					counter ++;
//					System.out.println("(" + y + " - " + (y+p.length()-1) + "," + x + ")");
				}
			}
		}
		
		/* right diagonal */
		for (int x = 0-1; x < boardSize - p.length() + 1+1; x++) {
			for (int y = 0-1; y < boardSize - p.length() + 1+1; y++) {
				boolean matching = true;
				for (int i = 0; i < p.length(); i++) {
					char c = p.charAt(i);
					
					int t = TOKEN_WALL;
					try {
						t =  board[y+i][x+i];
					}
					catch (Exception e) {}
					
					if (!(countPatternHelper(t,c))) matching = false;
				}
				if (matching) {
					counter ++;
				}
			}
		}
		
		/* left diagonal */
		for (int x = 0-1; x < boardSize - p.length() + 1+1; x++) {
			for (int y = 0-1+ p.length(); y < boardSize +1; y++) {
				boolean matching = true;
				for (int i = 0; i < p.length(); i++) {
					char c = p.charAt(i);
					
					int t = TOKEN_WALL;
					try {
						t =  board[y-i][x+i];
					}
					catch (Exception e) {}
					
					if (!(countPatternHelper(t,c))) matching = false;
				}
				if (matching) {
					counter ++;
				}
			}
		}
		
		return counter;
	}
	
	private boolean countPatternHelper(int t, char c) {
		if (c == 'W') {
			if (t != TOKEN_WHITE) return false;
		}
		else if (c == 'B') {
			if (t != TOKEN_BLACK) return false;
		}
		else if (c == ' ') {
			if (t != TOKEN_NONE) return false;
		}
		else if (c == 'N') {
			if (t == TOKEN_NONE) return false;
		}
		else if (c == 'C') {
			if (t != tokenColour(getTurn())) return false;
		}
		else if (c == 'O') {
			if (t != tokenColour(!getTurn())) return false;
		}
		else if (c == 'L') {
			if (t != TOKEN_WALL) return false;
		}
		else if (c == 'Y') {
			if (t != TOKEN_WALL && t != tokenColour(!getTurn())) return false;
		}
		else if (c == 'X') {
			if (t != TOKEN_WALL && t != tokenColour(getTurn())) return false;
		}
		
		return true;
	}
	
	public int numberOf_X_(int size, char c) {
		String s = "";
		for (int i = 0; i < size; i++)
			s += c;
		s = " " + s + " ";
		return countPattern(s);
	}
	
	public int numberOfX_(int size, char c) {
		String s = "";
		for (int i = 0; i < size; i++)
			s += c;
		
		char c2 = 'X';
		if (c == 'C') c2 = 'Y';
		else if (c == 'O') c2 = 'X';
		
		String s1 = c2 + s + " ";
		String s2 = " " + s + c2;
//		System.out.println(s1);
		return countPattern(s1) + countPattern(s2);
	}
	
	public int numberOf_X_X_(char c) {
		String s = " " + c + " " + c + " ";
		return countPattern(s);
	}
	
	public int numberOfXX_X(char c) {
		String s1 = c + "" + c + " " + c;
		String s2 = c + " " + c + "" + c;
//		System.out.println(s1);
//		System.out.println(s2);
		return countPattern(s1) + countPattern(s2);
	}
	
	public double avgDistance() {
		double rez = 0;
		int nbr = 0;
		
		for(int i = 0; i < board.length; i++) {
	    f: for(int j = 0; j < board[i].length; j++) {
			double min_rez = 10e6;
			
			if (board[i][j] == TOKEN_NONE) continue f;
			
	    	for(int k = 0; k < board.length; k++) {
			for(int l = 0; l < board[k].length; l++) {
				
				if(board[k][l] != TOKEN_NONE && board[i][j] != board[k][l]) {
						double tmp_rez = Math.sqrt(Math.pow(i-k, 2) + Math.pow(j-l, 2)); 
						if(tmp_rez < min_rez) { min_rez = tmp_rez;}
						}
			}
			}
	    	
	    	if (min_rez == 10e6) min_rez = 0;
	    	//if(min_rez != 0) {
	    		nbr++;
	    		rez += min_rez;
	    	//}
		}
		}
//		System.out.println("rez: "+rez+" nbr: "+nbr);
		return rez/(nbr);
	}
	
	public int longestChain(char c) {
		for (int i = 4; i > 0; i--) {
			String s = "";
			for (int j = 0; j < i; j++) s += "" + c;
			if (countPattern(s)>0) return i;
		}
		return 0;
	}
	
	public static double regressionClassification(double[] attr, double[] coef) {
		double r = 0;
		for (int i = 0; i < attr.length; i++)
			r += attr[i]*coef[i];
		return r;
	}

}

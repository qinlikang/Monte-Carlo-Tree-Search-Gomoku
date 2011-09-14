package gomoku;
import gomoku.GomokuState.Position;
import java.util.ArrayList;
import java.util.Random;

public class Node {

	private int ID = new Random().nextInt(100);
	public Node parent;
	//public int value = 0;
	public int value = 0;
	public int visitCount = 1;
	public ArrayList<Node> children = new ArrayList<Node>();
	public GomokuState state;
	
	private final int goban = 40;
	//private final int goban = 20;
	private int maxDepth = 30;
	
	private double C = 0.7d;
	private Random rand = new Random();

	public Node() {
		this.parent = null;
		this.state = new GomokuState();
	}
	
	public Node(GomokuState state) {
		this.parent = null;
		this.state = state;
	}

	public Node(Node parent, Position pos) {
		this.parent = parent;
	}
	
	public Node(Node parent, GomokuState state) {
		this.parent = parent;
		this.state = state;
	}
	
	public Node duplicateWithoutChildren() {
		Node n = new Node();
		n.state = state.duplicate();
		n.parent = parent;
		return n;
	}
	
	private int depth() {
		if (parent == null) return 0;
		return parent.depth() + 1;
	}
	
	private Node selection()
	{
		if (state.hasWon() != GomokuState.WON_NONE) return this;
		
		if(depth() > maxDepth) return this;
		else if(children.size() == 0) return expansion();
		
		ArrayList<Integer> rez = new ArrayList<Integer>();
		double maxRating = Integer.MIN_VALUE;
		
		for(int x = 0; x < children.size(); x++)
		{
			double currRating = computeNodeRating(children.get(x));
			
			if(currRating > maxRating)
			{
				rez = new ArrayList<Integer>();
				maxRating = currRating;
			}
			
			if(currRating == maxRating)
			{
				rez.add(x);
			}
		}
		
		int indexOfSelectedNode = rand.nextInt(rez.size());
		return (children.get(rez.get(indexOfSelectedNode))).selectionExpansion();
	}
	
	public Node selectionExpansion() {
		visitCount++;
		
		if (visitCount > goban)	return selection();
		
		if (state.hasWon() != GomokuState.WON_NONE) return this;
		
		GomokuState newState;
		
		try {
			newState = state.makeRandomMove();
		} catch (Exception e) {
			return this;
		}
		
		Node tmp;
		if ((tmp = containsChildState(newState)) != null) return tmp.selectionExpansion();
		
		Node newNode = new Node(this, newState);
		children.add(newNode);
		return newNode;
	}

	public double computeNodeRating(Node node)
	{
		double temp = Math.log((double) node.parent.visitCount) / (double) node.visitCount;
	    return (double) node.value + C * Math.sqrt(temp);
	}

	
	public Node expansion() // throws Exception
	{
		
		if(depth() > maxDepth)
		{
			return this;
		}

		if(children.size() == 0)
		{
			try {
				GomokuState newState = state.makeRandomMove();
				Node newNode = new Node(this, newState);
				children.add(newNode);
				return newNode;
			}
			catch (Exception e) {
				// no more empty fields
				return this;
			}
		}
		
		if(visitCount < goban)
		{
			return this;
		}
		
		return null;
	}
	
	public void backPropagation2(int won)
	{
		value += GomokuState.getScore(won, state.lastMoved());
		if(parent != null) parent.backPropagation2(won);
	}
	
	public int simulation() 
	{
		return state.monteCarlo();
	}
	
	private Node containsChildState(GomokuState st) {
		for (Node n : children) {
			if (n.state.equals3(st)) return n;
		}
		return null;
	}
	
	private String padWC(String str, String pad) {
		return pad + str;
	}

	private ArrayList<String> padFAllC(ArrayList<String> lst, String pad) {
		ArrayList<String> ret = new ArrayList<String>();
		for (String s : lst) ret.add(padWC(s, pad));
		return ret;
	}
	
	private String flatten(ArrayList<String> lst) {
		String ret = lst.get(0);
		for (int i = 1; i < lst.size(); i++) ret+="\n" + lst.get(i);
		return ret;
	}
	
	public String toString() {
		if (state.lastMove == null) return "T: " + state.getTurnS() + " ID: " + ID + " v: " + value + ", vc: " + visitCount + ", pcs: " + state.getPieces();
		return "T: " + state.getTurnS() + " ID: " + ID + " v: " + value + ", vc: " + visitCount + ", pos: " + state.lastMove + ", pcs: " + state.getPieces();
	}
	
	public ArrayList<String> getTree() {
		ArrayList<String> ret = new ArrayList<String>();
		String root = this.toString();
		ret.add(root);
		for (Node n : children) {
			ArrayList<String> nlst = padFAllC(n.getTree(), "-> ");
			ret.addAll(nlst);
		}
		return ret;
	}
	
	public String printTreeL() {
		return flatten(getTree());
	}
	
	public String printTree() {String rez="";
		rez+=this+"\n";
		for (Node n : children) rez+=n.printTree() + " ";
		rez+="\n";
		return rez;
	}

	double INFINITY = Integer.MAX_VALUE;
	
	public Position getMove(GomokuState _state, int _depth) {
		double best = -INFINITY;
		Position bestmove = null;
		double eval;
		for(Position move : state.choices()) {
			try {
				_state = _state.makeMove(move);
				eval = -NegaMax(_state, _depth);
				_state = _state.undoMove(move);
				if(eval > best) {
					best = eval;
					bestmove = move;
				}
			}
			
			catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		System.out.println("Best move: "+bestmove.x+", "+bestmove.y+" : "+best);
		return bestmove;
	}
	
	public double NegaMax(GomokuState _state, int _depth) {
		if(_depth == 0) return 1;//_state.eval();
		double best = -INFINITY;
		double eval;
		
		for(Position move : _state.choices()) {
			try {
				_state = _state.makeMove(move);
				eval = -NegaMax(_state, _depth - 1);
				_state = _state.undoMove(move);
				if(eval > best) best = eval;
			}
			
			catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return best;
	}
	
	public Position getMoveAB(GomokuState _state, int _depth) {
		double best = -INFINITY;
		Position bestmove = null;
		double eval;
		
		for(Position move : _state.choices()) {
			try {
				_state = _state.makeMove(move);
				eval = -AlphaBeta(_state, _depth, -INFINITY, INFINITY);
				_state = _state.undoMove(move);
				if(eval > best) {
					best = eval;
					bestmove = move;
				}
			}
			catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		System.out.println("Best move: "+bestmove.x+", "+bestmove.y+" : "+best);
		return bestmove;
	}
	
	public Position getMoveAB_MC(GomokuState _state, int _depth, int _EvalFunc) {
		double best = -INFINITY;
		Position bestmove = null;
		double eval;
		
		for(Position move : _state.choices()) {
			try {
				_state = _state.makeMove(move);
				eval = -AlphaBeta_MC(_state, _depth, _EvalFunc, -INFINITY, INFINITY);
				_state = _state.undoMove(move);
				if(eval > best) {
					best = eval;
					bestmove = move;
				}
			}
			
			catch(Exception e) {
				System.out.print(e.getMessage());
			}
		}
		System.out.println("Best move: "+bestmove.x+", "+bestmove.y+" : "+best);
		return bestmove;
	}
	
	public double AlphaBeta(GomokuState _state, int _depth, double _alpha, double _beta) {
		if(_depth == 0) return 1;//_state.eval()
		double best = -INFINITY;
		ArrayList<Position> moves = _state.choices();
		Position move = null;
		double eval;
		
		while(moves != null && best < _beta) {
			try {
				move = moves.get(0);
				moves.remove(0);
				if(best > _alpha) _alpha = best;
		   		_state = _state.makeMove(move);
		   		eval = -AlphaBeta(_state, _depth - 1, -_beta, -_alpha);
		   		_state = _state.undoMove(move);
		   		if(eval > best) best = eval;
			}
			
			catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
		return best;
	}
	
	public double AlphaBeta_MC(GomokuState _state, int _depth, int _EV, double _alpha, double _beta) {
		if(_depth == 0) return _EV;
		double best = -INFINITY;
		ArrayList<Position> moves = _state.choices();
		double eval;
		Position move;
		
		while(moves != null && best < _beta) {
			try {
				move = moves.get(0);
				moves.remove(0);
				
				if(best > _alpha) _alpha = best;
				_state = _state.makeMove(move);
				eval = -AlphaBeta_MC(_state, -_depth - 1, _EV, -_beta, -_alpha);
				_state = _state.undoMove(move);
				if(eval > best) best = eval;
			}
			
			catch(Exception e) {
				System.out.print(e.getMessage());
			}
		}
		return best;
	}
}

package gomoku;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class Output {
	public static void write(PrintWriter writer, String str) {
		try {
			writer.println(str);
			writer.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<GomokuResult> read(String file) {
		try {
			ArrayList<GomokuResult> lst = new ArrayList<GomokuResult>();
			
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				lst.add(new GomokuResult(
							new GomokuState(strLine), 
							GomokuState.parseClass(strLine)));
			}
			
			return lst;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public static void writeAttributes(String file) throws Exception {
		ArrayList<GomokuResult> lst = read(file);

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file + "_classified_" + Classification.discretize,false)));
		
		for (GomokuResult r : lst) {
			GomokuState s = r.state;
			GomokuState ps = s.undoMove(s.lastMove);
			
			int attr1 = s.numberOf_X_(1, 'C');
			int attr2 = s.numberOf_X_(2, 'C');
			int attr3 = s.numberOfX_(1, 'C');
			int attr4 = s.numberOfX_(2, 'C');
			int attr5 = s.numberOfX_(3, 'C');
			int attr6 = s.numberOf_X_X_('C');
			int attr7 = s.numberOfXX_X('C');
			
			int attr8 = s.numberOf_X_(1, 'O');
			int attr9 = s.numberOf_X_(2, 'O');
			int attr10 = s.numberOfX_(1, 'O');
			int attr11 = s.numberOfX_(2, 'O');
			int attr12 = s.numberOfX_(3, 'O');
			int attr13 = s.numberOf_X_X_('O');
			int attr14 = s.numberOfXX_X('O');
			
			double avgDist = s.avgDistance();
			
			out.print(s.getPieces() + "|");
			
			out.print(avgDist + "|");
			out.print(avgDist - ps.avgDistance() + "|");
			
			out.print(s.numberOf_X_(1, 'C') + "|");
			out.print(s.numberOf_X_(2, 'C') + "|");
			out.print(s.numberOfX_(1, 'C') + "|");
			out.print(s.numberOfX_(2, 'C') + "|");
			out.print(s.numberOfX_(3, 'C') + "|");
			out.print(s.numberOf_X_X_('C') + "|");
			out.print(s.numberOfXX_X('C') + "|");
			
			out.print(s.numberOf_X_(1, 'O') + "|");
			out.print(s.numberOf_X_(2, 'O') + "|");
			out.print(s.numberOfX_(1, 'O') + "|");
			out.print(s.numberOfX_(2, 'O') + "|");
			out.print(s.numberOfX_(3, 'O') + "|");
			out.print(s.numberOf_X_X_('O') + "|");
			out.print(s.numberOfXX_X('O') + "|");
			
			out.print((attr1 - ps.numberOf_X_(1, 'C')) + "|");
			out.print((attr2 - ps.numberOf_X_(2, 'C')) + "|");
			out.print((attr3 - ps.numberOfX_(1, 'C')) + "|");
			out.print((attr4 - ps.numberOfX_(2, 'C')) + "|");
			out.print((attr5 - ps.numberOfX_(3, 'C')) + "|");
			out.print((attr6 - ps.numberOf_X_X_('C')) + "|");
			out.print((attr7 - ps.numberOfXX_X('C')) + "|");
			
			out.print((attr8 - ps.numberOf_X_(1, 'O')) + "|");
			out.print((attr9 - ps.numberOf_X_(2, 'O')) + "|");
			out.print((attr10 - ps.numberOfX_(1, 'O')) + "|");
			out.print((attr11 - ps.numberOfX_(2, 'O')) + "|");
			out.print((attr12 - ps.numberOfX_(3, 'O')) + "|");
			out.print((attr13 - ps.numberOf_X_X_('O')) + "|");
			out.print((attr14 - ps.numberOfXX_X('O')) + "|");
			
			out.print(s.longestChain('C') + "|");
			out.print(s.longestChain('O') + "|");
			
			if (Classification.discretize == 0)
				out.print(r.value);
			else
				out.print((float)((int)(r.value*Classification.discretize))/Classification.discretize);
			out.println();
		}
		out.close();
	}

	public static void createBoards(int n, PrintWriter p, boolean after_) {
		try {

		Random r = new Random();
		float after = Integer.MIN_VALUE;
		for (int i = 0; i < n; i++) {
			int m = r.nextInt((GomokuState.boardSize*GomokuState.boardSize - 1) + 1);
			GomokuState rndState = new GomokuState().randomMoves(m);
			rndState.print();
			System.out.println(rndState.getTurnS() + " " + rndState.lastMove);
			
			Node root = new Node(rndState);
			System.out.println(root.printTreeL()); System.out.println();
			
			MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
			
			try {
				after = mcts.getScoreMultipleErr(root, 10000, 0.05f, 2, 10, after_);
			}
			catch(Exception e) {
				System.out.println(e.getMessage());
				continue;
			}
			write(p, rndState + "," + after);
			System.gc();
		}
		
		p.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

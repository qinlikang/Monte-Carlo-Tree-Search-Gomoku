package gomoku;

import gomoku.GomokuState.Position;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import weka.classifiers.Classifier;

public class Main implements Runnable  {
	
	private static final int threads = 3;
	private static final int max_boards = 3000;
	
	public static final boolean afterMove = true;
	
	private PrintWriter p;

	public Main(String file) throws Exception {
		p = new PrintWriter(new BufferedWriter(new FileWriter(file,true)));
	}
	
	@Override
	public void run() {
		Output.createBoards(max_boards, p, afterMove);
	}
	
	public static void main(String[] args)  throws Exception {
		
		
		String[] testingSet = {
				"sets/learning/01_after", 
				"sets/learning/02_after", 
//				"sets/learning/03_after", 
//				"sets/learning/04_after", 
				"sets/learning/05_after"};
		String[] learningSet = {
//				"sets/learning/01_after", 
//				"sets/learning/02_after", 
				"sets/learning/03_after", 
				"sets/learning/04_after", 
//				"sets/learning/05_after", 
				"sets/learning/09_after", 
				"sets/learning/06_after", 
				"sets/learning/07_after", 
				"sets/learning/08_after"};
		
		for (int i = 0; i < learningSet.length; i++) {
			Output.writeAttributes(learningSet[i]);
		}
		
		for (int i = 0; i < testingSet.length; i++) {
			Output.writeAttributes(testingSet[i]);
		}
		
		Classifier model = Classification.createModel(learningSet,true,true);
		Classification.test(testingSet,model);

		System.out.println();
		System.out.println();
		
		int MCwins = 0,ABwins = 0;
		int[][] wins = new int[300][2];
		
		GomokuState state;// = new GomokuState("[    B BW|W   B   |       W|W B     | B  BW  |      W |  W  B  |   B  W ,(3,2)],-0.007883749");
		Position pos = null;
		int val;
		Node MC = new Node();
		//state.print();
		System.out.println("*7+32");
		for(int i = 0; i<100; i++) {
			//generate random board on which there is no win
			do {
//				state = new GomokuState().randomMoves((int)new Random().nextDouble()*13); //0-13, 14-27, 28-41, 42-55
//				state = new GomokuState().randomMoves((int)new Random().nextDouble()*13+14);
//				state = new GomokuState().randomMoves((int)new Random().nextDouble()*13+28);
				state = new GomokuState().randomMoves((int)new Random().nextDouble()*7+32);
				if(state.hasWon() == state.WON_NONE && Classification.getClassValue(state, model) > -0.1 && Classification.getClassValue(state, model) < 0.1) break;
			} while(true);
				//state.print();
				wins[i][1] = state.getPieces();
				System.out.println("Found board "+i+" with "+state.getPieces()+" tokens and classValue: "+Classification.getClassValue(state, model));
				while(true) {
					//MCST
					val = Integer.MIN_VALUE;
					//System.out.println("MC");
					for(Position p : state.choices()) {
						MC = MonteCarloTreeSearch.run(new Node(state), 3000);
						if(val < MC.value) {
							val = MC.value;
							pos = p;
						}
					}
					
					state = state.makeMove(pos);
					//state.print();
					//AB
					
					if(state.hasWon() != state.WON_NONE) {
						System.out.println(state.getTurnS() + " lost!");
						wins[i][0] = 0;
						MCwins++;
						break;
					}
					
					//System.out.println("AB");
					pos = AlphaBeta.negaMax(state, 2, model);
					//System.out.println("Best move: " + pos);
					state = state.makeMove(pos);
					//state.print();
					
					if (state.hasWon() != state.WON_NONE) {
						System.out.println(state.getTurnS() + " lost!");
						wins[i][0] = 1;
						ABwins++;
						break;
					}
				}
				System.out.println("MCwins: "+MCwins+" ABwins: "+ABwins);
		}

		System.out.println("END! ");
		System.out.println("MCwins: "+MCwins+" ABwins: "+ABwins);
		/*int avg=0,nr=0;
		for(int i = 0; i<wins.length; i++) {
			if(wins[i][0] == 0) {
				avg = wins[i][1];
				nr++;
			}
		}
		System.out.println("Average nr. pieces when MCwins: "+(avg/nr));*/
	}
}

class GomokuResult {
	public GomokuState state;
	public double value;
	
	public GomokuResult(GomokuState state, double value) {
		this.state = state;
		this.value = value;
	}
	
	public String toString() {
		return state.toString() + ", " + value; 
	}
}

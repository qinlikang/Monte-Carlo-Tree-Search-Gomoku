package gomoku;

import java.util.ArrayList;
import java.util.Random;

import gomoku.GomokuState.Position;

import weka.classifiers.Classifier;

public class AlphaBeta2 {

//	public static Position alphaBeta(GomokuState node, int depth, double alpha, double beta, boolean maxPlayer, Classifier model) throws Exception {
//		ArrayList<Position> res = new ArrayList<Position>();
//		
//		if (depth == 0 || node.hasWon() != GomokuState.WON_NONE) {
//			return null;
//		}
//		
//		if (maxPlayer) {
//			for (Position p : node.choices()) {
//				GomokuState newNode = node.makeMove(p);
//				
//				double alpha_ = alpha;
//				alpha = Math.max(alpha, alphaBeta_(newNode, depth-1, alpha, beta, !maxPlayer, model));
//				
//				if (alpha > alpha_)	{
//					res = new ArrayList<Position>();
//					res.add(p);
//				}
//				else if (alpha == alpha_) {
//					res.add(p);
//				}
//				
//				if (beta <= alpha) {
//					break;
//				}
//			}
//		}
//		else {
//			for (Position p : node.choices()) {
//				GomokuState newNode = node.makeMove(p);
//				
//				double beta_ = beta;
//				beta = Math.min(beta, -alphaBeta_(newNode, depth-1, alpha, beta, !maxPlayer, model));
//				
//				if (beta < beta_)	{
//					res = new ArrayList<Position>();
//					res.add(p);
//				}
//				else if (beta == beta_) {
//					res.add(p);
//				}
//				
//				if (beta <= alpha) {
//					break;
//				}
//			}
//		}
//		
//		for (Position k : res) System.out.println(k + " " + alpha);
//		System.out.println();
//		
//		return res.get(new Random().nextInt(res.size()));
//	}
	
	public static Position alphaBeta(GomokuState node, int depth, double alpha, double beta, boolean maxPlayer, Classifier model) throws Exception {
		ArrayList<Position> res = new ArrayList<Position>();
		
		if (depth == 0 || node.hasWon() != GomokuState.WON_NONE) {
			return null;
		}
		
		for (Position p : node.choices()) {
			GomokuState newNode = node.makeMove(p);
			
			if (maxPlayer) {
				double rec = alphaBeta_(newNode, depth-1, alpha, beta, !maxPlayer, model);
				
				if (rec > alpha) {
					res = new ArrayList<Position>();
					res.add(p);
				}
				else if (rec == alpha) res.add(p);
				
				alpha = Math.max(alpha, rec);
			}
			else {
				double rec = -alphaBeta_(newNode, depth-1, alpha, beta, !maxPlayer, model);
				
				if (rec < beta) {
					res = new ArrayList<Position>();
					res.add(p);
				}
				else if (rec == beta) res.add(p);
				
				beta = Math.min(beta, rec);
			}
			
			if (beta <= alpha) {
				break;
			}

		}
		
		return res.get(new Random().nextInt(res.size()));
	}
	
	
	public static double alphaBeta_(GomokuState node, int depth, double alpha, double beta, boolean maxPlayer, Classifier model) throws Exception {
		String x = "";
		for (int i = 0; i < 3 - depth; i++) x += "  ";
		
		if (depth == 0 || node.hasWon() != GomokuState.WON_NONE) {
			double val = Classification.getClassValue(node, model);
			System.out.println(x + (maxPlayer ? "max" : "min") + " : " + "end of line" + " -> " + val);
			return val;
		}
		
		for (Position p : node.choices()) {
			GomokuState newNode = node.makeMove(p);
			
			if (maxPlayer) {
				double rec = alphaBeta_(newNode, depth-1, alpha, beta, !maxPlayer, model);
				
				if (rec >= alpha) System.out.println("success " + rec + ", " + alpha);
				else System.out.println("no success " + rec + ", " + alpha);
				
				alpha = Math.max(alpha, rec);
				
				System.out.println(x + (maxPlayer ? "max" : "min") + " : " + p + " -> " + alpha);
			}
			else {
				double rec = -alphaBeta_(newNode, depth-1, alpha, beta, !maxPlayer, model);
				
				if (rec <= beta) System.out.println("success " + rec + ", " + beta);
				else System.out.println("no success " + rec + ", " + beta);
				
				beta = Math.min(beta, rec);
				
				System.out.println(x + (maxPlayer ? "max" : "min") + " : " + p + " -> " + beta);
			}
			
			if (beta <= alpha) {
				break;
			}

		}
		
		if (maxPlayer) return alpha;
		else return beta; 
	}
	
	public static GomokuState pickBest(GomokuState state, Classifier model) throws Exception {
		double best = -100;
		GomokuState res = null;
		for (Position p : state.choices()) {
			GomokuState s_ = state.makeMove(p);
			double d = Classification.getClassValue(s_, model);
			System.out.println(p + " " + d);
			if (d > best) {
				res = s_;
				best = d;
				System.out.println("Now best: " + d + " position " + p);
			}
		}
		
		return res;
	}
}

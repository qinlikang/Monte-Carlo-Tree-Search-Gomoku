package gomoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import gomoku.GomokuState.Position;

import weka.classifiers.Classifier;
import weka.core.Attribute;


public class AlphaBeta {
	private static final boolean debug = false; 
	
	public static Position negaMax(GomokuState node, int depth, Classifier model) throws Exception {
		if (depth == 0 || node.hasWon() != GomokuState.WON_NONE) {
			return null;
		}
		
		if(node.getPieces() == 0) return node.new Position( (int)(new Random().nextDouble()*5+1) 
														  , (int)(new Random().nextDouble()*5+1)
														  );
		
		double alpha = Double.NEGATIVE_INFINITY;
		double beta = Double.POSITIVE_INFINITY;
		int colour = 1;
		
		double loc_alpha = Double.NEGATIVE_INFINITY;

		ArrayList<Position> res = null;
		GomokuState tmp;
		for (Position p : node.choices()) {
			tmp = node.makeMove(p);
			
			double alpha_ = -negaMax_(tmp, depth-1, -beta, -alpha, -colour, model);
			if (alpha_ > loc_alpha) {
				loc_alpha = alpha_;
				alpha = alpha_;
				
				res = new ArrayList<GomokuState.Position>();
				p.diff2RowO  = tmp.numberOf_X_(2, 'C') - node.numberOf_X_(2, 'C');	
				p.diff3RowC = tmp.numberOf_X_(3, 'O') - node.numberOf_X_(3, 'C');
				
				p.diff_2_RowC = tmp.numberOf_X_(2, 'O') - node.numberOf_X_(2, 'C');
				p.diff2_RowC  = tmp.numberOfX_(2, 'O')  - node.numberOfX_(2, 'C');
				
				p.diff_1_RowO = tmp.numberOf_X_(1, 'C') - node.numberOf_X_(1, 'O');

				res.add(p);
				
				if (debug) System.out.println("New best: " + p + " with value of " + loc_alpha);
			}
			else if (alpha_ >= loc_alpha) {
				p.diff2RowO  = tmp.numberOf_X_(2, 'C') - node.numberOf_X_(2, 'O');	
				
				p.diff3RowC = tmp.numberOf_X_(3, 'O') - node.numberOf_X_(3, 'C');
				
				p.diff_2_RowC = tmp.numberOf_X_(2, 'O') - node.numberOf_X_(2, 'C');
				p.diff2_RowC  = tmp.numberOfX_(2, 'O')  - node.numberOfX_(2, 'C');
				
				p.diff_1_RowO = tmp.numberOf_X_(1, 'C') - node.numberOf_X_(1, 'O');

				res.add(p);
				
				if (debug) System.out.println("New equal best: " + p + " with value of " + loc_alpha);
			}
			
			if (loc_alpha > beta) break;
		}
		
		/***********************************************/
		Collections.sort(res, new Comparator<Position>() {
			
			public int compare(Position p1, Position p2) {
		        if(null == p1 || null == p2){
		            throw new NullPointerException();
		        }
		        else{
		        	//minimize opponent 2 in a row
		        	int diff2O_ = p1.diff2RowO - p2.diff2RowO;
		            if(0 == diff2O_){
		            	//maximize current 3 in a row
		            	int diff3C_ = p2.diff3RowC - p1.diff3RowC;
		            	//maximize current 2 in a row
		            	if(0 == diff3C_) {
		            		int diff2C_ = (p2.diff_2_RowC - p1.diff_2_RowC);
		            		if(0 == diff2C_) {
		            			return p1.diff_1_RowO - p2.diff_1_RowO;
		            		}
		            		else return diff2C_;
		            	}
		            	else return diff3C_;
		            }
		            
		            else{
		                return diff2O_;
		            }
		        }
		    }
		});
		int diff2RowO = res.get(0).diff2RowO;
		int diff3RowC = res.get(0).diff3RowC;
		int diff_2_RowC = res.get(0).diff_2_RowC;
		int diff2_RowC = res.get(0).diff2_RowC;
		int diff_1_RowO = res.get(0).diff_1_RowO;
		int i=0;
		for(Position p : res){
			if( diff2RowO == p.diff2RowO &&
			    diff3RowC == p.diff3RowC &&
				diff_2_RowC == p.diff_2_RowC &&
				diff2_RowC == p.diff2_RowC &&
				diff_1_RowO == p.diff_1_RowO) i++;
		}
		/*******************************************/
		return res.get(new Random().nextInt(i));
	}
	
	public static double negaMax_(GomokuState node, int depth, double alpha, double beta, int colour, Classifier model) throws Exception {
		if (depth == 0 || node.hasWon() != GomokuState.WON_NONE) {
			return -1 * Classification.getClassValue(node, model);
		}
		
		double loc_alpha = Double.NEGATIVE_INFINITY;
		
		for (Position p : node.choices()) {
			double alpha_ = -negaMax_(node.makeMove(p), depth-1, -beta, -alpha, -colour, model);
			
			if (alpha_ > loc_alpha) {
				loc_alpha = alpha_;
				alpha = alpha_;
			}
			
			if (loc_alpha > beta) break;
		}
		
		return alpha;
	}
}
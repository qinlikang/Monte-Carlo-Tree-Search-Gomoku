package gomoku;

public class MonteCarloTreeSearch {

	public MonteCarloTreeSearch() {
		
	}
	
	public static Node run(Node start, int n) throws Exception {
		Node root = start.duplicateWithoutChildren();
		for(int i = 0; i < n; i++ ){ 
			Node selected_expanded = root.selectionExpansion();
			int won = selected_expanded.simulation();
			selected_expanded.backPropagation2(won);
		}
		return root;
	}
	
	public float getScore(Node start, int n, boolean after) throws Exception  {
		Node r = run(start, n);
		
		if (after) return (float)r.value / (float)n;
		
		int max = Integer.MIN_VALUE;
		for (Node ch : r.children) {
			if (ch.value > max) max = ch.value;
		}
		return (float)max  / (float)n;
	}
	
	public float getScoreMultiple(Node start, int n, int repeats, boolean after) throws Exception  {
		float d = 0;
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < repeats; i++) {
			d += getScore(start, n, after);
			if(System.currentTimeMillis() - startTime > 600000) throw new Exception("TIMEOUT 10mins");
		}
		return d/repeats;
	}

	public float getScoreMultipleErr(Node start, int n, float err, float step, int stepstart, boolean after) throws Exception {
		float d = getScore(start, n, after);
		int s = stepstart;
		
		System.out.println("S: " + s + ", V: " + d);
		while (true) {
			s *= step;
			float d_ = getScoreMultiple(start, n, s, after);
			
			System.out.println("S: " + s + ", V: " + d_);
			System.out.println((Math.abs(1 - Math.abs(d/d_))) + ", " + err);
			
			if (d - d_ == 0.0d) return d_;
			if (Math.abs(1 - Math.abs(d/d_)) < err) return d_; 
			
			d = d_;
			
		}
	}
}

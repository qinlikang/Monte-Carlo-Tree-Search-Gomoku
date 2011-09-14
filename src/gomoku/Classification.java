package gomoku;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class Classification {
	
	public static final int discretize = 8;
	public static final int classInd = 33; //classIndex
	public static Instance wekaInstanceW(GomokuState state, FastVector attrs) {

		GomokuState prevstate = state.undoMove(state.lastMove);
		int attr1 = state.numberOf_X_(1, 'C');
		int attr2 = state.numberOf_X_(2, 'C');
		int attr3 = state.numberOfX_(1, 'C');
		int attr4 = state.numberOfX_(2, 'C');
		int attr5 = state.numberOfX_(3, 'C');
		int attr6 = state.numberOf_X_X_('C');
		int attr7 = state.numberOfXX_X('C');
		
		int attr8 = state.numberOf_X_(1, 'O');
		int attr9 = state.numberOf_X_(2, 'O');
		int attr10 = state.numberOfX_(1, 'O');
		int attr11 = state.numberOfX_(2, 'O');
		int attr12 = state.numberOfX_(3, 'O');
		int attr13 = state.numberOf_X_X_('O');
		int attr14 = state.numberOfXX_X('O');
		
		double avgDist = state.avgDistance();
		
		Instance inst = new Instance(classInd + 1);
		inst.setValue((Attribute)attrs.elementAt(0), (double)state.getPieces());
		inst.setValue((Attribute)attrs.elementAt(1), avgDist);
		inst.setValue((Attribute)attrs.elementAt(2), avgDist - prevstate.avgDistance());
		
		inst.setValue((Attribute)attrs.elementAt(3), attr1);
		inst.setValue((Attribute)attrs.elementAt(4), attr2);
		inst.setValue((Attribute)attrs.elementAt(5), attr3);
		inst.setValue((Attribute)attrs.elementAt(6), attr4);
		inst.setValue((Attribute)attrs.elementAt(7), attr5);
		inst.setValue((Attribute)attrs.elementAt(8), attr6);
		inst.setValue((Attribute)attrs.elementAt(9), attr7);
		
		inst.setValue((Attribute)attrs.elementAt(10), attr8);
		inst.setValue((Attribute)attrs.elementAt(11), attr9);
		inst.setValue((Attribute)attrs.elementAt(12), attr10);
		inst.setValue((Attribute)attrs.elementAt(13), attr11);
		inst.setValue((Attribute)attrs.elementAt(14), attr12);
		inst.setValue((Attribute)attrs.elementAt(15), attr13);
		inst.setValue((Attribute)attrs.elementAt(16), attr14);
		
		inst.setValue((Attribute)attrs.elementAt(17), attr1 - prevstate.numberOf_X_(1, 'O'));
		inst.setValue((Attribute)attrs.elementAt(18), attr2 - prevstate.numberOf_X_(2, 'O'));
		inst.setValue((Attribute)attrs.elementAt(19), attr3 - prevstate.numberOfX_(1, 'O'));
		inst.setValue((Attribute)attrs.elementAt(20), attr4 - prevstate.numberOfX_(2, 'O'));
		inst.setValue((Attribute)attrs.elementAt(21), attr5 - prevstate.numberOfX_(3, 'O'));
		inst.setValue((Attribute)attrs.elementAt(22), attr6 - prevstate.numberOf_X_X_('O'));
		inst.setValue((Attribute)attrs.elementAt(23), attr7 - prevstate.numberOfXX_X('O'));
		
		inst.setValue((Attribute)attrs.elementAt(24), attr8 - prevstate.numberOf_X_(1, 'C'));
		inst.setValue((Attribute)attrs.elementAt(25), attr9 - prevstate.numberOf_X_(2, 'C'));
		inst.setValue((Attribute)attrs.elementAt(26), attr10 - prevstate.numberOfX_(1, 'C'));
		inst.setValue((Attribute)attrs.elementAt(27), attr11 - prevstate.numberOfX_(2, 'C'));
		inst.setValue((Attribute)attrs.elementAt(28), attr12 - prevstate.numberOfX_(3, 'C'));
		inst.setValue((Attribute)attrs.elementAt(29), attr13 - prevstate.numberOf_X_X_('C'));
		inst.setValue((Attribute)attrs.elementAt(30), attr14 - prevstate.numberOfXX_X('C'));
		
		inst.setValue((Attribute)attrs.elementAt(31), state.longestChain('C'));
		inst.setValue((Attribute)attrs.elementAt(32), state.longestChain('O'));
		
		return inst;
	}
	
	public static FastVector attributes(int discret) {
		FastVector attrs = new FastVector(classInd + 1);
		attrs.addElement(new Attribute("Tokens"));
		attrs.addElement(new Attribute("Average Distance"));
		attrs.addElement(new Attribute("diff Avg. Distance"));
		
		attrs.addElement(new Attribute("_C_"));
		attrs.addElement(new Attribute("_CC_"));
		attrs.addElement(new Attribute("C_"));
		attrs.addElement(new Attribute("CC_"));
		attrs.addElement(new Attribute("CCC_"));
		attrs.addElement(new Attribute("_C_C_"));
		attrs.addElement(new Attribute("CC_C"));
		
		attrs.addElement(new Attribute("_O_"));
		attrs.addElement(new Attribute("_OO_"));
		attrs.addElement(new Attribute("O_"));
		attrs.addElement(new Attribute("OO_"));
		attrs.addElement(new Attribute("OOO_"));
		attrs.addElement(new Attribute("_O_O_"));
		attrs.addElement(new Attribute("OO_O"));
		
		attrs.addElement(new Attribute("diff _C_"));
		attrs.addElement(new Attribute("diff _CC_"));
		attrs.addElement(new Attribute("diff C_"));
		attrs.addElement(new Attribute("diff CC_"));
		attrs.addElement(new Attribute("diff CCC_"));
		attrs.addElement(new Attribute("diff _C_C_"));
		attrs.addElement(new Attribute("diff CC_C"));
		
		attrs.addElement(new Attribute("diff _O_"));
		attrs.addElement(new Attribute("diff _OO_"));
		attrs.addElement(new Attribute("diff O_"));
		attrs.addElement(new Attribute("diff OO_"));
		attrs.addElement(new Attribute("diff OOO_"));
		attrs.addElement(new Attribute("diff _O_O_"));
		attrs.addElement(new Attribute("diff OO_O"));
		
		attrs.addElement(new Attribute("Longest Chain Current"));
		attrs.addElement(new Attribute("Longest Chain Opponent"));
		
		FastVector fvClassVal = new FastVector(discret);
		for (int i = -discret; i <= discret; i++) {
			fvClassVal.addElement("" + ((float)i/discret));
		}
		Attribute classAttribute = new Attribute("Value", fvClassVal);
		attrs.addElement(classAttribute);
		
		return attrs;
	}
	
	
	public static Instances readClassified(String[] files, String instanceName) throws Exception {
		FastVector attrs = attributes(discretize);
		Instances set = new Instances(instanceName, attrs, 10);
		
		for (int i = 0; i < files.length; i++) {
			String file = files[i];
			
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String strLine;
			
			while ((strLine = br.readLine()) != null)   {
				Instance inst = new Instance(classInd + 1);
				
				int j;
				int c = 0;
				while ((j = strLine.indexOf("|")) != -1 ) {
					String sub = strLine.substring(0, j);
					strLine = strLine.substring(j+1);
					
					inst.setValue((Attribute)attrs.elementAt(c), Double.parseDouble(sub));
					
					c++;
				}
				inst.setValue((Attribute)attrs.elementAt(c), strLine);
				
				set.add(inst);
			}
		}
		
		return set;
	}
	
	public static Classifier createModel(String[] learningSet, boolean pruning, boolean print)  throws Exception {
		String[] files = new String[learningSet.length];
		
		for (int i = 0; i < files.length; i++)
			files[i] = learningSet[i] + "_classified_" + discretize;
		
		Instances isLearningSet = readClassified(files, "Learning Data");
		isLearningSet.setClassIndex(classInd);
		
		Classifier cModel = (Classifier)new J48();
		((J48)cModel).setReducedErrorPruning(pruning);
		
		cModel.buildClassifier(isLearningSet);
		
		if (print) {
			System.out.println(((J48)cModel).toString());
		}
		
		return cModel;
	}
	
	public static void test(String[] testingSet, Classifier model) throws Exception {
		String[] filesTest = new String[testingSet.length];
		for (int i = 0; i < testingSet.length; i++)
			filesTest[i] = testingSet[i] + "_classified_" + discretize;
		
		Instances isTestingSet = readClassified(filesTest, "Testing Data");
		isTestingSet.setClassIndex(classInd);
		
		Evaluation eTest = new Evaluation(isTestingSet);
		eTest.evaluateModel(model, isTestingSet);
		 
		String strSummary = eTest.toSummaryString();
		System.out.println(strSummary);

		double[][] cmMatrix = eTest.confusionMatrix();
		for(int row_i=0; row_i<cmMatrix.length; row_i++){
			for(int col_i=0; col_i<cmMatrix.length; col_i++){
				String s = ""+cmMatrix[row_i][col_i];
				while (s.length() < 6) s+=" ";
				System.out.print(s);
				System.out.print("|");
			}
			System.out.println();
		}
		
		System.out.println();
		System.out.println("Deviation: " + (deviation(cmMatrix) * 1.0f/discretize));
		
	}
	
	public static double deviation(double[][] matrix) {
		int counter = 0;
		double value = 0;
		
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				counter += matrix[i][j];
				value += matrix[i][j] * Math.abs(j-i);
			}
		}
		
		return value / counter;
	}
	
	public static double getClassValue(GomokuState state, Classifier model) throws Exception {
		FastVector attrs = attributes(discretize);
		Instances set = new Instances("Test", attrs, 10);
		set.setClassIndex(classInd);
		
		Instance i = wekaInstanceW(state, attrs);
		set.add(i);
		return Double.parseDouble(set.classAttribute().value(
				(int)model.classifyInstance(
						set.instance(
								set.numInstances()-1)
							)));
	}
}

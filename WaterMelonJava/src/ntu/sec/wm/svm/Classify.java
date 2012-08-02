package ntu.sec.wm.svm;

import java.io.IOException;
import java.util.Vector;

import ntu.sec.wm.constant.Constant;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public class Classify {
	private String model_file_name = "wm/model";
	private Vector<float[]> input;
	private int label;
	private int correct;
	
	public Classify(Vector<float[]> test, int label) {
		this.input = test;
		this.label = label;
		correct = 0;
	}

	public void run() {
		try {
			svm_model model = svm.svm_load_model(model_file_name);

			for (int i = 0; i < input.size(); i++) {
				svm_node[] in = new svm_node[input.get(i).length];
				for (int j = 0; j < in.length; j++) {
					in[j] = new svm_node();
					in[j].index = j + 1;
					in[j].value = input.get(i)[j];
				}

				// Constant.print(in);

				double output = svm.svm_predict(model, in);

				if(output == label)
					correct++;
				
				if (output == 1)
					System.out.println("\nTest result is " + output
							+ " ----> WaterMelon");
				else
					System.out.println("\nTest result is " + output
							+ " ----> Not a WaterMelon");
			}
			System.out.println(correct + " Correct in "+input.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

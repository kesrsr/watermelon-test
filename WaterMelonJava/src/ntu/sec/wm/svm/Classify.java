package ntu.sec.wm.svm;

import java.io.IOException;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

public class Classify {
	private String model_file_name = "wm/model";
	private float[] input;
	//the period of features
	private int min = 100;
	private int max = 350;

	public Classify(float[] test) {
		this.input = test;
	}

	public void run() {
		try {
			svm_model model = svm.svm_load_model(model_file_name);

			svm_node[] in = new svm_node[max-min];
			for (int j = 0; j < in.length; j++) {
				in[j] = new svm_node();
				in[j].index = j;
				in[j].value = input[min+j];
			}

			double output = svm.svm_predict(model, in);

			if (output > 0.999)
				System.out.println("\nTest result is " + output
						+ " ----> WaterMelon");
			else
				System.out.println("\nTest result is " + output
						+ " ----> Not a WaterMelon");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}

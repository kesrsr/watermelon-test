package sec.fcl.audio;

import java.util.Vector;

import sec.fcl.features.ShortTimeEnergy;
import sec.fcl.preprocessing.Preprocessing;
import sec.fcl.svm.Classify;

public class AudioClassify {
	Vector<float[]> features_vector;
	String file = null;

	public AudioClassify(String file) {
		this.file = file;
		features_vector = new Vector<float[]>();
	}
	
	public void run(){
		if(file == null)
			return;
		
		Preprocessing pre_wm = new Preprocessing(file);
		pre_wm.run();

		ShortTimeEnergy ste = new ShortTimeEnergy(pre_wm.getFilterFrames());
		ste.calculate();

		float[][] features = ste.getFeature();

		// System.out.println(files[i] + " " + features.length);
		for (int j = 0; j < features.length; j++) {
			features_vector.add(features[j]);
		}
		
		Classify classify = new Classify(features_vector, 1);
		classify.run();
	}
}

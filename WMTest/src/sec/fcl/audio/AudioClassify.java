package sec.fcl.audio;

import java.util.Vector;

import sec.fcl.features.ShortTimeEnergy;
import sec.fcl.preprocessing.Preprocessing;
import sec.fcl.svm.Classify;

public class AudioClassify {
	Vector<float[]> features_vector;
	String file = null;
	float noise;

	public AudioClassify(String file, float noise) {
		this.file = file;
		this.noise = noise;
		features_vector = new Vector<float[]>();
	}

	public int run() {
		if (file == null)
			return -1;

		Preprocessing pre_wm = new Preprocessing(file, noise);
		pre_wm.run();

		if (pre_wm.getFilterFrames() == null)
			return -1;

		ShortTimeEnergy ste = new ShortTimeEnergy(pre_wm.getFilterFrames());
		ste.calculate();

		float[][] features = ste.getFeature();

		// System.out.println(files[i] + " " + features.length);
		for (int j = 0; j < features.length; j++) {
			features_vector.add(features[j]);
		}

		if (features_vector.size() == 0)
			return -1;

		Classify classify = new Classify(features_vector, 1);
		float result = classify.run();

		if (result >= 0.5f)
			return 1;
		else
			return 0;
	}
}

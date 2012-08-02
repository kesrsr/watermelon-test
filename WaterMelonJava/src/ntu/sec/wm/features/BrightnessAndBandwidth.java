package ntu.sec.wm.features;

import java.util.Vector;

public class BrightnessAndBandwidth {
	float[] brightness;
	float[] bandwidth;
	Vector<float[]> frequency_specturms;

	public BrightnessAndBandwidth(Vector<float[]> specturms) {
		this.frequency_specturms = specturms;
		brightness = new float[frequency_specturms.size()];
		bandwidth = new float[frequency_specturms.size()];
	}

	public void calculate() {
		for (int i = 0; i < frequency_specturms.size(); i++) {
			float[] specturm = frequency_specturms.get(i);

			float numerator = 0;
			float denominator = 0;

			for (int j = 0; j < specturm.length; j++) {
				numerator += j * specturm[j] * specturm[j];
				denominator += specturm[j] * specturm[j];
			}

			brightness[i] = numerator / denominator;
			System.out.println(brightness[i]);
		}

		for (int i = 0; i < frequency_specturms.size(); i++) {
			float[] specturm = frequency_specturms.get(i);

			float numerator = 0;
			float denominator = 0;

			for (int j = 0; j < specturm.length; j++) {
				numerator += (j - brightness[i]) * (j - brightness[i])
						* specturm[j] * specturm[j];
				denominator += specturm[j] * specturm[j];
			}
			
			bandwidth[i] = (float) Math.sqrt(numerator / denominator);
			System.out.println(bandwidth[i]);
		}
	}
}

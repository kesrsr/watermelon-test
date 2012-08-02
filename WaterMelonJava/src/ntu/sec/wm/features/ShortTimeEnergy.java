package ntu.sec.wm.features;

import java.util.Vector;

import ntu.sec.wm.constant.Constant;

public class ShortTimeEnergy {
	float[] ste;
	float[][] sub_band_ste;
	Vector<float[]> specturms;

	public ShortTimeEnergy(Vector<float[]> specturms) {
		this.specturms = specturms;
		ste = new float[specturms.size()];
		sub_band_ste = new float[specturms.size()][];
	}

	public void calculate() {
		for (int i = 0; i < specturms.size(); i++) {
			float[] specturm = specturms.get(i);

			float sum = 0;
			for (int j = 0; j < specturm.length; j++) {
				sum += specturm[j] * specturm[j];
			}

			ste[i] = sum;

			sub_band_ste[i] = new float[4];

			sub_band_ste[i][0] = 1 / ste[i]
					* sub_brand_sum(specturm, 0, specturm.length / 8);
			sub_band_ste[i][1] = 1
					/ ste[i]
					* sub_brand_sum(specturm, specturm.length / 8,
							specturm.length / 4);
			sub_band_ste[i][2] = 1
					/ ste[i]
					* sub_brand_sum(specturm, specturm.length / 4,
							specturm.length / 2);
			sub_band_ste[i][3] = 1
					/ ste[i]
					* sub_brand_sum(specturm, specturm.length / 2,
							specturm.length);

//			Constant.print(sub_band_ste[i]);
		}

	}
	
	public float[] getSTE(){
		return ste;
	}
	
	public float[][] getSubSTE(){
		return this.sub_band_ste;
	}
	
	public float[][] getFeature(){
		float[][] feature = new float[ste.length][5];
		for(int i = 0; i < ste.length; i++)
		{
			feature[i][0] = ste[i];
			for(int j = 0; j < 4; j++)
				feature[i][j+1] = sub_band_ste[i][j];
		}
		
		return feature;
	}

	private float sub_brand_sum(float[] specturm, int low, int high) {
		float sum = 0;
		for (int i = low; i < high; i++)
			sum += specturm[i] * specturm[i];
		return sum;
	}
}

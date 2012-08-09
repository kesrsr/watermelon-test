package sec.fcl.filter;

public class Filter {
	double[] b;
	double[] a;

	public Filter(double[] B, double[] A) {
		this.b = B;
		this.a = A;

		if (a[0] == 1.) {
			for (int i = 1; i < a.length; ++i)
				a[i] /= a[0];
			for (int i = 0; i < b.length; ++i)
				b[i] /= a[0];
		}
	}

	public float[] filter(float[] sample) {
		float[] yv = new float[sample.length];
		for (int i = 0; i < sample.length; ++i) {
			// compute the output
			double buf = b[0] * sample[i];
			for (int j = 1; j < b.length; ++j)
				buf += b[j] * sample[(i - j + sample.length) % sample.length];

			for (int j = 1; j < a.length; ++j)
				buf -= a[j] * yv[(i - j + yv.length) % yv.length];

			// save the result
			yv[i] = (float)buf;
		}
		
		return yv;
	}

}

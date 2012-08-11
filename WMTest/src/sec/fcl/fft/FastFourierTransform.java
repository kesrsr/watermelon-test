package sec.fcl.fft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

//import com.badlogic.audio.analysis.FFT;

public class FastFourierTransform {
	Vector<float[]> time_domain;
	private final int sample_rate = 44100;
	// only the frequency from 0-1500 are meaningful
	// private int cut = 600;
	private int min_length = 300;

	// the period of features
	private int min = 0;
	private int max = 1000;

	public FastFourierTransform(Vector<float[]> origin) {
		time_domain = origin;
	}

	public void run() {
//		Plot plot = new Plot("Note A Spectrum", 512, 512);
		for (int i = 0; i < time_domain.size(); i++) {
			if (time_domain.get(i).length < min_length)
				continue;

			int pow_two = next_pow_of_two(time_domain.get(i).length);
			float[] time_specturm = new float[pow_two];
			System.arraycopy(time_domain.get(i), 0, time_specturm, 0,
					time_domain.get(i).length);
			// padding 0 at the end
			for (int j = time_domain.get(i).length; j < pow_two; j++)
				time_specturm[j] = 0;
//			FFT fft = new FFT(time_specturm.length, sample_rate);

//			fft.forward(time_specturm);

			// plot.plot(fft.getSpectrum(), 1, Color.RED);
		}
	}

	public Vector<float[]> getFFT() {
		Vector<float[]> frequency_specturm = new Vector<float[]>();
		for (int i = 0; i < time_domain.size(); i++) {
			if (time_domain.get(i).length < min_length)
				continue;

			int pow_two = next_pow_of_two(time_domain.get(i).length);
			float[] time_specturm = new float[pow_two];
			System.arraycopy(time_domain.get(i), 0, time_specturm, 0,
					time_domain.get(i).length);
			// padding 0 at the end
			for (int j = time_domain.get(i).length; j < pow_two; j++)
				time_specturm[j] = 0;

//			FFT fft = new FFT(time_specturm.length, sample_rate);
//			fft.forward(time_specturm);

//			float[] spectrum = new float[max - min];
//			System.arraycopy(fft.getSpectrum(), min, spectrum, 0, max - min);
//			frequency_specturm.add(spectrum);
		}

		Vector<float[]> frequency_specturm_average = new Vector<float[]>();
		float[] average = new float[max - min];
		for (int i = 0; i < average.length; i++) {
			float sum = 0;
			for (int j = 0; j < frequency_specturm.size(); j++) {
				sum += frequency_specturm.get(j)[i];
			}
			average[i] = sum / frequency_specturm.size();
		}
		frequency_specturm_average.add(average);

		return frequency_specturm;
	}

	public void write_to_csv_file(String file_name) {
		try {
			File file = new File(file_name);

			if (!file.exists())
				file.createNewFile();

			FileWriter fw = new FileWriter(file);

			Vector<float[]> fft = getFFT();
			for (int i = 0; i < fft.size(); i++) {
				float[] frequency_spectrum = fft.get(i);

				for (int j = 0; j < frequency_spectrum.length; j++) {
					fw.append(new String(frequency_spectrum[j] + ""));
					fw.append(',');
				}
				fw.append('\n');
			}

			fw.flush();
			fw.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Write ends");
	}

	private int next_pow_of_two(int length) {
		int pow = 2;
		while (pow < length) {
			pow *= 2;
		}

		return pow;
	}
}

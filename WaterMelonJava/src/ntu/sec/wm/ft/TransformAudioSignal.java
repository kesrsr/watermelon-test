package ntu.sec.wm.ft;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import com.badlogic.audio.analysis.FFT;
import com.badlogic.audio.visualization.Plot;

public class TransformAudioSignal {
	Vector<float[]> time_domain;
	private final int sample_rate = 44100;

	public TransformAudioSignal(Vector<float[]> origin) {
		time_domain = origin;
	}

	public void run() {
		Plot plot = new Plot("Note A Spectrum", 512, 512);
		for (int i = 0; i < time_domain.size(); i++) {
			int pow_two = next_pow_of_two(time_domain.get(i).length);
			float[] time_specturm = new float[pow_two];
			System.arraycopy(time_domain.get(i), 0, time_specturm, 0,
					time_domain.get(i).length);
			// padding 0 at the end
			for (int j = time_domain.get(i).length; j < pow_two; j++)
				time_specturm[j] = 0;
			FFT fft = new FFT(time_specturm.length, sample_rate);

			fft.forward(time_specturm);

			plot.plot(fft.getSpectrum(), 1, Color.red);
		}
	}

	public void write_to_csv_file(String file_name) {
		try {
			File file = new File(file_name);

			if (!file.exists())
				file.createNewFile();

			FileWriter fw = new FileWriter(file);

			for (int i = 0; i < time_domain.size(); i++) {
				int pow_two = next_pow_of_two(time_domain.get(i).length);
				float[] time_specturm = new float[pow_two];
				System.arraycopy(time_domain.get(i), 0, time_specturm, 0,
						time_domain.get(i).length);
				// padding 0 at the end
				for (int j = time_domain.get(i).length; j < pow_two; j++)
					time_specturm[j] = 0;
				FFT fft = new FFT(time_specturm.length, sample_rate);

				fft.forward(time_specturm);
				
				float[] frequency_spectrum = fft.getSpectrum();

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
		System.out.println(length);
		int pow = 2;
		while (pow < length) {
			pow *= 2;
		}

		return pow;
	}
}

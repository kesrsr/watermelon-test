package sec.fcl.preprocessing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Vector;

import sec.fcl.filter.Butterworth;
import sec.fcl.filter.Filter;

import com.badlogic.audio.io.WaveDecoder;

public class Preprocessing {
	int windowSize = 40;
	float[] samples = new float[1024];
	float[] origin_samples = new float[1024];
	float thre = 0.025f;
	ArrayList<Integer> list = new ArrayList<Integer>();
	ArrayList<Integer> period;
	String input_file;
	private int min_frame_length = 1500;
	
	Butterworth bw;
	Filter filter;

	public Preprocessing(String input) {
		this.input_file = input;
		
		bw = new Butterworth(2, 0.05f, true);
		filter = new Filter(bw.computeB(), bw.computeA());
	}

	public static void main(String[] args) {
		(new Preprocessing("wm/Close/GOOD_2012_6_12_22_25_57.wav")).run();
	}

	public void run() {
		WaveDecoder decoder = null;
		try {
			decoder = new WaveDecoder(new FileInputStream(input_file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ArrayList<Float> allSamples = new ArrayList<Float>();

		while (decoder != null && decoder.readSamples(samples) > 0) {
			for (int i = 0; i < samples.length; i++)
				allSamples.add(samples[i]);
		}

		samples = new float[padding(allSamples.size())];
		origin_samples = new float[padding(allSamples.size())];
		for (int i = 0; i < allSamples.size(); i++)
			samples[i] = allSamples.get(i);

		for (int i = allSamples.size(); i < samples.length; i++)
			samples[i] = 0;

		System.arraycopy(samples, 0, origin_samples, 0, origin_samples.length);

		root_mean_square(samples);
		extract_frames(samples);
		remove_fake_frames();

//		Plot plot = new Plot("Test", 512, 512);
//		plot.plot(getFrames().get(0), 100, Color.red);
	}

	// return the minimum length of multiple of window size
	private int padding(int length) {
		int p = length / windowSize;
		if (length % windowSize != 0)
			return (p + 1) * windowSize;
		else
			return length;
	}

	// return the root mean square
	private float[] root_mean_square(float[] samples) {
		float[] root_mean_square = new float[samples.length / windowSize];

		for (int i = 0; i < root_mean_square.length; i++) {
			float sum = 0;
			for (int j = 0; j < windowSize; j++)
				sum += pow(samples[i * windowSize + j]);
			root_mean_square[i] = (float) Math.sqrt(sum / windowSize);

			// if the root mean square is low than threshold change to 0
			if (root_mean_square[i] < thre)
				for (int j = 0; j < windowSize; j++)
					samples[i * windowSize + j] = 0;
		}

		return root_mean_square;
	}

	private float pow(float i) {
		return i * i;
	}

	private void extract_frames(float[] samples) {
		boolean start = false;
		int size = 0;
		for (int i = 0; i < samples.length; i++)
			if (!start) {
				if (samples[i] != 0) {
					start = true;
					list.add(i);
					size++;
				}
			} else if (samples[i] == 0) {
				start = false;
				list.add(i);
				size++;
			}

		period = new ArrayList<Integer>();
		period.add(list.get(0));
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i) - list.get(i - 1) > min_frame_length) {
				period.add(list.get(i - 1));
				period.add(list.get(i));
			}
		}
		period.add(list.get(list.size() - 1));
	}

	private void remove_fake_frames() {
		if (period == null || period.size() == 0)
			return;

		for (int i = 0; i < period.size() / 2; i++)
			if (period.get(i * 2 + 1) - period.get(i * 2) < min_frame_length) {
				// System.out.println("remove " + period.get(i * 2) + " and "
				// + period.get(i * 2 + 1));
				period.remove(i * 2 + 1);
				period.remove(i * 2);
			}
	}

	public Vector<float[]> getFrames() {
		Vector<float[]> frames = new Vector<float[]>();

		if (period == null || period.size() == 0)
			return null;
		for (int i = 0; i < period.size() / 2; i++) {
			System.out.print(period.get(i * 2) + "-->" + period.get(i * 2 + 1)
					+ "\n");
			float[] frame = getFrames(origin_samples, period.get(i * 2),
					period.get(i * 2 + 1));
			frames.add(frame);
		}

		return frames;
	}
	
	public Vector<float[]> getFilterFrames() {
		Vector<float[]> frames = new Vector<float[]>();

		if (period == null || period.size() == 0)
			return null;
		for (int i = 0; i < period.size() / 2; i++) {
			float[] frame = getFrames(origin_samples, period.get(i * 2),
					period.get(i * 2 + 1));
			
			frames.add(filter.filter(frame));
		}

		return frames;
	}

	private float[] getFrames(float[] origin, int start, int end) {
		if (start < 0 || end >= origin.length)
			return null;

		float[] frame = new float[end - start + 1];

		System.arraycopy(origin, start, frame, 0, end - start + 1);

		return frame;
	}
}

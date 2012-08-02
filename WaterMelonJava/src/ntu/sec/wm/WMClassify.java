package ntu.sec.wm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import ntu.sec.wm.features.ShortTimeEnergy;
import ntu.sec.wm.preprocessing.Preprocessing;
import ntu.sec.wm.svm.Classify;

public class WMClassify {
	Vector<float[]> samples;

	String folder_pos = "wm/Train/Ripe/";
	String folder_neg = "wm/Train/Unripe/";
	String folder_test_pos = "wm/Test/Ripe/";
	String folder_test_neg = "wm/Test/Unripe/";
	String test = "wm/Test/Ripe/GOOD_2012_4_10_16_40_30";

	// String test = "wm/Far/GOOD_2012_6_12_22_55_54";

	public static void main(String[] args) {
		(new WMClassify()).run();
	}

	public void run() {

		Vector<float[]> train_positive_feature = new Vector<float[]>();
		Vector<float[]> train_negative_feature = new Vector<float[]>();

		Vector<float[]> test_pos_feature = new Vector<float[]>();
		Vector<float[]> test_neg_feature = new Vector<float[]>();

		loadFeature(folder_pos, train_positive_feature);
		loadFeature(folder_neg, train_negative_feature);
		loadFeature(folder_test_pos, test_pos_feature);
		loadFeature(folder_test_neg, test_neg_feature);

		Classify classify_unripe = new Classify(test_neg_feature, 2);
		classify_unripe.run();
		
		Classify classify_ripe = new Classify(test_pos_feature, 1);
		classify_ripe.run();
	}

	private void loadFeature(String folder, Vector<float[]> features_vector) {
		File pos_folder = new File(folder);
		if (pos_folder.isDirectory()) {
			String[] files = pos_folder.list();
			for (int i = 0; i < files.length; i++)
				if (!files[i].startsWith(".")) {
					Preprocessing pre_wm = new Preprocessing(pos_folder + "/"
							+ files[i]);
					pre_wm.run();
					ShortTimeEnergy ste = new ShortTimeEnergy(
							pre_wm.getFilterFrames());
					ste.calculate();

					float[][] features = ste.getFeature();

					// System.out.println(files[i] + " " + features.length);
					for (int j = 0; j < features.length; j++) {
						// System.out.println(files[i] + " " +
						// features[j].length);
						features_vector.add(features[j]);
					}
				}
		}
	}

	protected void print(float[] array) {
		System.out.println("Length: " + array.length);
		for (int i = 0; i < array.length; i++)
			System.out.print(array[i] + " ");
	}

	protected void write(String file_name, Vector<float[]> write) {
		try {
			File file = new File(file_name);

			if (!file.exists())
				file.createNewFile();

			FileWriter fw = new FileWriter(file);

			for (int i = 0; i < write.size(); i++) {
				float[] frequency_spectrum = write.get(i);

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
}

package ntu.sec.wm;

import java.util.Vector;

import ntu.sec.wm.ft.TransformAudioSignal;

import ntu.sec.wm.preprocessing.Preprocessing;
import ntu.sec.wm.svm.Classify;
import ntu.sec.wm.svm.Train;

public class WMClassify {
	Vector<float[]> samples;
	private String wav = ".wav";
	private String csv = ".csv";
	
	String input_wm = "wm/Far/GOOD_2012_6_12_22_55_17";
	String input_chair = "wm/Bad/BAD_2012_6_20_14_37_31";

//	String test = "wm/Bad/BAD_2012_6_20_14_37_22";
	String test = "wm/Far/GOOD_2012_6_12_22_55_17";

	public static void main(String[] args) {
		(new WMClassify()).run();
	}

	public void run() {
		Preprocessing pre_wm = new Preprocessing(input_wm+wav);
		pre_wm.run();
		TransformAudioSignal fft_wm = new TransformAudioSignal(
				pre_wm.getFrames());

		Preprocessing pre_chair = new Preprocessing(input_chair+wav);
		pre_chair.run();
		TransformAudioSignal fft_chair = new TransformAudioSignal(
				pre_chair.getFrames());

		Preprocessing pre_test = new Preprocessing(input_wm+wav);
		pre_test.run();
		TransformAudioSignal fft_test = new TransformAudioSignal(
				pre_test.getFrames());

		fft_wm.write_to_csv_file(input_wm+csv);
		fft_chair.write_to_csv_file(input_chair+csv);
//		fft_test.write_to_csv_file(test+csv);
		
		Train train = new Train(fft_wm.getFFT(), fft_chair.getFFT());
		train.run();
		Classify classify = new Classify(fft_wm.getFFT().get(0));
		classify.run();
		
		print(fft_wm.getFFT().get(0));
	}
	
	private void print(float[] array){
		System.out.println("Length: "+array.length);
		for(int i = 0; i < array.length; i++)
			System.out.print(array[i] +" ");
	}
}

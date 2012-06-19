package ntu.sec.wm;

import java.util.Vector;

import ntu.sec.wm.ft.TransformAudioSignal;

import ntu.sec.wm.preprocessing.Preprocessing;

public class WMClassify {
	Vector<float[]> samples;
	public static void main(String[] args){
		(new WMClassify()).run();
	}
	
	public void run(){
		Preprocessing pre = new Preprocessing();
		
		pre.run();
		
		samples = pre.getFrames();
		
		TransformAudioSignal fft = new TransformAudioSignal(samples);
		fft.write_to_csv_file("wm/results/Far/GOOD_2012_6_12_22_25_24.csv");
	}
}

package ntu.sec.wm;

import java.awt.Color;
import java.util.Vector;

import ntu.sec.wm.ft.TransformAudioSignal;

import com.badlogic.audio.visualization.Plot;

import wm.preprocessing.Preprocessing;

public class WMClassify {
	Vector<float[]> samples;
	public static void main(String[] args){
		(new WMClassify()).run();
	}
	
	public void run(){
		Preprocessing pre = new Preprocessing();
		
		pre.run();
		
		samples = pre.getFrames();

//		Plot plot = new Plot("Wave Plot", 512, 512);
//
//		plot.plot(samples.get(3), 44100 / 1000, Color.red);	
		
		TransformAudioSignal fft = new TransformAudioSignal(samples.get(1));
		fft.run();
	}
}

package sec.fcl.features;

import java.util.Vector;

public class ZeroCrossingRate {
	Vector<float[]> frames;
	float[] zcr;
	public ZeroCrossingRate(Vector<float[]> frames){
		this.frames = frames;
		zcr = new float[frames.size()];
	}
	
	public void calculate(){
		for(int i = 0; i < frames.size(); i++)
		{
			float[] frame_window = frames.get(i);
			
			float sum = 0;
			for(int j = 0; j < frame_window.length - 1; j ++)
				sum += Math.abs(Math.signum(frame_window[j + 1]) - Math.signum(frame_window[j]));
			
			zcr[i] = sum / (2 * (frame_window.length - 1));
			System.out.println("Zero Cross Rate "+zcr[i]);
		}
	}
}

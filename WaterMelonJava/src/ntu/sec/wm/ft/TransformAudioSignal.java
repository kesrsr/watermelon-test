package ntu.sec.wm.ft;

import java.awt.Color;

import com.badlogic.audio.analysis.FFT;
import com.badlogic.audio.visualization.Plot;

public class TransformAudioSignal {
	float[] time_domain;

	public TransformAudioSignal(float[] origin) {
		time_domain = new float[origin.length];
		System.arraycopy(origin, 0, time_domain, 0, time_domain.length);
	}

	public void run() {
		final float frequency = 100000; // Note A		
		float increment = (float)(2*Math.PI) * frequency / 44100;		
		float angle = 0;		
		float samples[] = new float[1024];
		FFT fft = new FFT( 1024, 44100 );
		
		for( int i = 0; i < samples.length; i++ )
		{
			samples[i] = (float)Math.sin( angle );
			angle += increment;		
		}
		
		fft.forward( samples );
		
		Plot plot = new Plot( "Note A Spectrum", 512, 512);
		plot.plot(fft.getSpectrum(), 1, Color.red );
	}
}

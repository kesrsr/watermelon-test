package sec.fcl;

import sec.fcl.accelerometer.AccelerationRecorder;
import sec.fcl.audio.AudioClassify;
import sec.fcl.audio.AudioRecorder;
import sec.fcl.plot.PlotSamples;
import sec.fcl.train.RecordSampleActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.androidplot.xy.XYPlot;

public class WMTestActivity extends Activity {
	Button start;
	Button stop;
	AudioRecorder audioRecorder;
	AccelerationRecorder accelerationRecorder;
	String audio_file = null;

	Float[] audio_x;
	Float[] audio_y;
	int size = 1000;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

//		XYPlot plot = (XYPlot) findViewById (R.id.audio_plot);
//		plot.setDomainLabel("Sample");
//		plot.setTitle("");
//		plot.setRangeLabel("Value");
//		plot.disableAllMarkup();
		
		audio_x = new Float[size];
		audio_y = new Float[size];	
		
		for(int i = 0; i < size; i++){
			audio_x[i] = (float)i;
			audio_y[i] = 0f;
		}
		
//		PlotSamples audio_sample = new PlotSamples(plot, audio_x, audio_y, "Audio Signal");
//		audio_sample.plot(Color.GREEN, Color.RED);
//		
//		new Thread(audio_sample).start();
		
		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);

		start.setEnabled(true);
		stop.setEnabled(false);

		audioRecorder = new AudioRecorder();
		accelerationRecorder = new AccelerationRecorder(
				this.getApplicationContext());

		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start.setEnabled(false);
				stop.setEnabled(true);
				audioRecorder.startRecord(audio_y);
			}
		});

		stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start.setEnabled(true);
				stop.setEnabled(false);
				audio_file = audioRecorder.stopRecord();
				Intent intent = new Intent(WMTestActivity.this, RecordSampleActivity.class);
				intent.putExtra("audiofilename", audioRecorder.getRcordFileName());
				startActivity(intent);
				
				AudioClassify classification = new AudioClassify(audio_file);
				classification.run();
			}
		});
		

	}
}
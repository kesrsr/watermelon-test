package sec.fcl;

import sec.fcl.accelerometer.AccelerationRecorder;
import sec.fcl.audio.AudioRecorder;
import cn.wh.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WMTestActivity extends Activity {
	Button start;
	Button stop;
	AudioRecorder recorder;
	AccelerationRecorder accelerationRecorder;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);

		start.setEnabled(true);
		stop.setEnabled(false);

		recorder = new AudioRecorder();
		accelerationRecorder = new AccelerationRecorder(
				this.getApplicationContext());

		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start.setEnabled(false);
				stop.setEnabled(true);
				recorder.startRecord();
				accelerationRecorder.init();
				accelerationRecorder.startRecord();
			}
		});

		stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start.setEnabled(true);
				stop.setEnabled(false);
				recorder.stopRecord();
				accelerationRecorder.stopRecord();
			}
		});

	}
}
package sec.fcl;

import sec.fcl.accelerometer.AccelerationRecorder;
import sec.fcl.audio.AudioRecorder;
import sec.fcl.train.RecordSampleActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.wh.R;

public class WMTestActivity extends Activity {
	Button start;
	Button stop;
	AudioRecorder audioRecorder;
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

		audioRecorder = new AudioRecorder();
		accelerationRecorder = new AccelerationRecorder(
				this.getApplicationContext());

		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start.setEnabled(false);
				stop.setEnabled(true);
				audioRecorder.startRecord();
				accelerationRecorder.init();
				accelerationRecorder.startRecord();
			}
		});

		stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start.setEnabled(true);
				stop.setEnabled(false);
				audioRecorder.stopRecord();
				accelerationRecorder.stopRecord();
				Intent intent = new Intent(WMTestActivity.this, RecordSampleActivity.class);
				intent.putExtra("audiofilename", audioRecorder.getRcordFileName());
				intent.putExtra("acclfilename", accelerationRecorder.getAcclFileName());
				startActivity(intent);
			}
		});

	}
}
package sec.fcl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import sec.fcl.accelerometer.AccelerationRecorder;
import sec.fcl.audio.AudioClassify;
import sec.fcl.audio.AudioRecorder;
import sec.fcl.train.RecordSampleActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

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

		// XYPlot plot = (XYPlot) findViewById (R.id.audio_plot);
		// plot.setDomainLabel("Sample");
		// plot.setTitle("");
		// plot.setRangeLabel("Value");
		// plot.disableAllMarkup();

		convertModel();

		audio_x = new Float[size];
		audio_y = new Float[size];

		for (int i = 0; i < size; i++) {
			audio_x[i] = (float) i;
			audio_y[i] = 0f;
		}

		// PlotSamples audio_sample = new PlotSamples(plot, audio_x, audio_y,
		// "Audio Signal");
		// audio_sample.plot(Color.GREEN, Color.RED);
		//
		// new Thread(audio_sample).start();

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
				Intent intent = new Intent(WMTestActivity.this,
						RecordSampleActivity.class);
				intent.putExtra("audiofilename",
						audioRecorder.getRcordFileName());

				AudioClassify classification = new AudioClassify(audio_file);
				int result = classification.run();

				if (result != -1){
					intent.putExtra("classify_result",
							result+"");
					startActivity(intent);
				}
				else 
					showAlert();
					
			}
		});

	}

	private void convertModel() {
		try {
			File file = new File("/sdcard/WMTest/model");
			File dir = new File("/sdcard/WMTest/");
			if (!dir.exists())
				dir.mkdir();

			if (file.exists()) {
				Log.e("WM", "File Exsit");
				return;
			}

			AssetManager am = WMTestActivity.this.getAssets();
			InputStream is = am.open("model");

			OutputStream os = new FileOutputStream(file);
			int read = 0;
			byte[] bytes = new byte[1024];

			while ((read = is.read(bytes)) != -1) {
				os.write(bytes, 0, read);
			}

			is.close();
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void showAlert(){
		final AlertDialog ad = new AlertDialog.Builder(WMTestActivity.this).create();
		ad.setTitle("Sorry");
		ad.setMessage("No Chumping Detected!");
		ad.setButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ad.cancel();
			}
		});
		ad.show();
	}
}
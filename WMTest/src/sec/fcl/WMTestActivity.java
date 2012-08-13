package sec.fcl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import sec.fcl.accelerometer.AccelerationRecorder;
import sec.fcl.audio.AudioClassify;
import sec.fcl.audio.AudioRecorder;
import sec.fcl.train.RecordSampleActivity;
import sec.fcl.view.DrawView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class WMTestActivity extends Activity {
	Button start;
	Button stop;
	AudioRecorder audioRecorder;
	AccelerationRecorder accelerationRecorder;
	String audio_file = null;

	Vector<Float> audio_samples;
	int display_size = 100;
	DrawView dv;
	private MyViewUpdater view_updater;

	// final float noise = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// showTimePassed;
		final TimePass tp = new TimePass();
		new Thread(tp).start();

		convertModel();

		audio_samples = new Vector<Float>();
		for (int i = 0; i < display_size; i++)
			audio_samples.add(0f);

		start = (Button) findViewById(R.id.start);
		stop = (Button) findViewById(R.id.stop);

		start.setEnabled(true);
		stop.setEnabled(false);

		dv = new DrawView(this, audio_samples);

		LinearLayout layout = (LinearLayout) findViewById(R.id.plot_layout);
		layout.addView(dv);

		view_updater = new MyViewUpdater(this, dv);

		audioRecorder = new AudioRecorder(dv);
		audioRecorder.addObserver(view_updater);

		start.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start.setEnabled(false);
				stop.setEnabled(true);
				audioRecorder.startRecord(audio_samples);
			}
		});

		stop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				start.setEnabled(true);
				stop.setEnabled(false);
				audioRecorder.stopRecord();
				audio_file = audioRecorder.saveToLocal();

				Intent intent = new Intent(WMTestActivity.this,
						RecordSampleActivity.class);
				intent.putExtra("audiofilename",
						audioRecorder.getRcordFileName());

				float noise = tp.get_env_noise();
				Log.e("WM", noise + " noise");

				AudioClassify classification = new AudioClassify(audio_file,
						noise);
				int result = classification.run();

				if (result != -1) {
					intent.putExtra("classify_result", result + "");
					startActivity(intent);
				} else
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

	private void showAlert() {
		final AlertDialog ad = new AlertDialog.Builder(WMTestActivity.this)
				.create();
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

	public void onRestart() {
		Intent i = getBaseContext().getPackageManager()
				.getLaunchIntentForPackage(getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
	}

	class TimePass implements Runnable {
		ProgressDialog progressDialog;
		AudioRecorder recorder;

		public TimePass() {
			progressDialog = new ProgressDialog(WMTestActivity.this);
			progressDialog
					.setMessage("Please wait 5 seconds while calculating environment noise...");
			progressDialog.setCancelable(false);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progressDialog.setProgress(0); // set percentage completed to 0%
			progressDialog.setMax(5);
			progressDialog.show();

			recorder = new AudioRecorder();
			recorder.startRecord(null);
		}

		public void run() {
			int progress = 0;
			while (progress < 5) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				progress++;
				progressDialog.setProgress(progress);
			}

			progressDialog.dismiss();
			recorder.stopRecord();
		}

		public float get_env_noise() {
			recorder.saveToLocal();

			return recorder.getMean();
		}
	}

	public class MyViewUpdater implements Observer {
		View view;
		Activity act;

		public MyViewUpdater(Activity act, View view) {
			this.view = view;
			this.act = act;
		}

		@Override
		public void update(Observable observable, Object data) {
			act.runOnUiThread(new Runnable() {
				public void run() {
					view.invalidate();
				}
			});
		}
	}
}
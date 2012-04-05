package sec.fcl.accelerometer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;

public class AccelerationRecorder implements SensorEventListener {
	// private final static String tag = "AccelerationRecorder";
	private float[] acceleration;

	private final String FILE_EXTEN = ".acc";
	private final String FILE_FOLDER = "WMTest";
	private SensorManager sensor_manager = null;
	private Sensor accelerosensor = null;

	private FileOutputStream os = null;
	private boolean isRecord = false;
	private Thread recordThread = null;

	private int frequency = 100; // 1 sec record 10 times
	private String file_name = null;

	public AccelerationRecorder(Context mContext) {
		acceleration = new float[3];
		sensor_manager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
	}

	public void init() {
		accelerosensor = sensor_manager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		sensor_manager.registerListener(this, accelerosensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	public void startRecord() {
		file_name = getFileName();
		try {
			os = new FileOutputStream(file_name);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		isRecord = true;

		recordThread = new Thread(new Runnable() {
			@Override
			public void run() {
				record();
			}
		});

		recordThread.start();
	}

	private void record() {
		if (os != null) {
			while (isRecord) {
				float sum = (float) Math.sqrt(Math.pow(acceleration[0], 2)
						+ Math.pow(acceleration[1], 2)
						+ Math.pow(acceleration[2], 2));
				String string_sum = sum + "\n";
				try {
					os.write(string_sum.getBytes());
					Thread.sleep(1000 / frequency);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopRecord() {
		if (recordThread != null) {
			isRecord = false;
			recordThread = null;
		}
		if (os != null)
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		sensor_manager.unregisterListener(this);
	}
	
	public String getAcclFileName(){
		return this.file_name;		
	}

	private String getFileName() {
		Calendar cal = Calendar.getInstance();
		String time = cal.get(Calendar.YEAR) + "_"
				+ (cal.get(Calendar.MONTH) + 1) + "_" + cal.get(Calendar.DATE)
				+ "_" + cal.get(Calendar.HOUR_OF_DAY) + "_"
				+ cal.get(Calendar.MINUTE) + "_" + cal.get(Calendar.SECOND);

		String file_path = Environment.getExternalStorageDirectory().getPath();
		File file_folder = new File(file_path, FILE_FOLDER);

		if (!file_folder.exists())
			file_folder.mkdirs();

		File file = new File(file_folder, time + FILE_EXTEN);

		if (file.exists())
			file.delete();

		return file_folder.getAbsolutePath() + "/" + time + FILE_EXTEN;
	}

	public void onSensorChanged(SensorEvent event) {
		acceleration[0] = event.values[0];
		acceleration[1] = event.values[1];
		acceleration[2] = event.values[2];
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}
}

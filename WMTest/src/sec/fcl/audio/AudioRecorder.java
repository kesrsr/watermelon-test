package sec.fcl.audio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import sec.fcl.R;
import sec.fcl.audio.io.EndianDataInputStream;
import sec.fcl.view.DrawView;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

@SuppressWarnings("unused")
public class AudioRecorder {
	class MyObservable extends Observable {
		@Override
		public void notifyObservers() {
			setChanged();
			super.notifyObservers();
		}
	}

	private MyObservable notifier;
	{
		notifier = new MyObservable();
	}

	private final String AUDIO_FILE_FORMAT = ".wav";
	private final String FILE_FOLDER = "WMTest";
	private final String TEMP_FILE = "record_temp";

	private final int SAMPLE_RATE = 44100;
	private final int RECORD_BPP = 16;
	private final int RECORD_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
	private final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	int bufferSize = 0;
	boolean isRecording = false;
	private Thread recordThread = null;
	AudioRecord recorder = null;
	private String fileName = null;

	/** inverse max short value as float **/
	private final float MAX_VALUE = 1.0f / Short.MAX_VALUE;
	Vector<Float> means;

	DrawView dv = null;
	int divide = 20;

	public AudioRecorder() {
		bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, RECORD_CHANNEL,
				AUDIO_ENCODING);

		means = new Vector<Float>();
		Log.e("AudioRecorder", "buffer " + bufferSize);// buffer 8192
	}

	public AudioRecorder(DrawView dv) {
		bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, RECORD_CHANNEL,
				AUDIO_ENCODING);

		means = new Vector<Float>();
		this.dv = dv;

		Log.e("AudioRecorder", "buffer " + bufferSize);// buffer 8192
	}

	public void startRecord(final Vector<Float> samples) {
		if (bufferSize == 0)
			bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
					RECORD_CHANNEL, AUDIO_ENCODING);

		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
				RECORD_CHANNEL, AUDIO_ENCODING, bufferSize);

		isRecording = true;
		recorder.startRecording();

		recordThread = new Thread(new Runnable() {

			@Override
			public void run() {
				String filename = getFilename(TEMP_FILE, AUDIO_FILE_FORMAT);

				writeToFile(filename, samples);
			}
		}, "AudioRecorder");

		recordThread.start();
	}

	private String getFilename(String filename, String exten) {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file_folder = new File(filepath, FILE_FOLDER);

		if (!file_folder.exists())
			file_folder.mkdirs();

		File file = new File(file_folder, filename);

		if (file.exists())
			file.delete();

		return (file_folder.getAbsolutePath() + "/" + filename + exten);
	}

	private void writeToFile(String filename, Vector<Float> values) {
		byte data[] = new byte[bufferSize];
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int read = 0;
		int mean_add = 0;

		if (os != null) {
			while (isRecording) {
				read = recorder.read(data, 0, bufferSize);

				Vector<Float> samples = read_samples(data);
				means.add(root_mean_sqr(samples));

				for (int i = 0; i < samples.size() / divide; i++){
					List<Float> sub = samples.subList(i * divide, (i+1)*divide - 1);
					if (values != null && mean_add < values.size()) {
						values.set(mean_add, mean(sub));
						mean_add++;

						notifier.notifyObservers();
					} else if (values != null) {
						values.remove(0);
						values.add(mean(sub));

						notifier.notifyObservers();
					}
				}
				if (AudioRecord.ERROR_INVALID_OPERATION != read) {
					try {
						os.write(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Log.e("z", filename);
		}

	}

	public void stopRecord() {
		if (recorder != null) {
			isRecording = false;

			recorder.stop();
			recorder.release();

			recorder = null;
			recordThread = null;
		}
	}

	public String saveToLocal() {
		fileName = getFilename(getTime(), AUDIO_FILE_FORMAT);
		copyWaveFile(getFilename(TEMP_FILE, AUDIO_FILE_FORMAT), fileName);
		deleteTempFile();

		return fileName;
	}

	public float getMean() {
		float sum = 0;
		for (int i = 0; i < means.size(); i++)
			sum += means.get(i);

		Log.e("WM", "mean " + sum / means.size());

		return sum / means.size();
	}

	public String getRcordFileName() {
		return fileName;
	}

	private String getTime() {
		Calendar cal = Calendar.getInstance();
		String time = cal.get(Calendar.YEAR) + "_"
				+ (cal.get(Calendar.MONTH) + 1) + "_" + cal.get(Calendar.DATE)
				+ "_" + cal.get(Calendar.HOUR_OF_DAY) + "_"
				+ cal.get(Calendar.MINUTE) + "_" + cal.get(Calendar.SECOND);

		return time;
	}

	private void deleteTempFile() {
		File file = new File(getFilename(TEMP_FILE, AUDIO_FILE_FORMAT));

		file.delete();
	}

	private void copyWaveFile(String inFilename, String outFilename) {
		FileInputStream in = null;
		FileOutputStream out = null;

		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = SAMPLE_RATE;
		int channels = 2;
		long byteRate = RECORD_BPP * SAMPLE_RATE * channels / 8;

		byte[] data = new byte[bufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while (in.read(data) != -1) {
				out.write(data);
			}

			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels, long byteRate)
			throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R'; // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f'; // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16; // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1; // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8); // block align
		header[33] = 0;
		header[34] = RECORD_BPP; // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}

	private Vector<Float> read_samples(byte[] data) {
		int readSamples = 0;
		EndianDataInputStream in = new EndianDataInputStream(
				new ByteArrayInputStream(data));
		Vector<Float> samples = new Vector<Float>();

		for (int i = 0; i < data.length / RECORD_CHANNEL; i++) {
			float sample = 0;
			try {
				for (int j = 0; j < RECORD_CHANNEL; j++) {
					int shortValue = in.readShortLittleEndian();
					sample += (shortValue * MAX_VALUE);
				}
				sample /= RECORD_CHANNEL;
				samples.add(sample);
				readSamples++;
			} catch (Exception ex) {
				break;
			}
		}

		// Log.e("WM", "Mean "+mean(samples));
		return samples;
	}
	
	private float mean(float[] input) {
		float sum = 0;
		for (int i = 0; i < input.length; i++)
			sum += input[i];

		return sum / input.length;
	}
	
	private float mean(List<Float> input) {
		float sum = 0;
		for (int i = 0; i < input.size(); i++)
			sum += input.get(i);

		return sum / input.size();
	}
	
	private float mean(Vector<Float> input) {
		float sum = 0;
		for (int i = 0; i < input.size(); i++)
			sum += input.get(i);

		return sum / input.size();
	}
	
	private float mean_sq(Vector<Float> input) {
		float sum = 0;
		for (int i = 0; i < input.size(); i++)
			sum += input.get(i) * input.get(i);

		return sum / input.size();
	}

	private float root_mean_sqr(Vector<Float> input) {
		return (float) Math.sqrt(mean_sq(input));
	}

	public void addObserver(Observer observer) {
		notifier.addObserver(observer);
	}

	public void removeObserver(Observer observer) {
		notifier.deleteObserver(observer);
	}
}

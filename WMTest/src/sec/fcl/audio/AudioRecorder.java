package sec.fcl.audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class AudioRecorder {
	private final String AUDIO_FILE_FORMAT = ".wav";
	private final String RAW_FILE_FORMAT = ".raw";
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
	
	private final int bytesPerSample = 2;
	private final double twopower15 = 32768.0;

	public AudioRecorder() {
		bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, RECORD_CHANNEL,
				AUDIO_ENCODING);
		Log.e("AudioRecorder", "buffer " + bufferSize);// buffer 8192
	}

	public void startRecord() {
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
				writeToFile();
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

	private void writeToFile() {
		byte data[] = new byte[bufferSize];
		String filename = getFilename(TEMP_FILE, AUDIO_FILE_FORMAT);
		String filename2 = getFilename(getTime(), RAW_FILE_FORMAT);
		FileOutputStream os = null;
		FileOutputStream doubleos = null;

		try {
			os = new FileOutputStream(filename);
			doubleos = new FileOutputStream(filename2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int read = 0;

		if (os != null) {
			while (isRecording) {
				read = recorder.read(data, 0, bufferSize);

				if (AudioRecord.ERROR_INVALID_OPERATION != read) {
					try {
						os.write(data);
						double[] rawDoubleData = getRawDoubleData(data);
						for (int i = 0; i < rawDoubleData.length; i++) {
							doubleos.write(String.valueOf(rawDoubleData[i])
									.getBytes());
							doubleos.write(String.valueOf("\n").getBytes());
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				os.close();
				doubleos.close();
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

		copyWaveFile(getFilename(TEMP_FILE, AUDIO_FILE_FORMAT),
				getFilename(getTime(), AUDIO_FILE_FORMAT));
		deleteTempFile();
	}

	private String getTime() {
		Calendar cal = Calendar.getInstance();
		String time = cal.get(Calendar.YEAR) + "_"
				+ (cal.get(Calendar.MONTH) + 1) + "_" + cal.get(Calendar.DATE)
				+ "_" + cal.get(Calendar.HOUR_OF_DAY) + "_"
				+ cal.get(Calendar.MINUTE) + "_" + cal.get(Calendar.SECOND);

		return time;
	}


	
	private  double[] getRawDoubleData(byte[] rawByteData) {
		int bytesRecorded = rawByteData.length;
		if(bytesRecorded % 2 != 0)
			throw new RuntimeException("N is not a power of 2");
			
		double[] rawDoubleData = new double[bytesRecorded / 2];
		for (int i = 0, floatIndex = 0; i < bytesRecorded - bytesPerSample + 1; i += bytesPerSample, floatIndex++) {
			double sample = 0;
			for (int b = 0; b < bytesPerSample; b++) {
				byte v = rawByteData[i + b];
				if (b < bytesPerSample - 1) {
					v &= 0xFF;
				}
				sample += v << (b * 8);
			}
			//scale to -1 to 1;
			double sample32 = (sample / twopower15);
			rawDoubleData[floatIndex] = sample32;
		}
		return rawDoubleData;
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
}

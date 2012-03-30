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
	private final String FILE_FORMAT = ".wav";
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

	public AudioRecorder() {
		bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, RECORD_CHANNEL,
				AUDIO_ENCODING);
		Log.e("Audio1", "buffer " + bufferSize);
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

	private String getFilename(String filename) {
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file_folder = new File(filepath, FILE_FOLDER);

		if (!file_folder.exists())
			file_folder.mkdirs();

		File file = new File(file_folder, filename);

		if (file.exists())
			file.delete();

		return (file_folder.getAbsolutePath() + "/" + filename + FILE_FORMAT);
	}

	private void writeToFile() {
		byte data[] = new byte[bufferSize];
		String filename = getFilename(TEMP_FILE);
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename);
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

		Calendar cal = Calendar.getInstance();
		String time = cal.get(Calendar.YEAR) + "_" + (cal.get(Calendar.MONTH)+1) + "_"
				+ cal.get(Calendar.DATE) + "_" + cal.get(Calendar.HOUR_OF_DAY) + "_"
				+ cal.get(Calendar.MINUTE) + "_" + cal.get(Calendar.SECOND);
		
		copyWaveFile(getFilename(TEMP_FILE), getFilename(time));
		deleteTempFile();
	}

	private void deleteTempFile() {
		File file = new File(getFilename(TEMP_FILE));

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

			Log.e("AudioRecorder", "File size: " + totalDataLen);

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

package sec.fcl.train;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.wh.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class RecordSampleActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sample);

		Button send_email = (Button) findViewById(R.id.email);
		Button save_local = (Button) findViewById(R.id.save);

		final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.type);

		send_email.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<String> files = addTypeToFileName(radioGroup);
				email(RecordSampleActivity.this,"huang@arch.ethz.ch", "zeng@arch.ethz.ch",
						"watermelon training data", "Thanks for sending us data", files);
			}
		});

		save_local.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addTypeToFileName(radioGroup);
			}
		});

	}

	public ArrayList<String> addTypeToFileName(RadioGroup radioGroup) {
		// get file names passed in
		Bundle extras = getIntent().getExtras();
		String audioFileName = extras.getString("audiofilename");
		String acclFileName = extras.getString("acclfilename");

		// get the type of watemelon: good or bad
		int selectedRadio = radioGroup.getCheckedRadioButtonId();
		//if nothing selected
		if(selectedRadio == -1){
			Toast.makeText(RecordSampleActivity.this,
				"Please make a selection", Toast.LENGTH_SHORT).show();
			return null;
			}
		
		RadioButton selectedRadioButton = (RadioButton) findViewById(selectedRadio);

		File audioFile = new File(audioFileName);
		File newAudioFile = new File(audioFile.getParent(),
				selectedRadioButton.getText() + "_" + audioFile.getName());
		audioFile.renameTo(newAudioFile);

		File acclFile = new File(acclFileName);
		File newAcclFile = new File(acclFile.getParent(),
				selectedRadioButton.getText() + "_" + acclFile.getName());
		acclFile.renameTo(newAcclFile);

		ArrayList<String> files = new ArrayList<String>();
		files.add(newAudioFile.getAbsolutePath());
		files.add(newAcclFile.getAbsolutePath());

		return files;
	}

	public void email(Context context, String emailTo, String emailCC,
			String subject, String emailText, List<String> filePaths) {
		// need to "send multiple" to get more than one attachment
		final Intent emailIntent = new Intent(
				android.content.Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("text/plain");
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
				new String[] { emailTo });
		emailIntent.putExtra(android.content.Intent.EXTRA_CC,
				new String[] { emailCC });
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, emailText);

		// has to be an ArrayList
		ArrayList<Uri> uris = new ArrayList<Uri>();
		// convert from paths to Android friendly Parcelable Uri's
		if (filePaths != null) {
			for (String file : filePaths) {
				File fileIn = new File(file);
				Uri u = Uri.fromFile(fileIn);
				uris.add(u);
			}
			emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		}
		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}
}

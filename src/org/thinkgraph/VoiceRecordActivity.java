package org.thinkgraph;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VoiceRecordActivity extends Activity {
    private static final int REQUEST_CODE = 1234;
    public static final String APP_ROOT = "ThinkGraph";
    private ListView wordsList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_layout);

        // Button startRec = (Button) findViewById(R.id.startRec);
        // Button stopRec = (Button) findViewById(R.id.stopRec);
        // Button playRec = (Button) findViewById(R.id.playRec);
        // final MediaRecorder recorder = new MediaRecorder();
        // final MediaPlayer player = new MediaPlayer();

        // startRec.setOnClickListener(new View.OnClickListener() {
        //
        // public void onClick(View v) {
        // recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        // recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // recorder.setOutputFile(sanitizePath("hello_world.wav"));
        // try {
        // recorder.prepare();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // recorder.start();
        // }
        // });
        //
        // stopRec.setOnClickListener(new View.OnClickListener() {
        //
        // public void onClick(View v) {
        // recorder.stop();
        // }
        // });
        //
        // playRec.setOnClickListener(new View.OnClickListener() {
        //
        // public void onClick(View v) {
        // try {
        // player.setDataSource(sanitizePath("hello_world.wav"));
        // player.prepare();
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // player.start();
        // }
        // });

        Button speakButton = (Button) findViewById(R.id.speakButton);
        wordsList = (ListView) findViewById(R.id.list); // Disable button if no
        // recognition service
        // is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0) {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
    }

    // private String sanitizePath(String path) {
    // if (!path.startsWith("/")) {
    // path = "/" + path;
    // }
    // if (!path.contains(".")) {
    // path += ".3gp";
    // }
    // return Environment.getExternalStorageDirectory().getAbsolutePath()
    // + path;
    // }

    public void speakButtonClicked(View v) {
        startVoiceRecognitionActivity();
    }

    private void startVoiceRecognitionActivity() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        /*
          You can set the language as below or rely on the phone settings (recommended if no multi-language support in the application code

          intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");
          */
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Populate the wordsList with the String values the recognition
            // engine thought it heard
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            saveRecord(matches);
            wordsList.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, matches));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveRecord(ArrayList<String> matches) {

        if (matches == null || matches.size() == 0) {
            return;
        }
        FileWriter writer = null;
        try {
            String speechContent = matches.get(0);

            File root = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + APP_ROOT,
                    speechContent.substring(0, speechContent.indexOf(" ")));

            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, System.currentTimeMillis() + ".txt");
            writer = new FileWriter(gpxfile);
            writer.append(speechContent);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
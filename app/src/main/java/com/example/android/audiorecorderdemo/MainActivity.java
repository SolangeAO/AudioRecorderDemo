package com.example.android.audiorecorderdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private RecordAudio record;

    Button startRecordingButton, stopRecordingButton;
    TextView statusText;

    File recordingFile;

    WavRecorder wavRecorder;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = (TextView) this.findViewById(R.id.StatusTextView);
        startRecordingButton = (Button) this .findViewById(R.id.StartRecordingButton);
        stopRecordingButton = (Button) this .findViewById(R.id.StopRecordingButton);

        startRecordingButton.setOnClickListener(recordOnClickListener);
        stopRecordingButton.setOnClickListener(stopOnClickListener);

        stopRecordingButton.setEnabled(false);

        checkRecordPermission();

        /*

        File path = new File(getFilesDir().getAbsolutePath()+"/WavRecorder/");
        path.mkdirs();
        try {

            recordingFile = File.createTempFile("record_temp", ".raw", path);
        } catch (IOException e) {

            throw new RuntimeException("Couldn't create file on SD card", e);

        }
        */
    }

    /**
     * OnClickListener para el botón de record.
     */
    private View.OnClickListener recordOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            record();
        }
    };

    /**
     * OnClickListener para el botón de stop.
     */
    private View.OnClickListener stopOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            stopRecording();
        }
    };

    public void record() {

        String outpath = getFilesDir().getAbsolutePath()+"/WavRecorder/path_to_file8.wav";
        String inpath = getFilesDir().getAbsolutePath()+"/WavRecorder/record_temp.raw";

        wavRecorder = new WavRecorder(outpath, inpath);
        wavRecorder.startRecording();

        handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                wavRecorder.stopRecording();

            }
        };
        handler.postDelayed(r, 5000);

        /*
        startRecordingButton.setEnabled(false);
        stopRecordingButton.setEnabled(true);

        record = new RecordAudio();
        record.setFichier(recordingFile);
        record.execute();
        */
    }

    public void stopRecording() {

    wavRecorder.stopRecording();

        /*
        if (record.isRecording()) {
            record.setRecording(false);
        }
        */

    }

    private void checkRecordPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    123);
        }
    }
}

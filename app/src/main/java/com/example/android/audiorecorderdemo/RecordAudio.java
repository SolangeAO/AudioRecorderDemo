package com.example.android.audiorecorderdemo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecordAudio extends AsyncTask <Void, Integer, Void> {

    public int frequency = 11025;
    private File fichier;
    private Integer progr = 0;
    private boolean isRecording;
    private int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord audioRecord;

    public File getFichier() {
        return fichier;
    }

    public void setFichier(File fichier) {
        this.fichier = fichier;
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void setRecording(boolean recording) {
        isRecording = recording;
    }


    @Override
    protected Void doInBackground(Void... voids) {
        setRecording(true);

        try {
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(getFichier())));

            int bufferSize = AudioRecord.getMinBufferSize(frequency ,channelConfiguration, audioEncoding);
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
            short [] buffer = new short[bufferSize/2];
            audioRecord.startRecording();

            while (isRecording){
                int bufferReadResult =  audioRecord.read(buffer,0,bufferSize/2);

                for (int i = 0; i < bufferReadResult; i++){
                    dos.writeShort(buffer[i]);
                    //publishProgress(new Integer(r));
                }

                Log.e("info", "record");
                //r++;
            }

            audioRecord.stop();
            audioRecord.release();
            dos.close();

        } catch (FileNotFoundException fnf){
            fnf.printStackTrace();
        } catch (IOException io){
            io.printStackTrace();
        }



        return null;

    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }
}

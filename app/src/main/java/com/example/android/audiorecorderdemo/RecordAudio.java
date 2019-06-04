package com.example.android.audiorecorderdemo;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

    private static final int RECORDER_BPP = 16;

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

        try {
            //DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(getFichier())));

            int bufferSize = AudioRecord.getMinBufferSize(frequency ,channelConfiguration, audioEncoding)*3; //Se le agregó el 3
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);//
            byte [] buffer = new byte [bufferSize]; //

            int state = audioRecord.getState();
            if (state == 1) {
                audioRecord.startRecording();
                setRecording(true);
            }

            FileOutputStream os = new FileOutputStream(getFichier());
            int read = 0;
            if (null != os) {

            while (isRecording) {

                read = audioRecord.read(buffer, 0, bufferSize);
                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    os.write(buffer);
                }
            }

            Log.e("info", "record");
            os.close();

            }

            audioRecord.stop();
            audioRecord.release();
            //dos.close();

            FileInputStream in = null;
            FileOutputStream out = null;
            long totalAudioLen = 0;
            long totalDataLen = totalAudioLen + 36;
            long longSampleRate = frequency;
            int channels = ((channelConfiguration == AudioFormat.CHANNEL_IN_MONO) ? 1
                    : 2);
            long byteRate = RECORDER_BPP * frequency * channels / 8;

            byte[] data = new byte[bufferSize];


                in = new FileInputStream(getFichier().getName());
                out = new FileOutputStream("/data/data/com.example.android.audiorecorderdemo/files/WavRecorder/waver1.wav");
                totalAudioLen = in.getChannel().size();
                totalDataLen = totalAudioLen + 36;

                WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                        longSampleRate, channels, byteRate);

                while (in.read(data) != -1) {
                    out.write(data);
                }

                in.close();
                out.close();

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
        header[32] = (byte) (((channelConfiguration == AudioFormat.CHANNEL_IN_MONO) ? 1
                : 2) * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
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

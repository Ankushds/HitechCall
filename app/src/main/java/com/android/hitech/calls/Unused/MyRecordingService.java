package com.android.hitech.calls.Unused;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class MyRecordingService extends Service{
    MediaRecorder recorder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("MyRecordingService","onCreate");
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //recorder.setAudioEncoder(MediaRecorder.getAudioSourceMax());
        recorder.setAudioEncodingBitRate(16);
        recorder.setAudioSamplingRate(44100);
        File file = new File(Environment.getExternalStorageDirectory()
                .getPath(), "HiTechRecording");
        if (!file.exists()){
            file.mkdir();
        }
        File file1 = new File(file,System.currentTimeMillis()+".mp3");
        recorder.setOutputFile(file1.getAbsolutePath());
        try {
            System.out.println("AnkushKaMBOJJJJ : "+file1.getAbsolutePath());
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            System.out.println("AnkushKaMBOJJJJ : "+e);
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recorder.stop();
        recorder.release();
        Log.i("MyRecordingService","onDestroy");
    }
}

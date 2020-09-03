package com.example.audiorecord;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    Button recButton, stopButton, playButton, stopPlayingRecording;
    String AudioSavePath = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recButton = findViewById(R.id.recButton);
        stopButton = findViewById(R.id.stopButton);
        playButton = findViewById(R.id.playButton);
        stopPlayingRecording = findViewById(R.id.stopPlayingRecording);

        stopButton.setEnabled(false);
        playButton.setEnabled(false);
        stopPlayingRecording.setEnabled(false);
        random = new Random();

        recButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){

                    AudioSavePath = getExternalFilesDir(null)
                            + "/" + CreateRandomAudioFileName(5) + "AudioRecording.3gp";
                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();
                        mediaRecorder.start();
                    } catch (IllegalStateException | IOException e){
                        e.printStackTrace();
                    }

                    recButton.setEnabled(false);
                    stopButton.setEnabled(true);

                    Toast.makeText(MainActivity.this, "Recording started", Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();
                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaRecorder.stop();
                stopButton.setEnabled(false);
                playButton.setEnabled(true);
                recButton.setEnabled(true);
                stopPlayingRecording.setEnabled(false);

                Toast.makeText(MainActivity.this, "Recording Completed",
                        Toast.LENGTH_LONG).show();
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                stopButton.setEnabled(false);
                recButton.setEnabled(false);
                stopPlayingRecording.setEnabled(true);

                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePath);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(MainActivity.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });

        stopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopButton.setEnabled(false);
                recButton.setEnabled(true);
                stopPlayingRecording.setEnabled(false);
                playButton.setEnabled(true);

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });
    }

    public void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePath);
        mediaRecorder.setMaxDuration(10000);

    }

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while ( i < string){
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();

    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{ WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this,"Permission Denied",
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
}
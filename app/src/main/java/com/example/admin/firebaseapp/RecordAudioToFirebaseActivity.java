package com.example.admin.firebaseapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;

public class RecordAudioToFirebaseActivity extends Activity {

    private static final String LOG_TAG ="Recording" ;
    Button sendAudio,start,stop;
    TextView status;
    MediaRecorder mRecorder;
    String mFileName=null;
    StorageReference mStorage;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio_to_firebase);
        Firebase.setAndroidContext(this);
        mStorage= FirebaseStorage.getInstance().getReference();
        sendAudio= (Button) findViewById(R.id.btnSendAudio);
        start= (Button) findViewById(R.id.btnStartRecording);
        stop= (Button) findViewById(R.id.btnStopRecording);
        progressDialog=new ProgressDialog(this);

        status= (TextView) findViewById(R.id.tvAudioRecordingStatus);
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/fileAudio1.3gp";
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
                status.setText("Recording Started..");
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
                status.setText("Recording stopped..");

            }
        });


    }
    private void startRecording() {
        if(mRecorder ==null)
        {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(mFileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {

            Log.e(LOG_TAG, "prepare() failed");
        }


    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        uoploadAudion();

    }

    private void uoploadAudion() {

        progressDialog.setTitle("Uploading Audio.");
        progressDialog.show();
        StorageReference filePath=mStorage.child("Audio").child("fileAudio1.3gp");
        Uri uri=Uri.fromFile(new File(mFileName));
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            progressDialog.dismiss();
             status.setText("Uploading Finished..");


            }
        });

    }
}

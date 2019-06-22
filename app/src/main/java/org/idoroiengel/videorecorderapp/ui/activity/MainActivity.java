package org.idoroiengel.videorecorderapp.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.idoroiengel.videorecorderapp.R;
import org.idoroiengel.videorecorderapp.utilities.managers.IntentManager;

import java.io.FileNotFoundException;


public class MainActivity extends AppCompatActivity {

    // consts
    private static final int REQUEST_WRITE_EXTERNAL_PERMISSION = 3;

    private static final String LOG_TAG = "debug";

    // UI
    private Button mStartVideoButton;
    private EditText mNumberOfSecondsInput;
    private EditText mFrameRateInput;

    private Uri mVideoUri;

    public EditText getmNumberOfSecondsInput() {
        return mNumberOfSecondsInput;
    }

    public EditText getmFrameRateInput() {
        return mFrameRateInput;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindViews();
        initViews();
        mVideoUri = null;
    }

    private void bindViews() {
        mStartVideoButton = findViewById(R.id.activity_main_start_video_button);
        mFrameRateInput = findViewById(R.id.activity_main_frame_rate);
        mNumberOfSecondsInput = findViewById(R.id.activity_main_number_of_seconds);
    }

    private void initViews() {
        mStartVideoButton.setOnClickListener(v -> {
            requestPermissions();

            if(!mNumberOfSecondsInput.getText().toString().isEmpty()
                    && !mFrameRateInput.getText().toString().isEmpty()) {
                IntentManager.dispatchVideoIntent(this);
            }else{
                Toast.makeText(this, getString(R.string.please_insert_parameters_toast_message), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == IntentManager.VIDEO_INTENT_REQUEST_CODE && resultCode == RESULT_OK){
            try {
                Uri videoUri = data.getData();
                Log.d(LOG_TAG, "video uri is: " + videoUri.toString());
                mVideoUri = videoUri;
                getContentResolver().openInputStream(videoUri);
                openVideoSharingChooser(videoUri);
            }catch (FileNotFoundException e){
                Log.d(LOG_TAG, e.getMessage());
            }catch (NullPointerException e){
                Toast.makeText(this, "there was a problem, please try again.", Toast.LENGTH_SHORT).show();
            }
        }
        // TODO check why the intent defaults to result canceled
        if(requestCode == IntentManager.SHARE_INTENT_REQUEST_CODE){
            deleteSharedVideo(mVideoUri);
        }
    }

    private void deleteSharedVideo(Uri fileToDelete) {
        getContentResolver().delete(fileToDelete, null, null);
    }

    private void requestPermissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    REQUEST_WRITE_EXTERNAL_PERMISSION);
        }
    }

    private void openVideoSharingChooser(Uri videoUri) {
        IntentManager.buildShareIntent(this, videoUri);
    }
}
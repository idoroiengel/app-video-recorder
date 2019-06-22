package org.idoroiengel.videorecorderapp.utilities.managers;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;

import org.idoroiengel.videorecorderapp.ui.activity.MainActivity;

public class IntentManager {

    public static final int VIDEO_INTENT_REQUEST_CODE = 1;
    public static final int SHARE_INTENT_REQUEST_CODE = 2;

    private static final String SHARE_VIDEO_TYPE = "video/mp4";
    private static final String SHARE_VIDEO_CHOOSE_TITLE = "share your video";

    private IntentManager(){

    }

    public static IntentManager instance = new IntentManager();

    public static IntentManager getInstance() {
        return instance;
    }

    public static void dispatchVideoIntent(AppCompatActivity appCompatActivity){
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        /*
            We're allowing the user to define parameters for the video. Although these parameters are probably not the best
            way to allow the user to control the video parameters (frame rate namely), this is designed to comply to the
            exercise's requirements and to demonstrate one method these parameters can be changed.
         */
        videoIntent.putExtra(
                MediaStore.EXTRA_DURATION_LIMIT,
                Integer.valueOf(
                        ((MainActivity)appCompatActivity).getmNumberOfSecondsInput().getText().toString()
                )
        );

        videoIntent.putExtra(
                MediaStore.EXTRA_VIDEO_QUALITY,
                Integer.valueOf(
                        ((MainActivity)appCompatActivity).getmFrameRateInput().getText().toString()
                )
        );



        if(videoIntent.resolveActivity(appCompatActivity.getPackageManager()) != null){
            appCompatActivity.startActivityForResult(videoIntent, VIDEO_INTENT_REQUEST_CODE);
        }
    }

    public static void buildShareIntent(AppCompatActivity appCompatActivity, Uri shareUri){

        ShareCompat.IntentBuilder intentBuilder =
                ShareCompat.IntentBuilder
                        .from(appCompatActivity)
                        .setType(SHARE_VIDEO_TYPE)
                        .setStream(shareUri)
                        .setChooserTitle(SHARE_VIDEO_CHOOSE_TITLE);

        Intent shareIntent = new Intent(intentBuilder.getIntent());
        shareIntent.setAction(Intent.ACTION_SEND)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        appCompatActivity.startActivityForResult(shareIntent, SHARE_INTENT_REQUEST_CODE);
    }
}
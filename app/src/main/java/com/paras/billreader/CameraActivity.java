package com.paras.billreader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity {

    CameraSource mCameraSource;
    final String TAG = "CameraActivity";
    SurfaceView mCameraView;
    final String Digits = "[0-9]{1,6}(\\.[0-9]*)?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mCameraView = findViewById(R.id.surfaceView);

        startCameraSource();
    }

    private void startCameraSource() {

        //Create the TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies not loaded yet");
        } else {

            //Initialize camerasource to use high resolution and set Autofocus on.
            mCameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            /**
             * Add call back to SurfaceView and check if camera permission is granted.
             * If permission is granted we can start our cameraSource and pass it to surfaceView
             */
            mCameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {


                        mCameraSource.start(mCameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                /**
                 * Release resources for cameraSource
                 */
                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    mCameraSource.stop();
                }
            });

            //Set the TextRecognizer's Processor.
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * Detect all the text from camera using TextBlock and the values into a stringBuilder
                 * which will then be set to the textView.
                 * */
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {

                    final SparseArray<TextBlock> items = detections.getDetectedItems();

                    if (items.size() != 0 ){

                                StringBuilder stringBuilder = new StringBuilder();
                                String text;
                                for(int i=0;i<items.size();i++) {

                                    try {
                                        TextBlock item = items.valueAt(i);
                                        stringBuilder.append(item.getValue());
                                        stringBuilder.append("\n");
                                        text = item.getValue().toString().toLowerCase();


                                        if (text.contains("total") || text.contains("bill") ||
                                                text.contains("pay") || text.contains("gross") ||
                                                text.contains("net") || text.contains("amount") ||
                                                text.contains("payable") ||  text.contains("bill total")){

                                            if (!items.valueAt(i + 1).getValue().isEmpty()) {

                                                if(items.valueAt(i+1).getValue().toString().matches(Digits)) {

                                                    Intent j = new Intent(CameraActivity.this,ResultActivity.class);
                                                    j.putExtra("amount",items.valueAt(i+1).getValue().toString());
                                                    startActivity(j);
                                                    finish();
                                                }


                                            }
                                        }
                                    }catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }

                    }
                }
            });
        }
    }


}

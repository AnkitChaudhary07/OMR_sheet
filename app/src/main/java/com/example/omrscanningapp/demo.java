package com.example.omrscanningapp;

import static com.example.omrscanningapp.R.id.camera_view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class demo extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    final int CAMERA_PERMISSION_REQUEST = 100;
    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean mCameraPermissionGranted = true;
    private Mat mRgba;
    private Mat mGray;
    private Button mScanButton, cameraButton;
    private ImageView mScannedImageView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    mOpenCvCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

       mOpenCvCameraView = findViewById(camera_view);
        //mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        // Register the CvCameraViewListener2 callback
        mOpenCvCameraView.setCvCameraViewListener(this);

        cameraButton = findViewById(R.id.cameraButton);
        mScanButton = findViewById(R.id.btn_scan);
        mScannedImageView = findViewById(R.id.img_scanned);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent icamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(icamera,CAMERA_PERMISSION_REQUEST);
            }
        });
        // Calling the requestCameraPermission() method here
        requestCameraPermission();
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Scanning", "mGray: " + mGray);
                if (mGray != null) {
                    Mat threshold = new Mat();
                    Imgproc.threshold(mGray, threshold, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
                    Bitmap bitmap = Bitmap.createBitmap(threshold.cols(), threshold.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(threshold, bitmap);
                    mScannedImageView.setImageBitmap(bitmap);
                }
                else
                    Toast.makeText(demo.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == CAMERA_PERMISSION_REQUEST) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                mScannedImageView.setImageBitmap(bitmap);
            }
        }
    }


    /*Request the camera permission at runtime in your activity or fragment.*/
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            mCameraPermissionGranted = true;
            initializeOpenCV();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mCameraPermissionGranted = true;
                initializeOpenCV();
            } else {
                Toast.makeText(demo.this, "Camera permission denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void initializeOpenCV() {
        if (OpenCVLoader.initDebug()) {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
        mGray.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.GaussianBlur(mGray, mGray, new Size(5, 5), 0);

        return mRgba;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraPermissionGranted) {
            if (!OpenCVLoader.initDebug()) {
                OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
            } else {
                mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }
}

package com.example.dongja94.samplecamera;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    Camera mCamera;
    SurfaceHolder mHolder;
    Gallery gallery;
    ImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        gallery = (Gallery)findViewById(R.id.gallery);
        mAdapter = new ImageAdapter();
        gallery.setAdapter(mAdapter);

        SurfaceView view = (SurfaceView)findViewById(R.id.surfaceView);
        view.getHolder().addCallback(this);
        view.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        Button btn = (Button)findViewById(R.id.btn_change);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeCamera();
            }
        });

        btn = (Button)findViewById(R.id.btn_effect);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEffect();
            }
        });

        btn = (Button)findViewById(R.id.btn_picture);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });
    }

    Camera.ShutterCallback shutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    Camera.PictureCallback raw = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };

    Camera.PictureCallback jpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 4;
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
            mAdapter.add(bm);
        }
    };

    private void captureImage() {
        if (mCamera != null) {
            mCamera.takePicture(null, raw, jpeg);
        }
    }

    private void changeEffect() {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            List<String> effectlist = params.getSupportedColorEffects();
            final String[] effects = effectlist.toArray(new String[effectlist.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Color Effect");
            builder.setItems(effects, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String effect = effects[which];
                    Camera.Parameters params = mCamera.getParameters();
                    params.setColorEffect(effect);
                    mCamera.setParameters(params);
                }
            });
            builder.create().show();
        }
    }
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private void changeCamera() {
        if (mCamera != null) {
            mCamera.release();
        }
        cameraId = (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK)? Camera.CameraInfo.CAMERA_FACING_FRONT: Camera.CameraInfo.CAMERA_FACING_BACK;
        openCamera();
        startPreview();
    }

    int[] CAMERA_ROTATE = {90, 0, 270, 180};

    private void openCamera() {
        if (mCamera != null) {
            mCamera.release();
        }
        mCamera = Camera.open(cameraId);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        mCamera.setDisplayOrientation(CAMERA_ROTATE[rotation]);

    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void startPreview() {
        if (mCamera != null && mHolder != null) {
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
        mHolder = holder;
        startPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        stopPreview();
        mHolder = holder;
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreview();
        releaseCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

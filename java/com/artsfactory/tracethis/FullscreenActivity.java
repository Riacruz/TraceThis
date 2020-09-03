package com.artsfactory.tracethis;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


public class FullscreenActivity extends AppCompatActivity  {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    ////MI ROLLO////////////
    private AdView mAdView;
    private Boolean modoBloqueo = true;
    private Button btnClickMe;
    private int exitCount = 0;
    private int totalBrillo = 100;
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private ImageView foto_gallery;
    private SeekBar brightnessBar, lightSeekBar, contrastBar, saturationBar;
    private float brightnessFloat = 0;
    private float contrastFloat = 10;
    private float saturationFloat = 200;
    /////////////////////
    ///EL ROLLO DE HACER ZOOM///
    float[] lastEvent = null;
    float d = 0f;
    float newRot = 0f;
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();
    private boolean firsTimeMatrix = true;
    private Matrix initialMatrix = new Matrix();
    public static String fileNAME;
    public static int framePos = 0;

    private float scale = 0;
    private float newDist = 0;

    // Fields
    private String TAG = this.getClass().getSimpleName();

    // We can be in one of these 3 states
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // Remember some things for zooming
    private PointF start = new PointF();
    private PointF mid = new PointF();
    float oldDist = 1f;
    //////////////////////////

    private static final boolean AUTO_HIDE = true;
    private int hideView =  View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LOW_PROFILE;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 0;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 0;
    private final Handler mHideHandler = new Handler();
    private View mContentView;

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                //delayedHide(AUTO_HIDE_DELAY_MILLIS);
                //hideNavigationBar();
            }
            return true;
        }
    };






    @Override
    public boolean onTouchEvent(MotionEvent e) {
        return true;
    }

        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if(modoBloqueo) {
                if (keyCode == KeyEvent.KEYCODE_HOME) {
                    Log.i("TESTE", "BOTAO HOME");
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    Log.i("TESTE", "BOTAO BACK");
                    //hideNavigationBar();
                    return true;
                }

                if (keyCode == KeyEvent.KEYCODE_SOFT_LEFT) {
                    Log.i("TESTE", "SOFT_LEFT");
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_SOFT_RIGHT) {
                    Log.i("TESTE", "BOTAO HOME");
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
                    Log.i("TESTE", "BOTAO HOME");
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_CALL) {
                    Log.i("TESTE", "BOTAO HOME");
                    return true;
                }


                if (keyCode == KeyEvent.KEYCODE_APP_SWITCH) {
                    Log.i("TESTE", "BOTAO HOME");
                    //System.exit(1);
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_PROFILE_SWITCH) {
                    Log.i("TESTE", "BOTAO HOME");
                    //System.exit(1);
                   // hideNavigationBar();
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_1) {
                    Log.i("TESTE", "BOTAO HOME");
                    //System.exit(1);
                    //hideNavigationBar();
                    return true;
                }
            }
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                exitCount += 1;
                if(exitCount >=10) {
                    //System.exit(1); //aqui
                    exitDialog();
                    exitCount = 0;
                }

                if(exitCount==1) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            exitCount = 0;
                        }
                    }, 5000);
                }
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if(modoBloqueo) {
                   //buttonBRILLO// final Button turnOnScreenButton = (Button)findViewById(R.id.buttonMasBrillo);
                    //buttonBRILLO// final Button turnOffScreenButton = (Button)findViewById(R.id.buttonMenosBrillo);

                    final Button pictureButton = (Button)findViewById(R.id.buttonOpenGallery);
                    final TextView textLight =  findViewById(R.id.textViewLight);
                    //textLight.setVisibility((View.VISIBLE));
                    final TextView textLight2 =  findViewById(R.id.textViewBrightness);
                    textLight2.setVisibility((View.VISIBLE));
                    final TextView textLight3 =  findViewById(R.id.textViewContrast);
                    textLight3.setVisibility((View.VISIBLE));
                    final TextView textLight4 =  findViewById(R.id.textViewSaturation);
                    textLight4.setVisibility((View.VISIBLE));
                    brightnessBar.setVisibility((View.VISIBLE));
                    contrastBar.setVisibility((View.VISIBLE));
                    final Button resetButton = findViewById(R.id.buttonDebug);
                    resetButton.setVisibility(View.VISIBLE);
                    saturationBar.setVisibility(View.VISIBLE);
                    //buttonBRILLO// turnOnScreenButton.setVisibility(View.VISIBLE);
                    //buttonBRILLO//turnOffScreenButton.setVisibility(View.VISIBLE);
                    pictureButton.setVisibility(View.VISIBLE);
                    resetButton.setVisibility(View.VISIBLE);

                    //final TextView textLight =  findViewById(R.id.textViewLight);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        lightSeekBar.setVisibility((View.VISIBLE));
                        textLight.setVisibility((View.VISIBLE));
                    }



                    final TableLayout tableSettings = findViewById(R.id.tableSettings);
                    tableSettings.setVisibility((View.VISIBLE));

                    final TextView text = findViewById(R.id.fullscreen_content);
                    text.setText("");

                    btnClickMe.setText(getResources().getText(R.string.dummy_content));

                    modoBloqueo = false;
                    show();
                }
                else {
                    //buttonBRILLO// final Button turnOnScreenButton = (Button)findViewById(R.id.buttonMasBrillo);
                    //buttonBRILLO// final Button turnOffScreenButton = (Button)findViewById(R.id.buttonMenosBrillo);

                    final TextView text = findViewById(R.id.fullscreen_content);
                    text.setText(getResources().getText(R.string.dummy_content));
                    final Button pictureButton = (Button)findViewById(R.id.buttonOpenGallery);
                    final TextView textLight =  findViewById(R.id.textViewLight);

                    final TextView textLight2 =  findViewById(R.id.textViewBrightness);
                    textLight2.setVisibility((View.INVISIBLE));
                    final TextView textLight3 =  findViewById(R.id.textViewContrast);
                    textLight3.setVisibility((View.INVISIBLE));
                    final TextView textLight4 =  findViewById(R.id.textViewSaturation);
                    textLight4.setVisibility((View.INVISIBLE));
                        lightSeekBar.setVisibility((View.GONE));
                        textLight.setVisibility((View.GONE));
                    brightnessBar.setVisibility((View.INVISIBLE));
                    contrastBar.setVisibility((View.INVISIBLE));
                    final Button resetButton = findViewById(R.id.buttonDebug);
                    resetButton.setVisibility(View.INVISIBLE);
                    saturationBar.setVisibility(View.INVISIBLE);
                    //buttonBRILLO// turnOnScreenButton.setVisibility(View.INVISIBLE);
                    //buttonBRILLO// turnOffScreenButton.setVisibility(View.INVISIBLE);
                    pictureButton.setVisibility(View.INVISIBLE);


                    final TableLayout tableSettings = findViewById(R.id.tableSettings);
                    tableSettings.setVisibility((View.GONE));
                    btnClickMe.setText("Lock");
                    modoBloqueo = true;
                    hide();
                    //hideNavigationBar();
                }
                return true;
            }


            return super.onKeyDown(keyCode, event);
    }

private void exitDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenActivity.this, R.style.MyAlertDialogStyle);
    builder.setTitle("EXIT");
    builder.setMessage("Close Trace This?");
    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            finish();
        }
    });
    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int id) {
            dialog.dismiss();
        }
    });
    builder.show();
}

    private void resetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(FullscreenActivity.this, R.style.MyAlertDialogStyle);
        builder.setTitle("RESET");
        builder.setMessage("Reset settings?");
        builder.setPositiveButton("COLOR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                brightnessBar.setProgress(255);
                contrastBar.setProgress(10);
                saturationBar.setProgress((200));
                setSaturation(200);
                contrastFloat = 10;
                brightnessFloat = 0;
                if(foto_gallery.getDrawingCache()==null) foto_gallery.buildDrawingCache();
                Bitmap bmap = foto_gallery.getDrawingCache();
                foto_gallery.setImageBitmap(changeBitmapContrastBrightness(bmap, contrastFloat, brightnessFloat ));

            }
        });
        builder.setNegativeButton("ROTATE/SIZE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                ;
                foto_gallery.setScaleType(ImageView.ScaleType.FIT_CENTER);
                matrix = new Matrix();
                savedMatrix = new Matrix();
                foto_gallery.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
                foto_gallery.setRotation(0);
                foto_gallery.setImageMatrix(matrix);
                foto_gallery.setImageURI(imageUri);
                foto_gallery.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //foto_gallery.destroyDrawingCache();
                foto_gallery.setScaleType(ImageView.ScaleType.FIT_CENTER);
                //foto_gallery.buildDrawingCache();
                if(foto_gallery.getDrawingCache()==null) foto_gallery.buildDrawingCache();
                Bitmap bmap = foto_gallery.getDrawingCache();
                foto_gallery.setImageBitmap(changeBitmapContrastBrightness(bmap, contrastFloat, brightnessFloat ));


            }
        });
        builder.show();
    }

/*
    @Override
    protected void onUserLeaveHint()
    {
        Log.d("onUserLeaveHint","Home button pressed");
        super.onUserLeaveHint();

        //hideNavigationBar();

    }
    */
    @Override
    protected void onPause() {
        super.onPause();

        ActivityManager activityManager = (ActivityManager) getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);

        activityManager.moveTaskToFront(getTaskId(), 0);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && modoBloqueo) {

            getWindow().getDecorView().setSystemUiVisibility(hideView);
            Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeDialog);
           // hideNavigationBar();
        } else {

            if(modoBloqueo) {
                getWindow().getDecorView().setSystemUiVisibility(hideView);
                Intent closeDialog = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                sendBroadcast(closeDialog);
               // hideNavigationBar();
            }
        }

    }

    /**
     *
     * @param bmp input bitmap
     * @param contrast 0..10 1 is default
     * @param brightness -255..255 0 is default
     * @return new bitmap
     */
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        contrast = contrast*0.1f;
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);


        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);


        return ret;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatActivity activity = FullscreenActivity.this;
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_fullscreen);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mVisible = true;
        if(modoBloqueo) {
            //hideNavigationBar();
            //buttonBRILLO//  final Button turnOnScreenButton = (Button)findViewById(R.id.buttonMasBrillo);
            //buttonBRILLO// final Button turnOffScreenButton = (Button)findViewById(R.id.buttonMenosBrillo);
            final Button pictureButton = (Button)findViewById(R.id.buttonOpenGallery);
            final TextView textLight =  findViewById(R.id.textViewLight);
            textLight.setVisibility((View.INVISIBLE));
            final TextView textLight3 =  findViewById(R.id.textViewContrast);
            textLight3.setVisibility((View.INVISIBLE));
            final TextView textLight2 =  findViewById(R.id.textViewBrightness);
            textLight2.setVisibility((View.INVISIBLE));
            //buttonBRILLO// turnOnScreenButton.setVisibility(View.INVISIBLE);
            //buttonBRILLO// turnOffScreenButton.setVisibility(View.INVISIBLE);
            pictureButton.setVisibility(View.INVISIBLE);
        }
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
        mContentView.setSystemUiVisibility(hideView);
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(hideView);

//Intialization Button

        btnClickMe = (Button) findViewById(R.id.dummy_button);
/*
        btnClickMe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(modoBloqueo) {
                    btnClickMe.setText("Unlock");
                    modoBloqueo = false;
                    show();
                }
                else {
                    btnClickMe.setText("Lock");
                    modoBloqueo = true;
                    hide();
                    //hideNavigationBar();
                }
                // click handling code
            }
        });
        */

        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modoBloqueo) {
                    mContentView.setSystemUiVisibility(hideView);
                    //if(!modoBloqueo) toggle();
                    //hideNavigationBar();
                }

                 if(modoBloqueo) getWindow().getDecorView().setSystemUiVisibility(hideView);

                // Code below is to handle presses of Volume up or Volume down.
                // Without this, after pressing volume buttons, the navigation bar will
                // show up and won't hide
                final View decorView = getWindow().getDecorView();
                decorView
                        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
                        {

                            @Override
                            public void onSystemUiVisibilityChange(int visibility)
                            {
                                if(modoBloqueo)
                                {
                                    decorView.setSystemUiVisibility(hideView);
                                }
                            }
                        });


            }

        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        if(modoBloqueo) findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);

        //registerKioskModeScreenOffReceiver();
       // startKioskService();  // add this


        //DebugButton//

        final Button debugButton = (Button)findViewById(R.id.buttonDebug);
        debugButton.setVisibility((View.GONE));
        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetDialog();
            }
        });
/*
        // Get turn on screen button
        final Button turnOnScreenButton = (Button)findViewById(R.id.buttonMasBrillo);
        turnOnScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Context context = getApplicationContext();




                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(getApplicationContext())) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 200);
                    }
                    else {
                        changeScreenBrightness(context, 255, true);
                    }
                }
            }
        });
        */

        final Button pictureButton = (Button)findViewById(R.id.buttonOpenGallery);
        pictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!modoBloqueo)
                {

                    openGallery();

                }
            }
        });

        brightnessBar=(SeekBar)findViewById(R.id.brightnessSeekBar);
        brightnessBar.setProgress(255);
        // perform seek bar change listener event used for getting the progress value
        brightnessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;


            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                final TextView textLight2 =  findViewById(R.id.textViewBrightness);
                String content = textLight2.getText().toString();
                Toast.makeText(FullscreenActivity.this, content+" :" + (progressChangedValue-255),
                        Toast.LENGTH_SHORT).show();

                foto_gallery.buildDrawingCache();
                Bitmap bmap = foto_gallery.getDrawingCache();
                //foto_gallery.setImageBitmap(changeBitmapContrastBrightness(bmap,1f,progressChangedValue));
                foto_gallery.setImageBitmap(changeBitmapContrastBrightness(bmap,contrastFloat,progressChangedValue-255));
                brightnessFloat = progressChangedValue-255;
            }
        });

        lightSeekBar=(SeekBar)findViewById(R.id.lightSeekBar);
        lightSeekBar.setProgress(50);
        contrastBar=(SeekBar)findViewById(R.id.seekBaContrast);
        contrastBar.setProgress(10);
        saturationBar=(SeekBar)findViewById(R.id.seekBarSaturation);
        saturationBar.setProgress((200));
        lightSeekBar.setVisibility((View.INVISIBLE));

        final TextView textLight =  findViewById(R.id.textViewLight);
        textLight.setVisibility((View.INVISIBLE));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            lightSeekBar.setVisibility(View.GONE); // you can use INVISIBLE also instead of
            textLight.setVisibility((View.GONE));
        }
        brightnessBar.setVisibility((View.INVISIBLE));
        contrastBar.setVisibility((View.INVISIBLE));
        saturationBar.setVisibility((View.INVISIBLE));

        final TextView textLight2 =  findViewById(R.id.textViewBrightness);
        textLight2.setVisibility((View.INVISIBLE));
        final TextView textLight3 =  findViewById(R.id.textViewContrast);
        textLight3.setVisibility((View.INVISIBLE));
        final TextView textLight4 =  findViewById(R.id.textViewSaturation);
        textLight4.setVisibility((View.INVISIBLE));
        // perform seek bar change listener event used for getting the progress value
        lightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;
            Context context = getApplicationContext();

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;


            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(getApplicationContext())) {
                        Toast.makeText(FullscreenActivity.this, "Need permission for change light",
                                Toast.LENGTH_LONG).show();
                    } else {
                        String content = textLight.getText().toString();
                        Toast.makeText(FullscreenActivity.this, content+" :" + progressChangedValue,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FullscreenActivity.this, "Only for > Android 6.0",
                            Toast.LENGTH_SHORT).show();
                }
                /*
                String content = textLight.getText().toString();
                Toast.makeText(FullscreenActivity.this, content+" :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();*/

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.System.canWrite(getApplicationContext())) {
                        Intent inte = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(inte, 200);
                    }
                    else {
                        changeScreenBrightnessSeekBar(context,progressChangedValue);
                    }
                }


            }
        });


        // perform seek bar change listener event used for getting the progress value
        contrastBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;


            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                String content = textLight3.getText().toString();
                Toast.makeText(FullscreenActivity.this, content+" :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
                foto_gallery.buildDrawingCache();
                Bitmap bmap = foto_gallery.getDrawingCache();
                //foto_gallery.setImageBitmap(changeBitmapContrastBrightness(bmap,1f,progressChangedValue));
                foto_gallery.setImageBitmap(changeBitmapContrastBrightness(bmap,progressChangedValue,brightnessFloat));
                contrastFloat = progressChangedValue;
            }
        });

        saturationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = 0;

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;


            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                String content = textLight4.getText().toString();
                Toast.makeText(FullscreenActivity.this, content+" :" + progressChangedValue,
                        Toast.LENGTH_SHORT).show();
                setSaturation(progressChangedValue);
                saturationFloat = progressChangedValue;
            }
        });

        foto_gallery = (ImageView)findViewById(R.id.imageView);
        foto_gallery.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!modoBloqueo) {
                    ImageView view = (ImageView) v;
                    if(firsTimeMatrix) {
                        initialMatrix = matrix;
                        firsTimeMatrix = false;
                    }

                    // Handle touch events here...
                    view.setScaleType(ImageView.ScaleType.MATRIX);
                    //
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            savedMatrix.set(matrix);
                            start.set(event.getX(), event.getY());
                            mode = DRAG;
                            lastEvent = null;
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            oldDist = spacing(event);
                            if (oldDist > 10f) {
                                savedMatrix.set(matrix);
                                midPoint(mid, event);
                                mode = ZOOM;
                            }
                            lastEvent = new float[4];
                            lastEvent[0] = event.getX(0);
                            lastEvent[1] = event.getX(1);
                            lastEvent[2] = event.getY(0);
                            lastEvent[3] = event.getY(1);
                            d = rotation(event);
                            break;
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            mode = NONE;
                            lastEvent = null;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            if (mode == DRAG) {
                                // ...
                                matrix.set(savedMatrix);
                                matrix.postTranslate(event.getX() - start.x, event.getY()
                                        - start.y);
                            } else if (mode == ZOOM && event.getPointerCount() == 2) {
                                float newDist = spacing(event);
                                matrix.set(savedMatrix);
                                if (newDist > 10f) {
                                    float scale = newDist / oldDist;
                                    matrix.postScale(scale, scale, mid.x, mid.y);
                                }
                                if (lastEvent != null) {
                                    newRot = rotation(event);
                                    float r = newRot - d;
                                    matrix.postRotate(r, view.getMeasuredWidth() / 2,
                                            view.getMeasuredHeight() / 2);
                                }
                            }
                            break;
                    }

                    view.setImageMatrix(matrix);
                }

                return true;
            }

            private float rotation(MotionEvent event) {
                double delta_x = (event.getX(0) - event.getX(1));
                double delta_y = (event.getY(0) - event.getY(1));
                double radians = Math.atan2(delta_y, delta_x);

                return (float) Math.toDegrees(radians);
            }
            private float spacing(MotionEvent event) {
                float x = event.getX(0) - event.getX(1);
                float y = event.getY(0) - event.getY(1);
                return (float)Math.sqrt(x * x + y * y);
            }

            private void midPoint(PointF point, MotionEvent event) {
                float x = event.getX(0) + event.getX(1);
                float y = event.getY(0) + event.getY(1);
                point.set(x / 2, y / 2);
            }


        });

/*
        foto_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!modoBloqueo) openGallery();
            }
        });
*/
    }

private void setSaturation(float sat) {
        sat = (sat-100)*0.01f;
    ImageView imageview = (ImageView) findViewById(R.id.imageView);
    ColorMatrix matrix = new ColorMatrix();
    matrix.setSaturation(sat);

    ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
    imageview.setColorFilter(filter);
}

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            brightnessBar.setProgress(255);
            contrastBar.setProgress(10);
            saturationBar.setProgress((200));
            setSaturation(200);
            foto_gallery.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageUri = data.getData();
            matrix = new Matrix();
            savedMatrix = new Matrix();
            foto_gallery.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            foto_gallery.setRotation(0);
            foto_gallery.setImageMatrix(matrix);
            foto_gallery.setImageURI(imageUri);
            foto_gallery.setScaleType(ImageView.ScaleType.FIT_CENTER);
            foto_gallery.destroyDrawingCache();
            foto_gallery.setScaleType(ImageView.ScaleType.FIT_CENTER);
            foto_gallery.buildDrawingCache();
            Bitmap bmap = foto_gallery.getDrawingCache();
            contrastFloat = 10;
            brightnessFloat = 0;
            foto_gallery.setImageBitmap(changeBitmapContrastBrightness(bmap, contrastFloat, brightnessFloat ));
            setSaturation(200);


        }
    }



    // This function only take effect in real physical android device,
    // it can not take effect in android emulator.
    private void changeScreenBrightness(Context context, int screenBrightnessValue, Boolean sube)
    {
        // Change the screen brightness change mode to manual.
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        // Apply the screen brightness value to the system, this will change the value in Settings ---> Display ---> Brightness level.
        // It will also change the screen brightness for the device.

        try {
            if(sube)
                screenBrightnessValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)+10;
            else
                screenBrightnessValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)-10;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);

        /*
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = screenBrightnessValue / 255f;
        window.setAttributes(layoutParams);
        */
    }

    private void changeScreenBrightnessSeekBar(Context context, int screenBrightnessValue)
    {
        // Change the screen brightness change mode to manual.
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
        // Apply the screen brightness value to the system, this will change the value in Settings ---> Display ---> Brightness level.
        // It will also change the screen brightness for the device.

        /*
        try {
                //screenBrightnessValue = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS)+10;

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        */

        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue);

        /*
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.screenBrightness = screenBrightnessValue / 255f;
        window.setAttributes(layoutParams);
        */
    }


    //////////////////////////////





    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        if(modoBloqueo) {
            delayedHide(0);
        }
        else {
            delayedHide(0);
        }
    }

    private void toggle() {

        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
            mControlsView.setVisibility(View.GONE);
            mVisible = false;

            // Schedule a runnable to remove the status and navigation bar after a delay
            mHideHandler.removeCallbacks(mShowPart2Runnable);
            mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);


    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}

package com.artsfactory.tracethis;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by AbhiAndroid
 */

public class SecondActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(SecondActivity.this,FullscreenActivity.class));

        // close splash activity
        finish();
    }
}
package com.lovejoy777sarootool.rootool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lovejoy777sarootool.rootool.R;

/**
 * Created by lovejoy on 20/10/14.
 */
public class Splash extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    sleep(2500);
                    Intent mainActivityIntent = new Intent("com.lovejoy777sarootool.rootool.BROWSER");
                    startActivity(mainActivityIntent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }
            }
        };
        logoTimer.start();
    }
}

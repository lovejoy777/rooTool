package com.lovejoy777sarootool.rootool.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lovejoy777sarootool.rootool.R;

/**
 * Created by lovejoy on 20/10/14.
 */
public class InitdUtils extends Activity

    {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

            mainmenu();

    }

        private void mainmenu() {

            Intent mainActivityIntent = new Intent("com.lovejoy777sarootool.rootool.MENU");
            startActivity(mainActivityIntent);

        }


}

package com.lovejoy777sarootool.rootool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.lovejoy777sarootool.rootool.R;

/**
 * Created by lovejoy on 20/10/14.
 */
public class Menu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        Button buttoninitd = (Button) findViewById(R.id.buttoninitd);
        Button buttonroms = (Button) findViewById(R.id.buttonroms);

        buttoninitd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent("com.lovejoy777sarootool.rootool.INITD"));
            }
        });


        buttonroms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent("com.lovejoy777sarootool.rootool.ROMS"));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main1, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the menu/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_instructions:
                Intent intent1 = new Intent();
                intent1.setClass(this, Instructions.class);
                startActivity(intent1);
                break;


            case R.id.action_about:
                Intent intent2 = new Intent();
                intent2.setClass(this, About.class);
                startActivity(intent2);
                break;

            case R.id.action_changelog:
                Intent intent3 = new Intent();
                intent3.setClass(this, ChangeLog.class);
                startActivity(intent3);
                break;


         //   case R.id.action_donation:
         //       Intent intent4 = new Intent();
         //       intent4.setClass(this, InAppBillingCoffee.class);
         //       startActivity(intent4);
         //       break;
        }


        return super.onOptionsItemSelected(item);


    }




}

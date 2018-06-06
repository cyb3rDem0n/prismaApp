/*
 *  Created by cyb3rDem0n (Giuseppe D'Agostino) on 01/06/18 15.26
 *  Copyright (c) 2018 . All rights reserved.
 *  Email dagostinogiuseppe@outlook.com
 *  Last modified 01/06/18 15.25
 */

package project.prisma.starnotifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

// this class has only one purpose, be a splash screen for our app
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int SPLASH_TIME = 1500;
        new Handler().postDelayed(() -> {

            // run(), executed after 1.5"

            // start my activity
            Intent intent = new Intent(MainActivity.this, ReadData.class);
            startActivity(intent);

            finish();
        }, SPLASH_TIME);
    }
}
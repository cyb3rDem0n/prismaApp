/*
 *  Created by cyb3rDem0n (Giuseppe D'Agostino) on 01/06/18 15.26
 *  Copyright (c) 2018 . All rights reserved.
 *  Email dagostinogiuseppe@outlook.com
 *  Last modified 01/06/18 15.25
 */

package prisma.project.starnotifier;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class EventDetails extends Activity {
    TextView textViewStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_datails);

        textViewStation = findViewById(R.id.textView7);

        Intent intent = getIntent();

        // it works
        String stationIntent = intent.getStringExtra("EVENT");
        textViewStation.setText(stationIntent);
        //Toast.makeText(getApplicationContext(),"... " + stationIntent, Toast.LENGTH_SHORT).show();

        }

    }


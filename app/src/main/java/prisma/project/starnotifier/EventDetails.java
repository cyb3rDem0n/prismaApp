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

public class EventDetails extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        TextView textViewStation = findViewById(R.id.textStation);
        TextView textViewDataObs = findViewById(R.id.textDataObs);
        TextView textViewEventName = findViewById(R.id.textEventName);
        TextView textViewLat = findViewById(R.id.textLat);
        TextView textViewLong = findViewById(R.id.textLong);
        TextView textViewTimeStamp = findViewById(R.id.textTimeStamp);
        TextView textViewUrl = findViewById(R.id.textUrl);

        // invoke the intend from ReadData activity to pull our data
        Intent intent = getIntent();

        String stationName = intent.getStringExtra("STATION");
        textViewStation.setText("Station Name: " + stationName);

        String dataObs = intent.getStringExtra("DATAOBS");
        textViewDataObs.setText("Observation Data: " + dataObs);

        String timeStamp = intent.getStringExtra("TIMESTAMP");
        textViewTimeStamp.setText("Event Timestamp: " + timeStamp);

        String latitude = intent.getStringExtra("LAT");
        textViewLat.setText("Latitude: " + latitude);

        String longitude = intent.getStringExtra("LONG");
        textViewLong.setText("Longitude: " + longitude);

        String url = intent.getStringExtra("URL");
        textViewUrl.setText("Cam Image Url: " + url);

        String eventName = intent.getStringExtra("EVENTNAME");
        textViewEventName.setText("Event Name: " + eventName);

        }

    }


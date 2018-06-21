/*
 *  Created by cyb3rDem0n (Giuseppe D'Agostino) on 01/06/18 15.26
 *  Copyright (c) 2018 . All rights reserved.
 *  Email dagostinogiuseppe@outlook.com
 *  Last modified 01/06/18 15.25
 */

package project.prisma.starnotifier;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadData extends Activity {

    // JSON Node names
    public static final String ITEM_ID = "id";
    public static final String ITEM_STATION = "station";
    public static final String ITEM_TIMESTAMP = "timestamp";
    public static final String mypreference = "mypref";
    public static final String TimeStamp = "timStampKey";
    public static final String FirstRun = "firstRun";
    private static final String TAG = "CyberDemon Logging";

    // our php files
    String url = "http://testmyapp.altervista.org/read.php";

    // array to store item for list menu and his structure
    ArrayList<HashMap<String, String>> Item_List;
    ListView listview = null;
    ArrayList<HashMap<String, Integer>> Initial_Item_Num;
    ListAdapter adapter;

    // loading progress animation
    ProgressDialog PD;

    // layout elements
    TextView newEvent;
    ImageButton updateButton;

    // Shared Preferences elements
    SharedPreferences sharedpreferences;

    // notification object
    private NotificationManager notificationManager;

    boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);

        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        Item_List = new ArrayList<>();
        Initial_Item_Num = new ArrayList<>();

        listview = findViewById(R.id.listview_01);
        newEvent = findViewById(R.id.new_event);
        updateButton = findViewById(R.id.update);

        updateButton.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Update Button Pressed", Toast.LENGTH_SHORT).show();
        });


        if (!sharedpreferences.contains(TimeStamp)) {
            Log.i(TAG, "First Run");
            // update for the first time the ui and local ts
            ReadDataFromDB();
            //setTimeStamp(returnLatestTs());
        }else {
            Log.i(TAG, "Not First Run");
            // parsedTS > savedTS return true
                if (downloadAndcheckLastTs(sharedpreferences.getLong(TimeStamp, 0L))) {
                    Log.i(TAG, "Update Available ");
                    //update ui ad local saved ts
                    ReadDataFromDB();
                    setTimeStamp(returnLatestTs());
                    // show a notification message
                }else{
                    ReadDataFromDB();
                    Log.i(TAG, "Updated");
                }
        }
    }

    public void setTimeStamp(Long newTimestamp) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong(TimeStamp, newTimestamp);
        editor.apply();
    }

    public Long getTimeStamp() {
        Long savedTs = 0L;
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains(TimeStamp)) {
            savedTs = sharedpreferences.getLong(TimeStamp, 0L);
        }
        return savedTs;
    }

    public void setFirstRun(Boolean runOrNot) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (runOrNot){
            editor.putBoolean(FirstRun, true);
            editor.apply();
        }
        else{
            editor.putBoolean(FirstRun, false);
            editor.apply();
        }
    }

    public Boolean isFirstRun() {
        Boolean runOrNOt = true;
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains(FirstRun))
            if (!sharedpreferences.getBoolean(FirstRun, false))
                runOrNOt = false;
            else
                runOrNOt = true;

        return runOrNOt;
    }

    // download the last timestamp from db
    private Long returnLatestTs() {
        PD = new ProgressDialog(this);
        PD.setMessage("Parsing Timestamp.....");
        PD.show();
        final Long[] parsedTs = {0L};

        JsonObjectRequest jreq = new JsonObjectRequest(Method.GET, url, response -> {
            try {
                int success = response.getInt("success");
                if (success == 1) { // well done
                    JSONArray ja = response.getJSONArray("my_testmyapp");
                    JSONObject jobj = ja.getJSONObject(ja.length() - 1);
                    parsedTs[0] = jobj.getLong(ITEM_TIMESTAMP);
                    PD.dismiss();

                } // if ends

            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

        }, error -> PD.dismiss());
        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(jreq);
        Log.i(TAG, "Retured TS: " + parsedTs[0]);
        return parsedTs[0];
    }

    // retrieve and campare
    private boolean downloadAndcheckLastTs(Long localSavedTs) {
        JsonObjectRequest jreq = new JsonObjectRequest(Method.GET, url, response -> {
            try {
                int success = response.getInt("success");
                if (success == 1) {
                    JSONArray ja = response.getJSONArray("my_testmyapp");
                    JSONObject jobj = ja.getJSONObject(ja.length() - 1);

                    // x > y = 1 || x  < y = -1 || x = y = 0
                    status = Long.compare(jobj.getLong(ITEM_TIMESTAMP), localSavedTs) > 0;
                    Log.i(TAG, "parsed ts: " +jobj.getLong(ITEM_TIMESTAMP) + " memorized ts: " + localSavedTs + " STATUS = " + status);
                    createNotification("New Event Detected");

                } // if ends

            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

        }, error -> PD.dismiss());
        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(jreq);

        return status;
    }

    // retrieve all the stuf and popolate ui
    private void ReadDataFromDB() {
        PD = new ProgressDialog(this);
        PD.setMessage("Loading.....");
        PD.show();

        JsonObjectRequest jreq = new JsonObjectRequest(Method.GET, url, response -> {
            try {
                int success = response.getInt("success");

                if (success == 1) {

                    JSONArray ja = response.getJSONArray("my_testmyapp");

                   // JSONObject jobj_ = ja.getJSONObject(ja.length() - 1);
                   // setTimeStamp(jobj_.getLong(ITEM_TIMESTAMP));

                    for (int i = 0; i < ja.length(); i++) {

                        JSONObject jobj = ja.getJSONObject(i);

                        HashMap<String, String> item = new HashMap<>();
                        item.put(ITEM_ID, jobj.getString(ITEM_ID));
                        item.put(ITEM_STATION, jobj.getString(ITEM_STATION));
                        Item_List.add(item);

                    } // for loop ends

                    String[] from = {ITEM_ID, ITEM_STATION};
                    int[] to = {R.id.item_id, R.id.item_station};

                    adapter = new SimpleAdapter(
                            getApplicationContext(), Item_List,
                            R.layout.list_items, from, to);

                    listview.setAdapter(adapter);

                    listview.setOnItemClickListener(new ListitemClickListener());

                    PD.dismiss();

                } // if ends

            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

        }, error -> PD.dismiss());
        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(jreq);
    }

    // Check android version and produce a notification with a simple message using different libs if we're on Oreo device.
    @SuppressWarnings("deprecation")
    public void createNotification(String aMessage) {
        final int NOTIFY_ID = 1002;

        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (notificationManager == null) {
            notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(this, id);

            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(aMessage)  // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        } else {

            builder = new NotificationCompat.Builder(this);

            intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

            builder.setContentTitle(aMessage)                           // required
                    .setSmallIcon(android.R.drawable.ic_popup_reminder) // required
                    .setContentText(this.getString(R.string.app_name))  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setTicker(aMessage)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
        }
        Notification notification = builder.build();
        notificationManager.notify(NOTIFY_ID, notification);
    }

    // On List Item Click move to Details Activity
    class ListitemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent modify_intent = new Intent(ReadData.this,
                    EventDetails.class);
            modify_intent.putExtra("item", Item_List.get(position));
            startActivity(modify_intent);

        }

    }
}
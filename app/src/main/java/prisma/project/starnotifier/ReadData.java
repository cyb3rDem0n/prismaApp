/*
 *  Created by cyb3rDem0n (Giuseppe D'Agostino) on 01/06/18 15.26
 *  Copyright (c) 2018 . All rights reserved.
 *  Email dagostinogiuseppe@outlook.com
 *  Last modified 26/06/18 12.00
 */

package prisma.project.starnotifier;

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

import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ReadData extends Activity {

    // JSON Node names
    private static final String ITEM_ID = "id";
    private static final String ITEM_STATION = "station";
    private static final String ITEM_TIMESTAMP = "timestamp";
    private static final String MY_PREFERENCES = "mypref"; // DO NOT CHANGE THE STRING VALUE, MEMORY POINTER
    private static final String TIMESTAMP = "timStampKey";
    private static final String TAG = "CyberDemon Log";
    private static final String MESSAGE_NO_UP = "No New event";
    private static final String MESSAGE_NEW_UP = "New event";
    private static final String MESSAGE_FIRST_RUN = "First Run";
    // our php files
    private String url = "http://testmyapp.altervista.org/read.php";
    // array to store item for list menu and his structure
    private ArrayList<HashMap<String, String>> itemList;
    private ListView listview = null;
    protected ArrayList<HashMap<String, Integer>> initialItemMum;
    private ListAdapter adapter;
    // loading progress animation
    private ProgressDialog PD;
    // layout elements
    private TextView newEvent;
    protected ImageButton updateButton;
    // Shared Preferences elements
    private SharedPreferences sharedpreferences;
    private boolean status = false;
    // notification object
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);

        sharedpreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        itemList = new ArrayList<>();
        initialItemMum = new ArrayList<>();

        listview = findViewById(R.id.listview_01);
        newEvent = findViewById(R.id.new_event);
        updateButton = findViewById(R.id.update);

        updateButton.setOnClickListener(v ->
                Toast.makeText(getApplicationContext(), "Update Button Pressed", Toast.LENGTH_SHORT).show());

        /*
        Every time the app start, this activity check if a new event is persisted on
        our db.
        If this is the first run, we populate the UI with @ReadDataFromDB else
        just verify if we need to perform an update just comparing the memorized timestamp
        with the last present in the DB.
        */
        if (!sharedpreferences.contains(TIMESTAMP)) {
            Log.i(TAG, "--> First Run");
            // update for the first time the ui and write ts into sharedPreferences
            readDataFromDb(true, MESSAGE_FIRST_RUN);
        } else {
            Log.i(TAG, "--> Not First Run");
            // parsedTS > savedTS return true
            if (downloadAndCheckLastTs(sharedpreferences.getLong(TIMESTAMP, 0L))) {
                Log.i(TAG, "--> NFR, Update Available ");
                //update ui and write the new ts into sharedPreferences
                readDataFromDb(true, MESSAGE_NEW_UP);
            } else {
                // just refresh ui
                readDataFromDb(false, MESSAGE_NO_UP);
                Log.i(TAG, "--> NFR, Updated");
            }
        }
    }

    // retrieve and compare
    protected boolean downloadAndCheckLastTs(Long localSavedTs) {
        // check if local saved TS is not 0L
        if (Long.compare(localSavedTs, 0L) == 1) {

            JsonObjectRequest jreq = new JsonObjectRequest(Method.GET, url, response -> {
                try {
                    int success = response.getInt("success");
                    if (success == 1) {
                        JSONArray ja = response.getJSONArray("my_testmyapp");
                        JSONObject jobj = ja.getJSONObject(ja.length() - 1);

                        Log.i(TAG, "--> parsedTS[" + jobj.getLong(ITEM_TIMESTAMP) + "] sharedPreferencesTS[" + localSavedTs + "]");

                        status = Long.compare(jobj.getLong(ITEM_TIMESTAMP), localSavedTs) > 0;
                        Log.i(TAG, "--> UpdateAvailable[" + status + "]");

                        // double check before make an update on sharedPreferences
                        if (status || localSavedTs - jobj.getLong(ITEM_TIMESTAMP) < 0) {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putLong(TIMESTAMP, jobj.getLong(ITEM_TIMESTAMP));
                            editor.apply();
                            createNotification(MESSAGE_NEW_UP);
                        } else {
                            newEvent.setText(MESSAGE_NO_UP);
                        }
                    } // if ends

                } catch (JSONException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }

            }, error -> PD.dismiss());
            // Adding request to request queue
            MyApplication.getInstance().addToReqQueue(jreq);

            // if true, we have new events and a not 0L value
            return status;
            // else, we have a 0L value or no new events
        } else
            return false;
    }

    // retrieve all the stuff and populate ui
    private void readDataFromDb(boolean writeTimeStamp, String message) {
        PD = new ProgressDialog(this);
        PD.setMessage("Loading.....");
        PD.show();

        // save our requested info
        JsonObjectRequest jreq = new JsonObjectRequest(Method.GET, url, response -> {
            try {
                int success = response.getInt("success");
                if (success == 1) {

                    // if we're lucky or good boy, we receive our response in a json array
                    JSONArray ja = response.getJSONArray("my_testmyapp");

                    if (writeTimeStamp) {
                        JSONObject jsonObject = ja.getJSONObject(ja.length() - 1);
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putLong(TIMESTAMP, jsonObject.getLong(ITEM_TIMESTAMP));
                        editor.apply();
                    } else
                        newEvent.setText(message);

                    // iterate the array and for each object we get our json
                    for (int i = 0; i < ja.length(); i++) {

                        JSONObject jobj = ja.getJSONObject(i);

                        // each json contain attribute, so we get and save them in a map
                        HashMap<String, String> item = new HashMap<>();
                        item.put(ITEM_ID, jobj.getString(ITEM_ID));
                        item.put(ITEM_STATION, jobj.getString(ITEM_STATION));
                        itemList.add(item);

                    } // for loop ends

                    String[] from = {ITEM_ID, ITEM_STATION};
                    int[] to = {R.id.item_id, R.id.item_station};

                    adapter = new SimpleAdapter(
                            getApplicationContext(), itemList,
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
            NotificationChannel mChannel = Objects.requireNonNull(notificationManager).getNotificationChannel(id);

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
            Intent modifyIntent = new Intent(ReadData.this,
                    EventDetails.class);
            modifyIntent.putExtra("item", itemList.get(position));
            startActivity(modifyIntent);

        }

    }
}
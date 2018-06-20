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

    public static final String ITEM_ID = "id";
    public static final String ITEM_STATION = "station";
    public static final String ITEM_TIMESTAMP = "timestamp";
    public static final String mypreference = "mypref";
    public static final String TimeStamp = "timStampKey";
    public static final String FirstRun = "firstRun";
    private static final String TAG = "CyberDemon Logging";    // JSON Node names
    String item_name;
    // our php files
    String url = "http://testmyapp.altervista.org/read.php";
    String url_check = "http://testmyapp.altervista.org/check.php";
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
    // flag for new items
    boolean newItem = false;
    // Shared Preferences elements
    private long parsedTimestamp;
    // notification object
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);

        // check if our sharedPreferences are empty or not
        //INITIALIZE START
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        if (sharedpreferences.contains(TimeStamp)) {
            sharedpreferences.getLong(TimeStamp, 0L);
        } else
            Log.e(TAG, "empty shared preference");

        if (sharedpreferences.contains(FirstRun)) {
            sharedpreferences.getBoolean(FirstRun, true);
        } else
            Log.e(TAG, "empty shared preference");
        //INITIALIZE END

        Item_List = new ArrayList<>();
        Initial_Item_Num = new ArrayList<>();

        listview = findViewById(R.id.listview_01);
        newEvent = findViewById(R.id.new_event);
        updateButton = findViewById(R.id.update);

        updateButton.setOnClickListener(v -> {
            ReadDataFromDB();
          /*  Intent refresh = new Intent(ReadData.this,
                    ReadData.class);
            startActivity(refresh);*/
        });

        //FIRST RUN CHECK
        if (isFirstRun()) {
            // read the db data and fill the ui
            ReadDataFromDB();
            // toast, maybe we are angry
            Toast.makeText(getApplicationContext(), "FIRST RUN", Toast.LENGTH_SHORT);
        } else {
            // check the timestamp
            check();
            // newItem -> true mean new items
            if (newItem) {
                // update UI and save the new timestamp
                ReadDataFromDB();
                // get the new timestamp and set it into sharedPreferences
                updateTimestamp();
            } else {
                // content is updated, local saved timestmap is up to date
                Toast.makeText(getApplicationContext(), "Content Updated", Toast.LENGTH_LONG).show();
                setFirstRun(false);
            }
        }
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

    // SharedPreferences Methods START
    public void setTimeStamp(Long newTimestamp) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putLong(TimeStamp, newTimestamp);
        editor.apply();
    }

    public void setFirstRun(Boolean runOrNot) {
        SharedPreferences.Editor editor = sharedpreferences.edit();

        if (runOrNot) {
            editor.putBoolean(FirstRun, true);
            editor.apply();
        } else {
            editor.putBoolean(FirstRun, false);
            editor.apply();
        }

    }

    public Boolean isFirstRun() {
        Boolean runOrNOt = true;
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains(FirstRun))
            if(!sharedpreferences.getBoolean(FirstRun, false))
                runOrNOt = false;
            else
                runOrNOt = true;

        return runOrNOt;
    }
    // SharedPreferences Methods END

    // retrieve only the last timestamp from db and write it into sharedPreferences
    private void updateTimestamp() {
        PD = new ProgressDialog(this);
        PD.setMessage("Parsing Timestamp.....");
        PD.show();

        JsonObjectRequest jreq = new JsonObjectRequest(Method.GET, url, response -> {
            try {
                int success = response.getInt("success");

                if (success == 1) { // well done
                    JSONArray ja = response.getJSONArray("my_testmyapp");
                    JSONObject jobj = ja.getJSONObject(ja.length() - 1);
                    parsedTimestamp = jobj.getLong(ITEM_TIMESTAMP);
                    setTimeStamp(parsedTimestamp);
                    PD.dismiss();

                } // if ends

            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }

        }, error -> PD.dismiss());
        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(jreq);
    }

    private void ReadDataFromDB() {
        PD = new ProgressDialog(this);
        PD.setMessage("Loading.....");
        PD.show();

        JsonObjectRequest jreq = new JsonObjectRequest(Method.GET, url, response -> {
            try {
                int success = response.getInt("success");

                if (success == 1) {
                    JSONArray ja = response.getJSONArray("my_testmyapp");

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

    // make db request with our saved timestamp,
    // if response is 1 we have new events
    public void check() {
        item_name = String.valueOf(getTimeStamp());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url_check, response -> {
            newItem = true; // so we have news today
                    Toast.makeText(getApplicationContext(),
                            "Data Inserted Successfully",
                            Toast.LENGTH_SHORT).show();

                }, error -> {
            newItem = false; // boring day
            Toast.makeText(getApplicationContext(),
                    "failed to insert", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("item_name", item_name);
                return params;
            }
        };

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(postRequest);
    }

    //On List Item Click move to Details Activity
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



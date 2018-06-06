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
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ReadData extends Activity {

    // JSON Node names
    public static final String ITEM_ID = "id";
    public static final String ITEM_STATION = "station";
    public static final String ITEM_TIMESTAMP = "timestamp";
    // our php file to query the db
    String url = "http://testmyapp.altervista.org/read.php";
    ArrayList<HashMap<String, String>> Item_List;
    ProgressDialog PD;
    ListAdapter adapter;
    ListView listview = null;
    ArrayList<HashMap<String, Integer>> Initial_Item_Num;
    TextView newEvent;
    ImageButton updateButton;
    private long actualTimestamp = System.currentTimeMillis() / 1000;
    private long lastTimestamp;
    private boolean check4NewEvent = false;
    // NOTIFICATION
    private NotificationManager notifManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);

        Item_List = new ArrayList<>();
        Initial_Item_Num = new ArrayList<>();

        listview = findViewById(R.id.listview_01);
        newEvent = findViewById(R.id.new_event);
        updateButton = findViewById(R.id.update);


        // query remote DB
        ReadDataFromDB();

        // maybe we have another row? // this is java 8
        check4NewEvent = lastTimestamp > actualTimestamp;

        // check if events counted and saved in ROM DB are >=< versus remote DB
        updateButton.setOnClickListener(v -> {
            if (check4NewEvent)
                createNotification("new event" + " ACTUAL: " + actualTimestamp + " LAST:" + lastTimestamp);
            else
                createNotification("UPDATED" + " ACTUAL: " + actualTimestamp + " LAST:" + lastTimestamp);
        });
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

                        lastTimestamp = jobj.getLong(ITEM_TIMESTAMP);

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
                e.printStackTrace();
            }

        }, error -> PD.dismiss());

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(jreq);

    }

    // Check android version and produce a notification with a simple message using
    //  different libs if we're on Oreo device.
    public void createNotification(String aMessage) {
        final int NOTIFY_ID = 1002;

        // There are hardcoding only for show it's just strings
        String name = "my_package_channel";
        String id = "my_package_channel_1"; // The user-visible name of the channel.
        String description = "my_package_first_channel"; // The user-visible description of the channel.

        Intent intent;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;

        if (notifManager == null) {
            notifManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, name, importance);
                mChannel.setDescription(description);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
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
        } // else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }

    //On List Item Click move to Details Activity
    class ListitemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Intent modify_intent = new Intent(ReadData.this,
                    UpdateDeleteData.class);

            modify_intent.putExtra("item", Item_List.get(position));

            startActivity(modify_intent);

        }

    }

}



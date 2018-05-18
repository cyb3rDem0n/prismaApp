package project.prisma.starnotifier;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ReadData extends AppCompatActivity {

    String url = "http://testmyapp.altervista.org/read.php";
    ArrayList<HashMap<String, String>> Item_List;
    ProgressDialog PD;
    ListAdapter adapter;
    String val_norify;
    ListView listview = null;

    // JSON Node names
    public static final String ITEM_ID = "id";
    public static final String ITEM_STATION = "station";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.read);

        listview = (ListView) findViewById(R.id.listview_01);
        Item_List = new ArrayList<HashMap<String, String>>();

        ReadDataFromDB();
    }
    private void ReadDataFromDB() {
        PD = new ProgressDialog(this);
        PD.setMessage("Loading.....");
        PD.show();

        JsonObjectRequest jreq = new JsonObjectRequest(Method.GET, url, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int success = response.getInt("success");

                            if (success == 1) {
                                JSONArray ja = response.getJSONArray("my_testmyapp");

                                for (int i = 0; i < ja.length(); i++) {

                                    JSONObject jobj = ja.getJSONObject(i);
                                    HashMap<String, String> item = new HashMap<String, String>();
                                    item.put(ITEM_ID, jobj.getString(ITEM_ID));
                                    item.put(ITEM_STATION,
                                            jobj.getString(ITEM_STATION));

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

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToReqQueue(jreq);

    }


    //On List Item Click move to UpdateDelete Activity
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

    public void addData(View view) {

        Intent add_intent = new Intent(ReadData.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(add_intent);
    }
}



package project.prisma.starnotifier;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static android.support.v4.app.NotificationCompat.*;


public class MainActivity extends AppCompatActivity {

    ConnectionClass dbTest = new ConnectionClass();
    String output;
    ConnectionClass connectionClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView textView = findViewById(R.id.showevent);
        connectionClass = new ConnectionClass();

        try {
            Connection con = connectionClass.CONN();
            if (con == null) {
                String z = "Please check your internet connection";
            } else {

                String query = "select * from evento";
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next())

                {
                    output= resultSet.getString(2);
                    textView.append("DATE: "+output + "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void notification(View view)
    {
        addNotification();
    }

    private void addNotification()
    {
        //Todo: On notification click it should show the details activity. By cyberdemon
        Builder builder =
                new Builder(this)
                        .setSmallIcon(R.drawable.message)
                        .setContentTitle("Unread Message")   //this is the title of notification
                        .setContentText("You have an unread message.");   //this is the message showed in notification

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
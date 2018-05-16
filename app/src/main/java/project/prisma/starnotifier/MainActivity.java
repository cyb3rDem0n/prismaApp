package project.prisma.starnotifier;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static android.support.v4.app.NotificationCompat.*;

// Todo: the connection JDBC class should return with a main method the data that we need...
public class MainActivity extends AppCompatActivity {

    //ConnectionClass dbTest = new ConnectionClass();
    String eventData;
    int eventId;
    //ConnectionClass connectionClass;
    TextView textView, textView0;  // textView0 is the textView inside the ScrollView - textView is the number of events shown

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.showevent);
        textView0 = findViewById(R.id.ev0);

        //connectionClass = new ConnectionClass();

        try
        {
            ConnectionJDBC connectionJDBC = new ConnectionJDBC();

            Connection conn = connectionJDBC.getMySqlConnection();
            String query = "select * from evento";
            Statement statement = conn.createStatement();

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next())

            {
                eventId = resultSet.getInt(1);
                eventData = resultSet.getString(2);

                System.out.print("--------- "+ eventData + " -----------");
                System.out.print("--------- "+ eventId + " -----------");

                textView0.setText(eventData);

            }

            /* You can use the connection object to do any insert, delete, query or update action to the mysql server.*/

            /* Do not forget to close the database connection after use, this can release the database connection.*/
            conn.close();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }


    /*
    This function when executed by the onclick, perform a SELECT query on db and make two operation:
    1) Append the the data obtained from db in a textView inside a scrollList
    2) Put +1 in the main textView of the main activity xml
     */
    public void updateStatus(View v){
            textView.setText(eventId);

    }

    private void addNotification(String content)
    {
        content = eventData;
        //Todo: On notification click it should show the details activity. By cyberdemon
        Builder builder =
                new Builder(this)
                        .setSmallIcon(R.drawable.message)
                        .setContentTitle("New Event Happen")   //this is the title of notification
                        .setContentText(content);   //this is the message showed in notification

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
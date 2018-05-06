package project.prisma.starnotifier;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


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
}
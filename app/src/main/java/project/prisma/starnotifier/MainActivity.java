/*
 *  Created by cyb3rDem0n (Giuseppe D'Agostino) on 01/06/18 15.26
 *  Copyright (c) 2018 . All rights reserved.
 *  Email dagostinogiuseppe@outlook.com
 *  Last modified 01/06/18 15.25
 */

package project.prisma.starnotifier;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class  MainActivity extends Activity {

    private static final String DATABASE_NAME = "userdata_db";
    private DataBase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataBase = Room.databaseBuilder(getApplicationContext(), DataBase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                UserData userData = new UserData();
//                userData.setUserId(99);
//                userData.setUsername("appUsername");
//                userData.setPassword("appUserPassword");
//
//                dataBase.daoAccess () . insertOnlySingleUserData (userData);
//            }
//        }) .start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                UserData userData_ = dataBase.daoAccess () . fetchOneUserDatabyUserId (99);
                System.out.print("--------------->>> "+ userData_.getUsername() + " <<<------------");

            }
        }) .start();
    }

    public void read(View v) {
        Intent read_intent = new Intent(MainActivity.this, ReadData.class);
        startActivity(read_intent);
    }
}
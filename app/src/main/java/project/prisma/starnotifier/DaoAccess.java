/*
 *  Created by cyb3rDem0n (Giuseppe D'Agostino) on 01/06/18 15.34
 *  Copyright (c) 2018 . All rights reserved.
 *  Email dagostinogiuseppe@outlook.com
 *  Last modified 01/06/18 15.34
 */

package project.prisma.starnotifier;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface DaoAccess {
    @Insert
    void insertOnlySingleUserData(UserData userData);

    @Insert
    void insertMultipleUserData(List<UserData> userDataList);

    @Query("SELECT * FROM UserData WHERE userId = :userId")
    UserData fetchOneUserDatabyUserId(int userId);

    //TODO: select a user from DB to get his ID

    @Update
    void updateUserData(UserData userData);

    @Delete
    void deleteUserData(UserData userData);



}

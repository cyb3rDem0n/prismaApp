/*
 *  Created by cyb3rDem0n (Giuseppe D'Agostino) on 01/06/18 15.51
 *  Copyright (c) 2018 . All rights reserved.
 *  Email dagostinogiuseppe@outlook.com
 *  Last modified 01/06/18 15.51
 */

package project.prisma.starnotifier;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {UserData.class}, version = 2, exportSchema = false)
public abstract class DataBase extends RoomDatabase {
    public abstract DaoAccess daoAccess();
}

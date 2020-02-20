package com.daesy.taskmaster;

import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Task.class}, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}


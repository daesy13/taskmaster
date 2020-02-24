package com.daesy.taskmaster;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.daesy.taskmaster.models.Task;

@Database(entities = {Task.class}, exportSchema = false, version = 1)
public abstract class MyDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}


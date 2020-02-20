package com.daesy.taskmaster;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task")
    List<Task> getTasks();

//    @Query("SELECT * FROM task WHERE id = :id")
//    Task getOne(long id);

    @Insert
    void save(Task newTask);

}

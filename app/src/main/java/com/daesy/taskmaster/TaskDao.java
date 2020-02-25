package com.daesy.taskmaster;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.daesy.taskmaster.models.Task;

import java.util.List;

@Dao
public interface TaskDao {
    @Query("SELECT * FROM task WHERE id = :id")
    public Task getOneTask(long id);

    @Query("SELECT * FROM task")
    List<Task> getAll();

    @Insert
    public void addTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Update
    void updateTask(Task task);

    @Query("DELETE FROM task")
    public void deleteAllTask();
}

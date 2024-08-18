package com.mobile2app.eventtracker.repo;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.mobile2app.eventtracker.model.Event;
import com.mobile2app.eventtracker.model.User;

/**
 * Identifies the class as a Room database and lists the tables
 * within it. Provides access points to interact with the Event and
 * User tables.
 */
@Database(entities = {Event.class, User.class}, version = 1)
public abstract class EventDatabase extends RoomDatabase {
    public abstract EventDao eventDao();
    public abstract UserDao userDao();
}
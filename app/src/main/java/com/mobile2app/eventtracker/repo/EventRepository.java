package com.mobile2app.eventtracker.repo;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.mobile2app.eventtracker.model.Event;
import com.mobile2app.eventtracker.model.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton class for handling interactions involving users and
 * events in a database.
 */
public class EventRepository {
    private static EventRepository mEventRepo;
    private final EventDao mEventDao;
    private final UserDao mUserDao;
    private static final int NUMBER_OF_THREADS = 4;
    // Runs database operations in the background
    private static final ExecutorService mDatabaseExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    // Method to get Singleton instance
    public static EventRepository getInstance(Context context) {
        if (mEventRepo == null) {
            mEventRepo = new EventRepository(context);
        }
        return mEventRepo;
    }

    // Sets up Room database and initializes the EventDao and UserDao objects
    private EventRepository(Context context) {
        RoomDatabase.Callback databaseCallback = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                // Include starter data when database is created
                mDatabaseExecutor.execute(() -> addStarterData());
            }
        };
        // Build database labeled event.db
        EventDatabase database = Room.databaseBuilder(context, EventDatabase.class, "event.db")
                .addCallback(databaseCallback)
                .build();

        mEventDao = database.eventDao();
        mUserDao = database.userDao();
    }

    // Add event to events table
    public void addEvent(Event event) {
        mDatabaseExecutor.execute(() -> mEventDao.addEvent(event));
    }
    // Update event in events table
    public void updateEvent(Event event) {
        mDatabaseExecutor.execute(() -> mEventDao.updateEvent(event));
    }
    // Delete event in events table
    public void deleteEvent(Event event) {
        mDatabaseExecutor.execute(() -> mEventDao.deleteEvent(event));
    }

    // Return all events in events table
    public LiveData<List<Event>> getEvents() {
        return mEventDao.getEvents();
    }

    // Place starter data inside events table
    private void addStarterData() {
        Event event = new Event("Project Three Deadline", "04/21/2024", "9:00PM");
        mEventDao.addEvent(event);

        event = new Event("Robotics Event", "04/22/2024", "1:00PM");
        mEventDao.addEvent(event);

        event = new Event("Career Fair", "04/23/2024", "10:30AM");
        mEventDao.addEvent(event);

        event = new Event("Drone Show", "04/24/2024", "12:00PM");
        mEventDao.addEvent(event);

        event = new Event("Tech Talk", "04/25/2024", "8:30AM");
        mEventDao.addEvent(event);

        event = new Event("Job Fair", "04/26/2024", "02:15PM");
        mEventDao.addEvent(event);

        event = new Event("Hackathon", "04/27/2024", "11:30AM");
        mEventDao.addEvent(event);

        event = new Event("AI Seminar", "04/28/2024", "03:45PM");
        mEventDao.addEvent(event);
    }

    // Get user by email from users table
    public User getUser(String email) {
        return mUserDao.getUser(email);
    }

    // Add user to users table
    public void addUser(User user) {
        mDatabaseExecutor.execute(() -> mUserDao.addUser(user));
    }
}

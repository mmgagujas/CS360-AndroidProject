package com.mobile2app.eventtracker.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mobile2app.eventtracker.model.Event;
import com.mobile2app.eventtracker.repo.EventRepository;
import java.util.List;

/**
 * Manages and prepares event data for the UI
 */
public class EventListViewModel extends AndroidViewModel {

    private final EventRepository mEventRepo;

    // Constructor that obtains Singleton instance of event repository
    public EventListViewModel(Application application) {
        super(application);
        mEventRepo = EventRepository.getInstance(application.getApplicationContext());
    }

    // Get LiveData of all events
    public LiveData<List<Event>> getEvents() {
        return mEventRepo.getEvents();
    }

    // Add event to database
    public void addEvent(Event event) {
        mEventRepo.addEvent(event);
    }

    // Update event in database
    public void updateEvent(Event event) {
        mEventRepo.updateEvent(event);
    }

    // Delete event in database
    public void deleteEvent(Event event) {
        mEventRepo.deleteEvent(event);
    }
}
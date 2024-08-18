package com.mobile2app.eventtracker.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mobile2app.eventtracker.model.User;
import com.mobile2app.eventtracker.repo.EventRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Manages and prepares user data for the UI
 */
public class UserViewModel extends AndroidViewModel {

    private final EventRepository mEventRepo;

    // Constructor that obtains Singleton instance of event repository
    public UserViewModel(Application application) {
        super(application);
        mEventRepo = EventRepository.getInstance(application.getApplicationContext());
    }

    // Get LiveData of a user by their email
    public LiveData<User> getUser(String email) {
        Executor myExecutor = Executors.newSingleThreadExecutor();
        MutableLiveData<User> userLiveData = new MutableLiveData<>();
        myExecutor.execute(() -> {
            User user = mEventRepo.getUser(email);
            userLiveData.postValue(user);
        });
        return userLiveData;
    }

    // Add user to the database
    public void addUser(User user) {
        mEventRepo.addUser(user);
    }
}
package com.mobile2app.eventtracker;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Primary Activity with a navigation bar for a user events, search events,
 * and settings fragment. Contains a floating action bar to transition to
 * event create activity.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize FAB and nav menu
        FloatingActionButton mFloatingButton = findViewById(R.id.floatingActionButton);
        BottomNavigationView navView = findViewById(R.id.bottom_navigation);

        // Get the NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        // Get the NavController from the NavHostFragment
        NavController navController = null;
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }

        // Setup the BottomNavigationView with the NavController
        if (navController != null) {
            NavigationUI.setupWithNavController(navView, navController);
        }

        // Add the destination changed listener
        if (navController != null) {
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                // Shows FAB on user's events screen
                if(destination.getId() == R.id.nav_user_events) {
                    mFloatingButton.show();
                } else {
                    mFloatingButton.hide();
                }
            });
        }

        // Switch screens to event create activity on FAB click
        mFloatingButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EventCreateActivity.class);
            startActivity(intent);
        });
    }
}
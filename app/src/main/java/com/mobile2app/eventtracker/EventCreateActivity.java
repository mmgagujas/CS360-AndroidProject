package com.mobile2app.eventtracker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.mobile2app.eventtracker.model.Event;
import com.mobile2app.eventtracker.viewmodel.EventListViewModel;

import java.util.Calendar;

/**
 * Activity for creating events and providing details like title,
 * date, and time. Allows users to update an events date and time with
 * DatePicker and TimePicker widgets.
 */
public class EventCreateActivity extends AppCompatActivity {

    private EventListViewModel mEventListViewModel;
    private EditText mEventTitle;
    private TextView mEventDate;
    private TextView mEventTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_create);

        // Adjust layout based on system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.event_create_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mEventTitle = findViewById(R.id.event_title);
        mEventDate = findViewById(R.id.event_date);
        mEventTime = findViewById(R.id.event_time);
        Button mCreateButton = findViewById(R.id.create_button);

        // Get the EventListViewModel instance
        mEventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);

        // Listener for date picker dialog to display the month/date of an event
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
            String month = monthOfYear < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
            String day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
            String date = month + "/" + day + "/" + year;
            mEventDate.setText(date);
        };
        mEventDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            // Create a new date picker dialog with the current date as the default date
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    EventCreateActivity.this,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Listener for time picker dialog to display the time of an event
        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minutes) -> {
            String AM_PM = hourOfDay < 13 ? "AM" : "PM";
            int hour = hourOfDay < 13 ? hourOfDay : (hourOfDay - 12);
            String min = minutes < 10 ? ("0" + minutes) : String.valueOf(minutes);
            String time = hour + ":" + min + AM_PM;
            mEventTime.setText(time);
        };

        mEventTime.setOnClickListener(v -> {
            // Get current time
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            // Create a new instance of TimePickerDialog and return it
            TimePickerDialog timePickerDialog = new TimePickerDialog(EventCreateActivity.this, timeSetListener, hour, minute, DateFormat.is24HourFormat(EventCreateActivity.this));
            timePickerDialog.show();
        });

        // Creates event with user inputs
        mCreateButton.setOnClickListener(view -> {
            String title = mEventTitle.getText().toString();
            String date = mEventDate.getText().toString();
            String time = mEventTime.getText().toString();
            // Check that title is provided
            if (!title.isEmpty()) {
                Event newEvent = new Event (title, date, time);
                mEventListViewModel.addEvent(newEvent);
                Intent intent = new Intent(EventCreateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
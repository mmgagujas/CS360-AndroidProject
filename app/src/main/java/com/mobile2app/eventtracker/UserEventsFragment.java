package com.mobile2app.eventtracker;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobile2app.eventtracker.model.Event;
import com.mobile2app.eventtracker.viewmodel.EventListViewModel;
import com.mobile2app.eventtracker.viewmodel.UserViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

/**
 * Handles displaying and updating the events of a user in a grid layout.
 * Requests SMS permissions for notifying users of events on a certain day.
 */
public class UserEventsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private EventListViewModel mEventListViewModel;
    private final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;


    public UserEventsFragment() {
        // Empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_events, container, false);
        mEventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        mRecyclerView = view.findViewById(R.id.event_recycler_view);

        // Create a new GridLayoutManager with a single column
        RecyclerView.LayoutManager gridLayoutManager =
                new GridLayoutManager(getContext(), 1);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Place divider between events in list
        DividerItemDecoration divider = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(divider);

        // Call updateUI() when the event list changes
        mEventListViewModel.getEvents().observe(requireActivity(), this::updateUI);

        checkPermissions();
        return view;
    }

    // Check for permissions used in app
    private void checkPermissions() {
        // Check if the permission is already granted
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            showCustomPermissionScreen();
        } else {
            // Hard-coded number, associate with account phone number or user-input
            String phoneNumber = "15551234567";
            final String[] message = {"Your upcoming events:\n"};

            mEventListViewModel.getEvents().observe(requireActivity(), events -> {
                // Update the cached copy of the events in the adapter.
                for (Event event : events) {
                    message[0] += event.getEventTitle() + "(" + event.getEventDate().substring(0,5) + ")\n";
                }
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message[0], null, null);
            });
        }
    }

    // Sends confirmation text when permission is approved
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, the app can send SMS now
                String phoneNumber = "15551234567";
                String message = "Text notifications are now enabled.";
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }
        }
    }

    // Requests SMS permissions with custom layout
    private void showCustomPermissionScreen() {
        final Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.request_permission_layout);
        // Find approve/deny buttons in layout
        Button btnApprove = dialog.findViewById(R.id.grantPermissionButton);
        TextView btnDeny = dialog.findViewById(R.id.denyPermissionButton);

        // Set an onClick listener for the button
        btnApprove.setOnClickListener(v -> {
            dialog.setOnDismissListener(dialogInterface -> {
                // When the dialog is dismissed, request the permission
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
            });
            dialog.dismiss();
        });
        btnDeny.setOnClickListener(v -> {
            // Dismiss the dialog
            dialog.dismiss();
        });
        dialog.show();
    }

    // Updates the UI
    private void updateUI(List<Event> eventList) {
        EventAdapter mEventAdapter = new EventAdapter(eventList);
        mRecyclerView.setAdapter(mEventAdapter);
    }

    // Bridges data to be displayed in RecyclerView
    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {

        private final List<Event> mEventList;

        // Constructor
        public EventAdapter(List<Event> events) {
            mEventList = events;
        }

        @NonNull
        @Override
        public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflate the layout for the ViewHolder
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            return new EventHolder(layoutInflater, parent);
        }

        // Bind the event at the given position to the holder
        @Override
        public void onBindViewHolder(EventHolder holder, int position){
            holder.bind(mEventList.get(position));
        }

        // Return number of items in data set
        @Override
        public int getItemCount() {
            return mEventList.size();
        }

        //
        private class EventHolder extends RecyclerView.ViewHolder {

            private final TextView mEventTextView;
            private final TextView mEventDateView;
            private final TextView mEventTimeView;

            // References to the views for each item in RecyclerView
            public EventHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.recycler_view_items, parent, false));
                mEventTextView = itemView.findViewById(R.id.event_text_view);
                mEventDateView = itemView.findViewById(R.id.event_date_view);
                mEventTimeView = itemView.findViewById(R.id.event_time_view);
                ImageButton mDeleteButton = itemView.findViewById(R.id.delete_button);

                DatePickerDialog.OnDateSetListener dateSetListener = (view, year, monthOfYear, dayOfMonth) -> {
                    // Add leading zeros to single digits
                    String month = monthOfYear < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
                    String day = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);
                    String date = month + "/" + day;

                    // Get the position of the current item
                    int position = getAdapterPosition();
                    Event event = mEventList.get(position);
                    // Update event's date with DatePicker
                    event.setEventDate(date);
                    mEventListViewModel.updateEvent(event);
                };

                mEventDateView.setOnClickListener(v -> {
                    Calendar calendar = Calendar.getInstance();
                    // Set the current date as the default
                    DatePickerDialog datePickerDialog = new DatePickerDialog(
                            requireActivity(),
                            dateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));

                    datePickerDialog.show();
                });

                TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minutes) -> {
                    // Format time as string
                    String AM_PM = hourOfDay < 13 ? "AM" : "PM";
                    int hour = hourOfDay < 13 ? hourOfDay : (hourOfDay - 12);
                    String min = minutes < 10 ? ("0" + minutes) : String.valueOf(minutes);
                    String time = hour + ":" + min + AM_PM;

                    // Get position of current item
                    int position = getAdapterPosition();
                    Event event = mEventList.get(position);
                    // Update event's time with TimePicker
                    event.setEventTime(time);
                    mEventListViewModel.updateEvent(event);
                };

                mEventTimeView.setOnClickListener(v -> {
                    // Get current time
                    final Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    // Create a new instance of TimePickerDialog and return it
                    TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), timeSetListener, hour, minute, DateFormat.is24HourFormat(getActivity()));
                    timePickerDialog.show();
                });

                mDeleteButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    // Check position is valid
                    if (position != RecyclerView.NO_POSITION) {
                        Event event = mEventList.get(position);
                        mEventList.remove(position);
                        notifyItemRemoved(position);
                        // Delete event from database
                        mEventListViewModel.deleteEvent(event);
                    }
                });
            }

            // Bind data of event object to views
            public void bind(Event event) {
                mEventTextView.setText(event.getEventTitle());
                mEventDateView.setText(event.getEventDate().substring(0,5));
                mEventTimeView.setText(event.getEventTime());
            }
        }
    }
}
package com.mobile2app.eventtracker.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * User object that holds an id, email, salt, hashed password, and
 * phone number. Uses Room Persistence Library to interact with
 * SQLite table
 */
@Entity(indices = {@Index(value = {"email"}, unique = true)})
public class User {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private long mId;
    @NonNull
    @ColumnInfo(name = "email")
    private String mEmail;
    @ColumnInfo(name = "salt")
    private String mPasswordSalt;
    @ColumnInfo(name = "hash")
    private String mHashedPassword;
    @ColumnInfo(name = "phone_number")
    private String mPhoneNumber;

    // Constructor for Room
    public User() {}
    // Constructor assigning email, salt, hashed password, and emulator phone number
    public User(@NonNull String email, String salt, String hash) {
        mEmail = email;
        mPasswordSalt = salt;
        mHashedPassword = hash;
        // Default phone number for emulator
        mPhoneNumber = "15551234567";
    }

    // Get user Id
    public long getId() {
        return mId;
    }
    // Set user Id
    public void setId(long id) {
        mId = id;
    }

    // Get user email
    public String getEmail() { return mEmail; }
    // Set user email
    public void setEmail(String email) {
        mEmail = email;
    }

    // Get user's salt for password
    public String getPasswordSalt() { return mPasswordSalt; }
    // Set user's salt for password
    public void setPasswordSalt(String salt) {
        mPasswordSalt = salt;
    }

    // Get user's hashed password
    public String getHashedPassword() { return mHashedPassword; }
    // Set user's hashed password
    public void setHashedPassword(String password) {
        mHashedPassword = password;
    }

    // Get user phone number
    public String getPhoneNumber() { return mPhoneNumber; }
    // Set user phone number
    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }
}

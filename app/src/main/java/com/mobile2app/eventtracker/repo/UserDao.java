package com.mobile2app.eventtracker.repo;

import androidx.room.*;
import com.mobile2app.eventtracker.model.User;

/**
 * Data Access Object (DAO) for performing CRUD operations on users.
 *
 */
@Dao
public interface UserDao {
    // Return a user by email
    @Query("SELECT * FROM User WHERE email = :email")
    User getUser(String email);

    // Add a user
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long addUser(User user);

    // Update a user
    @Update
    void updateUser(User user);

    // Delete a user
    @Delete
    void deleteUser(User user);
}
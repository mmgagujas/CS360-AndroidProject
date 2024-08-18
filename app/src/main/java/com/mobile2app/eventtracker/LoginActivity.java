package com.mobile2app.eventtracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.mobile2app.eventtracker.model.User;
import com.mobile2app.eventtracker.utility.PasswordUtility;
import com.mobile2app.eventtracker.viewmodel.UserViewModel;

/**
 * Handles the Login and Sign Up process for users. Validates credentials
 * and stores the salt/hashed password of an account
 */
public class LoginActivity extends AppCompatActivity {

    private UserViewModel mUserViewModel;
    private EditText emailInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Adjust layout based on system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_screen), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Override auto-adjustment so status bar remains readable
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        emailInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.buttonLogin);
        Button signUpButton = findViewById(R.id.buttonSignUp);

        // Verifies login credentials from user input
        loginButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            isValidCredentials(email, password, isValid -> {
                if (isValid) {
                    // Handle valid credentials
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    // Handle invalid credentials
                    Snackbar.make(findViewById(R.id.login_screen), "Invalid username or password", Snackbar.LENGTH_SHORT).show();
                }
            });
            hideKeyboard(view);

        });

        // Creates user account from user input
        signUpButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString();
            String password = passwordInput.getText().toString();

            mUserViewModel.getUser(email).observe(LoginActivity.this, user -> {
                if (user == null && !email.isEmpty()) {
                    // Generate a salt
                    byte[] salt = PasswordUtility.createSalt();

                    // Hash the password with the salt
                    byte[] hashedPassword = PasswordUtility.hashPassword(password, salt);

                    // Convert hashed password and salt to strings for storage
                    String hashedPasswordStr = Base64.encodeToString(hashedPassword, Base64.DEFAULT);
                    String saltStr = Base64.encodeToString(salt, Base64.DEFAULT);

                    // Create a new user with the hashed password and salt
                    User newUser = new User(email, saltStr, hashedPasswordStr);
                    Snackbar.make(findViewById(R.id.login_screen), "Account Created", Snackbar.LENGTH_SHORT).show();

                    // Add the user to the database
                    mUserViewModel.addUser(newUser);
                } else {
                    // Notify email already exists in database
                    Snackbar.make(findViewById(R.id.login_screen), "Account already exists. Try another email.", Snackbar.LENGTH_SHORT).show();
                }
            });
            hideKeyboard(view);
        });
    }

    // Validates user credentials
    private void isValidCredentials(String email, String password, OnResultListener listener) {
        // Check database for user with email input
        mUserViewModel.getUser(email).observe(LoginActivity.this, user -> {
            if (user != null) {
                // Retrieve stored/hashed password from associated email
                String storedSaltStr = user.getPasswordSalt();
                String storedHashedPassword = user.getHashedPassword();
                byte[] storedSalt = Base64.decode(storedSaltStr, Base64.DEFAULT);
                byte[] hashedInputPassword = PasswordUtility.hashPassword(password, storedSalt);
                String hashedInputPasswordStr = Base64.encodeToString(hashedInputPassword, Base64.DEFAULT);
                // Compare stored and recently hashed password
                boolean isValid = storedHashedPassword.equals(hashedInputPasswordStr);
                listener.onResult(isValid);
            } else {
                listener.onResult(false);
            }
        });
    }

    // Interface for the result of the validation
    public interface OnResultListener {
        void onResult(boolean isValid);
    }

    // Hides device keyboard
    private void hideKeyboard(View view) {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            // Hide the soft keyboard using the window token from the view
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
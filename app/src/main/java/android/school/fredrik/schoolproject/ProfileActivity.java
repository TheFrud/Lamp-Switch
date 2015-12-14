package android.school.fredrik.schoolproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Makes it possible for the user to change his/her user info.
 * @author Fredrik Johansson
 * */
public class ProfileActivity extends AppCompatActivity {

    // xxx
    private User user = User.getINSTANCE();

    // xxx
    private ClientSideValidation validator = null;

    // UI references
    private EditText eMailView;
    private EditText passwordView;
    private View loginFormView;
    private View progressView;

    // LOG TAG
    private static final String TAG = ProfileActivity.class.getSimpleName();


    /**
     * Creates the activity, instantiates UI objects, and sets up click listeners.
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Start of onCreate method.");

        setContentView(R.layout.activity_profile);

        // So that we can navigate back to the login activity with the up arrow.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Instantiates UI elements.
        eMailView = (EditText) findViewById(R.id.userName);
        passwordView = (EditText) findViewById(R.id.userPassword);
        loginFormView = findViewById(R.id.save_profile_settings_form);
        progressView = findViewById(R.id.save_profile_settings_progress);

        // Sets the current user info in the form.
        eMailView.setText(user.getUserName(this));
        passwordView.setText(user.getUserPassword(this));

        Log.d(TAG, "End of onCreate method.");
    }

    /**
     * Note from Fredrik: I don't fully understand this code.. but it works.
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Called from view. Attempts to save new user info. Does client-side validation before sending user info to the server for server-side validation.
     * */
    public void saveProfileSettings(View view) {
        Log.d(TAG, "Save Profile Settings Method.");

        // Reset errors.
        eMailView.setError(null);
        passwordView.setError(null);

        // xxx
        View focusView = null;

        // Get user supplied values.
        String email = eMailView.getText().toString();
        String password = passwordView.getText().toString();

        validator = new ClientSideValidation(this);

        validator.checkValidity(email, password);
        boolean success = validator.getSuccess();

        // If client-side validation failed.
        if(!success){
            String message = validator.getMessage();

            if(message.equals(getString(R.string.error_invalid_password))){
                passwordView.setError(message);
                focusView = passwordView;
            }

            else if(message.equals(getString(R.string.error_field_required))){
                eMailView.setError(message);
                focusView = eMailView;
            }

            else if(message.equals(getString(R.string.error_invalid_email))){
                eMailView.setError(message);
                focusView = eMailView;
            }

            /* There was an error; don't attempt login and focus the first
            form field with an error.*/
            focusView.requestFocus();
        }

        else {
            // Show a progress spinner, and kick off a background task to
            // perform the user registration attempt.
            showProgress(true);
            new SaveProfileSettingsTask(email, password, this).execute((Void) null);;
        }

    }
    /**
     * This task is starting the server-side validation.
     * It makes a call to the server (using the User-class)
     * with the user info it got through the constructor.
     *
     * @author Fredrik Johansson
     * */
    public class SaveProfileSettingsTask extends AsyncTask<Void, Void, Boolean> {

        private final String name;
        private final String password;
        private final Context context;

        private boolean success;

        // The task get supplied the user supplied information in the constructor.
        SaveProfileSettingsTask(String name, String password, Context context) {
            this.name = name;
            this.password = password;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Using the user supplied information to make a attempt to change the user info.
            return user.saveProfileSettings(name, password, context);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            // If the server-side validation succeeded.
            if (success) {
                // The user gets sent to the MainActivity. (due to the success of the update)
                Intent intent = new Intent(ProfileActivity.this.getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
            // If the server-side validation failed.
            else {
                // The user gets notified about the failure of the attempt to change the user info.
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }


    }



}

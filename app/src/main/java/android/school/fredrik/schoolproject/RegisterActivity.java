/*
Copyright 2016 Fredrik Johansson

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package android.school.fredrik.schoolproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Handles the registration functionality.
 * @author Fredrik Johansson
 */
public class RegisterActivity extends AppCompatActivity{

    // Fetches the single existing instance of User
    private User user = User.getINSTANCE();

    // For client validation when registering
    private ClientSideValidation validator = null;

    // UI references.
    private EditText eMailView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;

    // LOG TAG
    private static final String TAG = RegisterActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Start of onCreate method.");

        setContentView(R.layout.activity_register);

        // So that we can navigate back to the login activity with the up arrow.
/*        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Instantiates UI elements.
        eMailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        // When the user clicks the registration button. A attempt will be made to log register the user.
        mEmailSignInButton.setOnClickListener(
                view -> attemptRegistration()
        );

        Log.d(TAG, "End of onCreate method.");
    }


    /**
     * Called from view. Attempts to register. Does client-side validation before sending user info to the server for server-side validation.
     * */
    private void attemptRegistration() {

        // Reset errors.
        eMailView.setError(null);
        passwordView.setError(null);

        // Get user supplied values.
        String email = eMailView.getText().toString();
        String password = passwordView.getText().toString();

        // The view to focus on if registration fails
        View focusView = null;

        // Instantiates a new Validator.
        // Used for client-side validation
        validator = new ClientSideValidation(this);

        // The Validator object gets called with the supplied data
        validator.checkValidity(email, password);

        // We check if the validation succeeded.
        boolean success = validator.getSuccess();

        // If client-side validation failed.
        if(!success){
/*          Because the validation failed,
            we are interested in what kind of error it was that caused the failure.
            That way, we can show the error message to the user.
            This will help him/her correct the mistake.*/

            // We get the message from the validator
            String message = validator.getMessage();

            // We check which error it was and show the appropriate message to the user.
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
            new UserRegisterTask(email, password, this).execute((Void) null);;
        }
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

    // THIS DOES NOT WORK RIGHT NOW (WIP)
/*    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Log.d(TAG, "Tried to navigate back");

                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);

                // finish();
                // NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    /**
     * This task is starting the server-side validation.
     * It makes a call to the server (using the User-class)
     * with the user info it got through the constructor.
     *
     * @author Fredrik Johansson
     * */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Context context;

        // The task get supplied the user supplied information in the constructor.
        UserRegisterTask(String email, String password, Context context) {
            mEmail = email;
            mPassword = password;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Using the user supplied information to make a login attempt.
            return user.register(mEmail, mPassword, context);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            showProgress(false);

            // If the server-side validation succeeded.
            if (success) {
                // The user gets sent to the LoginActivity. (due to the success of the registration)
                Intent intent = new Intent(RegisterActivity.this.getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }

            // If the server-side validation failed.
            else {
                // The user gets notified about the failure of the registration attempt.

                // This is not a optimal message.
                // We should get passed the message from the server.
                // However.. this is not implemented as of now.
                passwordView.setError(getString(R.string.server_error_generic_register));
                passwordView.requestFocus();
            }
        }


    }
}


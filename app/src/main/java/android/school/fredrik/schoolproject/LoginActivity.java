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
 * Handles the login logic and provides navigation to the register view.
 * @author Fredrik Johansson
 */
public class LoginActivity extends AppCompatActivity {

    // Fetches the single existing instance of User
    private User user = User.getINSTANCE();

    // For client validation when logging in
    private ClientSideValidation validator = null;

    // UI references.
    private EditText eMailView;
    private EditText passwordView;
    private View progressView;
    private View loginFormView;

    // LOG TAG
    private static final String TAG = LoginActivity.class.getSimpleName();

    /**
     * Creates the activity, instantiates UI objects, and sets up click listeners.
     * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Start of onCreate method.");

        setContentView(R.layout.activity_login);


        // Instantiates UI elements.
        eMailView = (EditText) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);
        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        Button navigateToActivityRegisterButton = (Button) findViewById(R.id.navigate_to_activity_register_button);

        // When the user clicks the login button. A attempt will be made to log the user in.
        mEmailSignInButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        attemptLogin();
                    }
                }
        );

        // When the user clicks the "register here" button. We will navigate the user to the RegisterActivity.
        navigateToActivityRegisterButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        navigateToActivityRegister();
                    }
                }
        );

        Log.d(TAG, "End of onCreate method.");
    }

    /**
     * Called from view. Attempts to login. Does client-side validation before sending user info to the server for server-side validation.
    * */
    private void attemptLogin() {

        // Reset errors.
        eMailView.setError(null);
        passwordView.setError(null);

        // The view to focus on if client-validation fails
        View focusView = null;

        // Get user supplied values.
        String email = eMailView.getText().toString();
        String password = passwordView.getText().toString();

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

        // If client-side validation succeeded.
        else {
/*          Show a progress spinner, and kick off a background task to
            perform the user login attempt.*/
            showProgress(true);

            //// Server-side validation ////

            // If the user got through client-side validation an asynchronous task will be started to attempt server side validation.
            new UserLoginTask(email, password, this).execute((Void) null);;
        }
    }

    /**
     * Called from view. When the user clicks the "register here" button, we will start the RegisterActivity.
     * */
    private void navigateToActivityRegister() {
        Intent intent = new Intent(LoginActivity.this.getApplicationContext(), RegisterActivity.class);
        startActivity(intent);
        finish();
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
     * This task is starting the server-side validation.
     * It makes a call to the server (using the User-class)
     * with the user info it got through the constructor.
     *
     * @author Fredrik Johansson
     * */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final Context context;

        // The task get supplied the user supplied information in the constructor.
        UserLoginTask(String email, String password, Context context) {
            mEmail = email;
            mPassword = password;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Using the user supplied information to make a login attempt.
            return user.login(mEmail, mPassword, LoginActivity.this);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            // Hides "spinner".
            showProgress(false);

            // If the server-side validation succeeded.
            if (success) {
                // The user gets sent to the MainActivity. (due to the success of the login)
                Log.d(TAG, "Login: Success.");
                Intent intent = new Intent(LoginActivity.this.getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }

            // If the server-side validation failed.
            else {
                // The user gets notified about the failure of the login attempt.
                Log.d(TAG, "Login: Failed.");

                // This is not a optimal message.
                // We should get passed the message from the server.
                // However.. this is not implemented as of now.
                passwordView.setError(getString(R.string.server_error_generic_login));
                passwordView.requestFocus();
            }
        }


    }
}


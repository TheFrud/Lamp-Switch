package android.school.fredrik.schoolproject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class ProfileActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private SaveProfileSettingsTask task = null;

    private User user = User.getINSTANCE();

    // UI references
    private EditText userName;
    private EditText userPassword;
    private View mLoginFormView; //Ändra namn !!!!!!!!
    private View mProgressView; //Ändra namn !!!!!!!!

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userName = (EditText) findViewById(R.id.userName);
        userPassword = (EditText) findViewById(R.id.userPassword);

        userName.setText(user.getUserName(this));
        userPassword.setText(user.getUserPassword(this));

        // TA BORT???
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mLoginFormView = findViewById(R.id.save_profile_settings_form);
        mProgressView = findViewById(R.id.save_profile_settings_progress);

    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public void saveProfileSettings(View view) {
        System.out.println("Save Profile Settings Method.");

        // Reads the data from the fields.
        String name = userName.getText().toString();
        String password = userPassword.getText().toString();

        showProgress(true);
        task = new SaveProfileSettingsTask(name, password, this);
        task.execute((Void) null);

    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class SaveProfileSettingsTask extends AsyncTask<Void, Void, Boolean> {

        private final String name;
        private final String password;
        private final Context context;

        private boolean success;

        SaveProfileSettingsTask(String name, String password, Context context) {
            this.name = name;
            this.password = password;
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return user.saveProfileSettings(name, password, context);
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            task = null;
            showProgress(false);

            if (success) {
                Intent intent = new Intent(ProfileActivity.this.getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                userPassword.setError(getString(R.string.error_incorrect_password));
                userPassword.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            task = null;
            showProgress(false);
        }


    }



}

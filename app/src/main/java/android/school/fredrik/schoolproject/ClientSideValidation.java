package android.school.fredrik.schoolproject;

/**
 * Created by Fredrik on 14-Dec-15.
 */

import android.content.Context;
import android.text.TextUtils;

/**
 * Handles client side validation.
 * @author Fredrik Johansson
 * */
public class ClientSideValidation {

    private boolean success = false;
    private String message = null;
    private Context context = null;

    public ClientSideValidation(Context context) {
        this.context = context;
    }

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    /**
     * xxxxxxxxxx
     *
     * */
    public void checkValidity(String email, String password){
        if(!isEmailValid(email)){
            success = false;
            message = context.getString(R.string.error_invalid_email);
        }
        else if(TextUtils.isEmpty(email)){
            success = false;
            message = context.getString(R.string.error_field_required);
        }
        else if(!isPasswordValid(password) && TextUtils.isEmpty(password)){
            success = false;
            message = context.getString(R.string.error_invalid_password);
        }
        else {
            success = true;
        }
    }

    /**
     * Checks the validity of the supplied email adress.
     * Will fail if it doesn't contain "@" or is longer than 254 characters (apparently the max length of email adresses).
     * */
    private boolean isEmailValid(String email) {
        return email.contains("@") && email.length() < 254;
    }

    /**
     * Checks the validity of the supplied password.
     * Will fail if shorter than 4 characters.
     * */
    private boolean isPasswordValid(String password) {
        if(password.length() < 4){
            return false;
        }
        return true;
    }
}

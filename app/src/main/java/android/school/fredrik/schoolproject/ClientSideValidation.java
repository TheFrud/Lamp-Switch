package android.school.fredrik.schoolproject;

/**
 * Created by Fredrik on 14-Dec-15.
 */

import android.content.Context;
import android.text.TextUtils;

import org.apache.commons.validator.routines.EmailValidator;

/**
 * Handles client side validation.
 * @author Fredrik Johansson
 * */
public class ClientSideValidation {

    private boolean success = false;
    private String message = null;
    private Context context = null;
    private static final int MAX_EMAIL_LENGTH = 254;
    private static final int MIN_PASSWORD_LENGTH = 4;

    private EmailValidator emailValidator = EmailValidator.getInstance();

    public ClientSideValidation(Context context) {
        this.context = context;
    }

    /**
     * Makes it possible for the caller to check if the validation succeded.
     * */
    public boolean getSuccess() {
        return success;
    }

    /**
     * This is called IF the validation FAILED.
     * Makes it possible for the caller to show the user what went wrong.
     * */
    public String getMessage() {
        return message;
    }

    /**
     * Checks the validity of the user supplied data.
     * Checks if: <u>
     *     <l>The email adress is valid (contains "@", shorter than {@value #MAX_EMAIL_LENGTH} characters)
     *     (It also has to be valid according to the EmailValidator in Apache Commons. I kept my own checker for fun...)
     *     <l>That the email field isn't empty.
     *     <l>That the password is longer than {@value #MIN_PASSWORD_LENGTH} characters.
     *     <l>That the password field isn't empty.
     * </u>
     * */
    public void checkValidity(String email, String password){
        if(!isEmailValid(email) || emailValidator.isValid(email)){
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
     * Will fail if it doesn't contain "@" or is longer than {@value #MAX_EMAIL_LENGTH} characters (apparently the max length of email adresses).
     * */
    private boolean isEmailValid(String email) {
        return email.contains("@") && email.length() < MAX_EMAIL_LENGTH;
    }

    /**
     * Checks the validity of the supplied password.
     * Will fail if shorter than {@value #MIN_PASSWORD_LENGTH} characters.
     * */
    private boolean isPasswordValid(String password) {
        if(password.length() < MIN_PASSWORD_LENGTH){
            return false;
        }
        return true;
    }
}

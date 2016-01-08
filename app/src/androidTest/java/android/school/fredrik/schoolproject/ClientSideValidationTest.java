package android.school.fredrik.schoolproject;

import android.content.Context;
import android.test.AndroidTestCase;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Testing client-side validation.
 * @author Fredrik Johansson
 * */
public class ClientSideValidationTest extends AndroidTestCase {

    private Context context;
    private String validEmail = "slowdive@live.com";
    private String validPassword = "abcd";
    private String invalidEmail = "rogga.se";
    private String invalidPassword = "abc";

    @Override
    public void setUp() throws Exception {
        super.setUp();
        context = getContext();
    }

    /**
     * Checks that the client-side validation works as intended.
     */
    public void testCheckValidity(){
        ClientSideValidation validator;

        // Valid email/valid password
        validator = new ClientSideValidation(context);
        validator.checkValidity(validEmail, validPassword);
        boolean shouldBeValid = validator.getSuccess();
        assertTrue(shouldBeValid);

        // Valid email/Invalid password
        validator = new ClientSideValidation(context);
        validator.checkValidity(validEmail, invalidPassword);
        boolean shouldBeInvalid = validator.getSuccess();
        assertFalse(shouldBeInvalid);

        // Invalid email/Valid password
        validator = new ClientSideValidation(context);
        validator.checkValidity(invalidEmail, validPassword);
        shouldBeInvalid = validator.getSuccess();
        assertFalse(shouldBeInvalid);

        // Invalid email/Invalid password
        validator = new ClientSideValidation(context);
        validator.checkValidity(invalidEmail, invalidPassword);
        shouldBeInvalid = validator.getSuccess();
        assertFalse(shouldBeInvalid);
    }
}
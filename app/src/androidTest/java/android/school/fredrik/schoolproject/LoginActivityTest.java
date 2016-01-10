package android.school.fredrik.schoolproject;

import android.test.ActivityInstrumentationTestCase2;

/**
 * xxx
 * @author Fredrik Johansson
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity loginActivity;

    public LoginActivityTest(Class<LoginActivity> activityClass) {
        super(activityClass);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        loginActivity = getActivity();



    }
}

package android.school.fredrik.schoolproject;

import android.test.AndroidTestCase;

import junit.framework.TestCase;

/**
 * Tests if internet is available.
 * @author Fredrik Johansson
 * */
public class InternetCheckerTest extends AndroidTestCase {

    private InternetChecker internetChecker;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        internetChecker = new InternetChecker();
    }

    /**
     * Checks internet connection.
     * @throws InterruptedException
     */
    public void testIsInternetAvailable() throws InterruptedException {
        internetChecker.checkInternetConnection();

        Thread.sleep(3000);

        boolean isInternetAvailable= internetChecker.isInternetAvailable();
        assertTrue(isInternetAvailable);
    }
}
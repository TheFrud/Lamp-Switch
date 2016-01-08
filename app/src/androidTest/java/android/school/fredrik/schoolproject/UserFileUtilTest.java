package android.school.fredrik.schoolproject;

import android.content.Context;
import android.test.AndroidTestCase;

import junit.framework.TestCase;

/**
 * Tests file functionality (write and read).
 * Created by Fredrik on 08-Jan-16.
 */
public class UserFileUtilTest extends AndroidTestCase {

    private UserFileUtil userFileUtil;
    private Context context;
    private int dummyUserId;
    private String dummyUserName;
    private String dummyUserPassword;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        userFileUtil = new UserFileUtil();
        context = getContext();
        dummyUserId = 100;
        dummyUserName = "Dummy Usersson";
        dummyUserPassword = "dummypassword";
    }

    /**
     * Tests if user info can be written to file.
     */
    public void testSaveUserDataOnFile(){
        boolean saved = userFileUtil.saveUserDataOnFile(dummyUserId, dummyUserName, dummyUserPassword, context);
        assertTrue(saved);
    }

    /**
     * Tests if user info can be read from file.
     */
    public void testReadFromFile(){
        String retrievedUserId = userFileUtil.readFromFile("userId", context);
        String retrievedUserName = userFileUtil.readFromFile("userName", context);
        String retrievedUserPassword = userFileUtil.readFromFile("userPassword", context);

        System.out.println(retrievedUserId);
        System.out.println(retrievedUserName);
        System.out.println(retrievedUserPassword);

        boolean correctData = false;
        if(retrievedUserId.equals(String.valueOf(retrievedUserId)) && retrievedUserName.equals(dummyUserName) && retrievedUserPassword.equals(dummyUserPassword)){
            correctData = true;
        }

        assertTrue(correctData);
    }

}
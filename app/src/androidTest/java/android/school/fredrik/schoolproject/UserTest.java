package android.school.fredrik.schoolproject;

import android.content.Context;
import android.test.AndroidTestCase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Fredrik on 08-Dec-15.
 */
public class UserTest extends AndroidTestCase {

    private User user;
    private Context context;
    private String standardLogin;
    private String standardPassword;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        user = User.getINSTANCE();
        context = getContext();
        standardLogin = context.getResources().getString(R.string.standard_login_name);
        standardPassword = context.getResources().getString(R.string.standard_login_password);

    }

    public void testLogin(){
        boolean success = user.login(standardLogin, standardPassword, context);
        assertTrue(success);
    }

    public void testGetUsers() throws InterruptedException {
        List<JSONObject> users = new ArrayList<>();
        user.getUsers(users, context);

        // Wait for network
        Thread.sleep(3000);

        assertTrue(users.size() > 0);
    }

    // Essentially tests the getters which are implemented which a sort of cache
    public void testCache() throws InterruptedException {

        // This is usually set after login.
        user.setUserId(Integer.parseInt(context.getResources().getString(R.string.standard_login_id)));

        // Will make a network call due to statechanged being TRUE
        user.getUserName(context);

        // Wait for network call do finish (async call)
        // Otherwise always null
        // Time to sleep will of course change if the network is slow/fast
        // Not a good solution. But it wil do for now...
        Thread.sleep(3000);

        String userName = user.getUserName(context);
        assertNotNull(userName);

        userName = null;

        // Statechanged will now be FALSE.
        // No network call will be made.
        // Getting locally saved name
        userName = user.getUserName(context);
        assertNotNull(userName);
        System.out.println(userName);

    }

    public void testSaveProfileSettings(){
        boolean success = user.saveProfileSettings(standardLogin, standardPassword, context);
        assert(success);
    }
}

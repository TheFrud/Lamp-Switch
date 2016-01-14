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

import android.content.Context;
import android.test.AndroidTestCase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Tests for the UserTest.
 * Essentially integration testing (with the server as a "dependency")
 * @author Fredrik Johansson
 * */
public class UserTest extends AndroidTestCase {

    private User user;
    private Context context;

    /*
    Declaring variables to be used in tests.
     */
    private String standardLogin;
    private String standardPassword;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        user = User.getINSTANCE();
        context = getContext();

        /*
        Initialising test data.
         */
        standardLogin = context.getResources().getString(R.string.standard_login_name);
        standardPassword = context.getResources().getString(R.string.standard_login_password);

    }

    /**
     * Tests login functionality with valid input data
     */
    public void testLogin(){
        boolean success = user.login(standardLogin, standardPassword, context);
        assertTrue(success);
    }

    /**
     * Essentially tests the getters which are implemented which a sort of cache
     */
    public void testCache() throws InterruptedException {

        // This is usually set after login.
        user.setUserId(Integer.parseInt(context.getResources().getString(R.string.standard_login_id)));
        user.setStateChanged(true);

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

    /**
     * Tests if we can save user settings.
     */
    public void testSaveProfileSettings(){
        boolean success = user.saveProfileSettings(standardLogin, standardPassword, context);
        assert(success);
    }
}
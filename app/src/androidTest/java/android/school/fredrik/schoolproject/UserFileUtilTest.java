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
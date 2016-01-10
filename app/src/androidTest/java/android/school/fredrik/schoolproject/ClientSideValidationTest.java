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
package android.school.fredrik.schoolproject;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Class description
 * @author Fredrik Johansson
 */
public class UserFileUtil {

    // LOG TAG
    private static final String TAG = UserFileUtil.class.getSimpleName();

    /**
     *  xxx
     *
     * */
    public static boolean saveUserDataOnFile(int userId, String userName, String userPassword, Context context){
        String FILENAME = context.getResources().getString(R.string.user_info);

        FileOutputStream fos = null;
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("user_info.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(String.valueOf(userId));
            outputStreamWriter.write("\n");
            outputStreamWriter.write(userName);
            outputStreamWriter.write("\n");
            outputStreamWriter.write(userPassword);
            outputStreamWriter.write("\n");
            outputStreamWriter.close();
            Log.d(TAG, "User data saved to file.");
            return true;

        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    /**
     *  xxx
     *
     * */
    public static String readFromFile(String attribute, Context context) {

        try {
            String FILENAME = context.getResources().getString(R.string.user_info);
            InputStream inputStream = context.openFileInput(FILENAME);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                List<String> info = new ArrayList<>();


                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    info.add(receiveString);
                }

                inputStream.close();

                // Reading data from file.
                String userId = info.get(0);
                String userName = info.get(1);
                String userPassword = info.get(2);

                // Checking attribute argument to determine what attribute to return to caller
                if(attribute.equals("userId")){
                    return userId;
                } else if(attribute.equals("userName")){
                    return userName;
                } else if(attribute.equals("userPassword")){
                    return userPassword;
                }

            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return null;
    }
}

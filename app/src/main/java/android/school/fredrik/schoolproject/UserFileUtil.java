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
 * Is meant to be used as a kind of fail safe if the user's internet connection goes down when using the app.
 * It has two responsibilities: WRITING user info to file, READING user info from file.
 * @author Fredrik Johansson
 */
public class UserFileUtil {

    // LOG TAG
    private static final String TAG = UserFileUtil.class.getSimpleName();

    /**
     *  Gets called when a server call that changed the user info on the server SUCCEDED.
     *  Makes it possible to retrieve the user info even when the internet connection goes down.
     * */
    public static boolean saveUserDataOnFile(int userId, String userName, String userPassword, Context context){
        try {

            // Retrieve the name of the file that should be written to.
            String FILENAME = context.getResources().getString(R.string.user_info);

            // Instantiates the OutputStreamWriter, so that we can write to file.
            // The file to write to is passed into the constructor.
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FILENAME, Context.MODE_PRIVATE));

            // Writes the user info (to file) that was supplied as arguments to the method.
            // Could have used a loop but I think this is easier to read in this context.
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
     *  Gets called if these two conditions are true: 1. The user state on the server has changed, 2. The internet connection is down.
     *  Retrieves the locally saved user info.
     * */
    public static String readFromFile(String attribute, Context context) {

        try {
            // Retrieve the name of the file that should be read from.
            String FILENAME = context.getResources().getString(R.string.user_info);

            // Sets up the InputStream object with the file to read from.
            InputStream inputStream = context.openFileInput(FILENAME);

            if ( inputStream != null ) {

                // Instantiates the InputStreamReader, so that we can read from the input stream.
                // The inputStream is passed into the constructor.
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                // Instantiates the InputStreamReader, so that we can read from the file, line by line.
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                // Will contain the current line when the file content is read line by line.
                String receiveString = "";

                // The user info that is read from the file will be added to this list.
                // This could have been solved in a more elegant way (not using a list), but this will suffice for now...
                List<String> info = new ArrayList<>();

                // While there is a new line -> Add it to the list.
                // Will loop three times.
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    info.add(receiveString);
                }

                inputStream.close();

                // Reading data that was added to the list.
                // As I wrote earlier, not the most elegant solution.
                String userId = info.get(0);
                String userName = info.get(1);
                String userPassword = info.get(2);

                // Checking attribute argument to determine what attribute to return to caller.
                // This could also have been solved better. One drawback is that is hard to debug
                // if the caller gets a character wrong or something like that.
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

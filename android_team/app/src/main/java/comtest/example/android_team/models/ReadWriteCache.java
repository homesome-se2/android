package comtest.example.android_team.models;

import android.content.Context;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;



public class ReadWriteCache {

    private Context context;
    private static final String FILENAME = "logInCache";
    private static final String TAG = "Info";


    public ReadWriteCache(Context context) {
        this.context = context;
    }

    public void writeToCache(final String fileContents) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "Writing to Cache");
                try {
                    File cacheFile = new File(context.getCacheDir(),FILENAME);
                    FileOutputStream fos = new FileOutputStream(cacheFile);
                    fos.write(fileContents.getBytes());
                    fos.flush();
                    fos.close();
                    Log.i(TAG, "Writing done!");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String readFromCache(){
        Log.i(TAG, "Reading from Cache");
        try {
            File cacheFile = new File(context.getCacheDir(),FILENAME);
            FileInputStream fis = new FileInputStream(cacheFile);
            byte[] cache = new byte[(int) cacheFile.length()];
            fis.read(cache);
            fis.close();
            String str = new String(cache);
            Log.i(TAG, str);
            Log.i(TAG, "Reading done!");
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public boolean cacheFileExist(){
        File cacheFile = new File(context.getCacheDir(),FILENAME);
        return cacheFile.exists();
    }

    public boolean deleteCacheFile(){
        File cacheFile = new File(context.getCacheDir(),FILENAME);
        if (cacheFile.exists()) {
            cacheFile.delete();
            return true;
        }else {
            return false;
        }
    }


}

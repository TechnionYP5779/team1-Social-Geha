package com.example.ofir.social_geha;

import android.content.Context;
import android.util.Log;

import java.io.File;


// This class contains static methods which allow deletion of app related caches an memory
public class AppStorageManipulation {

    // This function deletes all of the user data from the phone
    public static void deleteAppData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("DELETE_USER", "File /data/data/APP_PACKAGE/" + s +" DELETED");
                }
            }
        }
    }

    // This function deletes a given directory from the user's phone
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

}

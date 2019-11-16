package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;

public class FileUtils extends Object {
    private static String BASE_PATH = Environment.getExternalStorageDirectory()+"DCIM/ItemFactory";
    public static String getFilePath(String filename){
        File dir = new File(BASE_PATH);
        if (!dir.exists()){
            dir.mkdirs();
        }
        return BASE_PATH+"/"+filename;
    }

    public static Uri getFileUri(Context context, String fileName){
        String filePath = getFilePath(fileName);
        if (Build.VERSION.SDK_INT >= 24) {
            return FileProvider.getUriForFile(context, context.getPackageName()+".fileprovider", new File(filePath));
        } else {
            return Uri.fromFile(new File(filePath));
        }

    }
}

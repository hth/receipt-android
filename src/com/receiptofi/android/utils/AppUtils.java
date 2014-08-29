package com.receiptofi.android.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

public class AppUtils {
	
	static File image;
	
	public static String getImageFileFromURI(Context context,Uri uri){
		String[] filePathColoumn = { MediaStore.Images.Media.DATA };
		Cursor c = context.getContentResolver().query(uri,
				filePathColoumn, null, null, null);
		c.moveToFirst();
		final String imageAbsolutePath = c.getString(c
				.getColumnIndex(filePathColoumn[0]));
		return imageAbsolutePath;
	}
	
	
	public static File createImageFile() {
	    // Create an image file name
		try {
				String timeStamp = new SimpleDateFormat("dd_MM_yyyy_HHmmss").format(new Date());
			    String imageFileName = "Receipt_android_" + timeStamp + "_";
			    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			    image = File.createTempFile(imageFileName,".jpg",storageDir);

			    // Save a file: path for use with ACTION_VIEW intents
			    String  mCurrentPhotoPath = "file:" + image.getAbsolutePath();

			    return image;
		} catch (Exception e) {
			// TODO: handle exception
				if(image!=null && image.exists()){
					image.delete();
					image=null;
				}
			 	return null;
		}
	  
	}
	

	public static String getImageFilePath() {
		if(image!=null){
			return image.getAbsolutePath();
		}else {
			return null;
		}
	}
}

package com.receiptofi.android.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class AppUtils {
	
	public static String getImageFileFromURI(Context context,Uri uri){
		String[] filePathColoumn = { MediaStore.Images.Media.DATA };
		Cursor c = context.getContentResolver().query(uri,
				filePathColoumn, null, null, null);
		c.moveToFirst();
		final String imageAbsolutePath = c.getString(c
				.getColumnIndex(filePathColoumn[0]));
		return imageAbsolutePath;
	}
}

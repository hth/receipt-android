package com.receiptofi.android.models;

import java.util.ArrayList;

import android.R.bool;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;

import com.receiptofi.android.ReceiptofiApplication;

public class ImageModel {

	public String blobId;
	public String imgPath;
	public String imgDate;
	public String imgStatus;
	public boolean LOCK=false;
	public boolean isTriedForUpload=false;
	public Thread uploaderThread ;

	public static final class STATUS {
		public static final String PROCESSED = "P";
		public static final String UNPROCESSED = "U";
	}

	public boolean addToQueue() throws SQLiteConstraintException{
		
		long responseCode =-1;
		
		ContentValues values = new ContentValues();
		values.put(ReceiptDB.UploadQueue.IMAGE_DATE, imgDate);
		values.put(ReceiptDB.UploadQueue.IMAGE_PATH, imgPath);
		values.put(ReceiptDB.UploadQueue.STATUS,STATUS.UNPROCESSED);

		responseCode=ReceiptofiApplication.rdh.getWritableDatabase().insertOrThrow(ReceiptDB.UploadQueue.TABLE_NAME, null, values);
		
		if(responseCode!=-1){
			return true;
		}else {
			return false;
		}
		
	}

	public void deleteFromQueue() {
		ReceiptofiApplication.rdh.getWritableDatabase().delete(ReceiptDB.UploadQueue.TABLE_NAME, ReceiptDB.UploadQueue.IMAGE_PATH+"=?", new String[]{imgPath});
	}
	
	public synchronized boolean updateStatus(boolean isProcessed) {
		
		ContentValues uploadValues = new ContentValues();
		
		uploadValues.put(ReceiptDB.UploadQueue.IMAGE_DATE, imgDate);
		uploadValues.put(ReceiptDB.UploadQueue.IMAGE_PATH, imgPath);
	
		
		if(isProcessed){
			imgStatus=STATUS.PROCESSED;
			uploadValues.put(ReceiptDB.UploadQueue.STATUS,STATUS.PROCESSED);
		}else {
			imgStatus=STATUS.UNPROCESSED;
			uploadValues.put(ReceiptDB.UploadQueue.STATUS,STATUS.UNPROCESSED);
		}

		ReceiptofiApplication.rdh.getWritableDatabase().update(ReceiptDB.UploadQueue.TABLE_NAME, uploadValues, ReceiptDB.UploadQueue.IMAGE_PATH+"=?", new String[]{imgPath});
		

		ContentValues imageIndexValue = new ContentValues();

		imageIndexValue.put(ReceiptDB.ImageIndex.IMAGE_PATH, imgPath);
		imageIndexValue.put(ReceiptDB.ImageIndex.BLOB_ID, blobId);
		
		ReceiptofiApplication.rdh.getWritableDatabase().insert(ReceiptDB.ImageIndex.TABLE_NAME, null, imageIndexValue);
		
		if(isProcessed){
			deleteFromQueue();
		}
		
		return true;
	}
	
	public static ArrayList<ImageModel> getAllUnprocessedImages(){
		
		ArrayList<ImageModel> models= new ArrayList<ImageModel>();
		Cursor c= ReceiptofiApplication.rdh.getReadableDatabase().query(ReceiptDB.UploadQueue.TABLE_NAME, new String[]{ReceiptDB.UploadQueue.IMAGE_PATH}, ReceiptDB.UploadQueue.STATUS+"=null or "+ReceiptDB.UploadQueue.STATUS+"='"+STATUS.UNPROCESSED+"'", null, null, null, null);
		if(c!=null && c.getCount()>0){
			c.moveToFirst();
			ImageModel model= new ImageModel();
			model.imgPath=c.getString(0);
			models.add(model);
		}
		return models;
	}

}


package com.receiptofi.android;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.receiptofi.android.adapters.ImageUpload;
import com.receiptofi.android.fragments.ReceiptListFragment;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.utils.AppUtils;

public class HomePageActivity extends ParentActivity {

	private static final int RESULT_IMAGE_GALLERY = 0x4c5;
	private static final int RESULT_IMAGE_CAPTURE = 0x4c6;
	
	TextView unprocessDoucumentCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);
		AppUtils.setHomePageContext(this);
		
		unprocessDoucumentCount=(TextView)findViewById(R.id.unprocessDoucumentCount);
	}

	public void invokeMenu(View view) {
		startActivity(new Intent(this, SettingsPage.class));
		this.overridePendingTransition(R.anim.left_slide_in, R.anim.right_side_out);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		addToBackStack(this);
	}
	
	public void takePhoto(View view){
		
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File photoFile = AppUtils.createImageFile();
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
	    		startActivityForResult(takePictureIntent, RESULT_IMAGE_CAPTURE);
	        }else {
	        	showErrorMsg("some error occured !!");
			}
	}
	
	public void chooseImage(View view){
		
		Intent g = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);	
		startActivityForResult(g, RESULT_IMAGE_GALLERY);
	}
	
	public void invokeReceiptList(View view) {
		 View container=findViewById(R.id.leftSidePane);
		 startFragment(new ReceiptListFragment(), true,container.getId());
	}

	public void onBackPressed(View view) {
		onBackPressed();
	}
	
	public void updatUnprocessCount(final int count) {
		uiThread.post(new Runnable() {

			@Override
			public void run() {
				unprocessDoucumentCount.setText("UNPROCESSED COUNT: "+count);
			}

		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode== RESULT_IMAGE_GALLERY && resultCode==RESULT_OK && data!=null){

			Uri imageGallery = data.getData();
			String imageAbsolutePath = AppUtils.getImageFileFromURI(this,imageGallery);
//			HTTPUtils.uploadImage(this,API.UPLOAD_IMAGE_API,imageAbsolutePath);
			ImageUpload.process(this, imageAbsolutePath);
		} else if (requestCode == RESULT_IMAGE_CAPTURE	&& resultCode == RESULT_OK) {

			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			
			String captiredImgFile= AppUtils.getImageFilePath();
			if (captiredImgFile!= null) {

				File capturedFile = new File(captiredImgFile);
				Uri contentUri = Uri.fromFile(capturedFile);
				mediaScanIntent.setData(contentUri);
				this.sendBroadcast(mediaScanIntent);

				// HTTPUtils.uploadImage(this,API.UPLOAD_IMAGE_API,captiredImgFile);
				ImageUpload.process(this, captiredImgFile);
			}
		}
		
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AppUtils.setHomePageContext(null);
	}

}

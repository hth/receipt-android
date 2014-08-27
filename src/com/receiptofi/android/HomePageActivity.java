package com.receiptofi.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.view.View;

import com.receiptofi.android.fragments.ReceiptListFragment;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.utils.AppUtils;

public class HomePageActivity extends ParentActivity {

	private static final int RESULT_IMAGE_GALLERY = 0x4c5;
	private static final int RESULT_IMAGE_CAPTURE = 0x4c6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);
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
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, RESULT_IMAGE_CAPTURE);
	
	}
	
	public void chooseImage(View view){
		Intent g = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);	
		startActivityForResult(g, RESULT_IMAGE_GALLERY);
	}
	
	public void invokeReceiptList(View view) {
		 View container=findViewById(R.id.leftSidePane);
		 startFragment(new ReceiptListFragment(), true,container.getId());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode== RESULT_IMAGE_GALLERY && resultCode==RESULT_OK && data!=null){
			
			Uri imageGallery = data.getData();
			final String imageAbsolutePath = AppUtils.getImageFileFromURI(this, imageGallery);
			try {
				new Thread(){
					public void run() {
						try {
						String str = HTTPUtils.uploadImage(API.UPLOAD_IMAGE_API, imageAbsolutePath);
						showErrorMsg(str);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (requestCode == RESULT_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
			Bitmap photo = (Bitmap) data.getExtras().get("data");
			String url = Images.Media.insertImage(getContentResolver(), photo, "receipt_" + calender.getTimeInMillis(), null);
			Uri uri = Uri.parse(url);
			final String imageAbsolutePath = AppUtils.getImageFileFromURI(this, uri);
			try {
				new Thread(){
					public void run() {
						try {
						String str=	HTTPUtils.uploadImage(API.UPLOAD_IMAGE_API, imageAbsolutePath);
						showErrorMsg(str);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}.start();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
}

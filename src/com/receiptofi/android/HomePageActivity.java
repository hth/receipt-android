package com.receiptofi.android;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.receiptofi.android.http.API;
import com.receiptofi.android.http.HTTPUtils;

public class HomePageActivity extends ParentActivity {

	private static final int RESULT_IMAGE_GALLARY = 0x4c5;
	private static final int RESULT_IMAGE_CAPTURE = 0x4c6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.homepage);
	}

	public void invokeMenu(View view) {
		startActivity(new Intent(this, SettingsPage.class));
		this.overridePendingTransition(R.anim.left_slide_in, R.anim.right_side_out);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		addTobackStack(this);
	}
	
	public void takePhoto(View view){
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivity(intent);
	}
	
	public void chooseImage(View view){
		Intent g = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);	
		startActivityForResult(g, RESULT_IMAGE_GALLARY);
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_IMAGE_GALLARY && resultCode == RESULT_OK && data != null) {

            Uri imageGallery = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(imageGallery, filePathColumn, null, null, null);
            c.moveToFirst();
            final String imageAbsolutePath = c.getString(c.getColumnIndex(filePathColumn[0]));
            try {
                new Thread() {
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
        }
    }

}

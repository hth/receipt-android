package com.receiptofi.android;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

public class HomePageActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
    }

    public void invokeMenu(View view) {
        startActivity(new Intent(this, MenuPageActivity.class));
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        addTobackStack(this);
    }

    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);

    }

    public void chooseImage(View view) {
        Intent g = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivity(g);
    }
}

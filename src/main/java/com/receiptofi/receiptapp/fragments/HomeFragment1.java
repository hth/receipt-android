package com.receiptofi.receiptapp.fragments;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.receiptapp.MainMaterialDrawerActivity;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.adapters.ImageUpload;
import com.receiptofi.receiptapp.utils.AppUtils;

import java.io.File;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment1 extends Fragment implements View.OnClickListener {
    private static  final String TAG = HomeFragment1.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int RESULT_IMAGE_GALLERY = 0x4c5;
    private static final int RESULT_IMAGE_CAPTURE = 0x4c6;

    public static final int IMAGE_UPLOAD_SUCCESS = 0x2563;
    public static final int IMAGE_ALREADY_QUEUED = 0x2564;
    public static final int IMAGE_ADDED_TO_QUEUED = 0x2565;
    public static final int IMAGE_UPLOAD_FAILURE = 0x2566;
    public static final int UPDATE_UNPROCESSED_COUNT = 0x2567;
    public static final int UPDATE_MONTHLY_EXPENSE = 0x2568;
    public static final int UPDATE_EXP_BY_BIZ_CHART = 0x2569;
    public static final int GET_ALL_RECEIPTS = 0x2570;
    private SuperActivityToast uploadImageToast;

    private String unprocessedValue;
    //UI variables
private  LinearLayout llTakePhoto,llChoosePhoto;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case IMAGE_UPLOAD_SUCCESS:
                    unprocessedValue = Integer.toString(msg.arg1);
                    setUnprocessedCount();
                    showMessage((String) msg.obj, SuperToast.Background.BLUE);
                    if (getActivity() instanceof MainMaterialDrawerActivity) {
                        ((MainMaterialDrawerActivity) getActivity()).stopProgressToken();
                    }
                    break;
                case IMAGE_UPLOAD_FAILURE:
                    showMessage((String) msg.obj, SuperToast.Background.RED);
                    if (getActivity() instanceof MainMaterialDrawerActivity) {
                        ((MainMaterialDrawerActivity) getActivity()).stopProgressToken();
                    }
                    break;
                case IMAGE_ADDED_TO_QUEUED:
                    if (getActivity() instanceof MainMaterialDrawerActivity) {
                        ((MainMaterialDrawerActivity) getActivity()).stopProgressToken();
                    }
                    showMessage((String) msg.obj, SuperToast.Background.BLUE);
                    break;
                case IMAGE_ALREADY_QUEUED:
                    if (getActivity() instanceof MainMaterialDrawerActivity) {
                        ((MainMaterialDrawerActivity) getActivity()).stopProgressToken();
                    }
                    showMessage((String) msg.obj, SuperToast.Background.GRAY);
                    break;
                case UPDATE_UNPROCESSED_COUNT:
                    unprocessedValue = (String) msg.obj;
                    setUnprocessedCount();
                   /* if (mPtrFrameLayout != null) {
                        mPtrFrameLayout.refreshComplete();
                    }*/
                    break;
                case UPDATE_MONTHLY_EXPENSE:
                   // currentMonthExpValue = (String) msg.obj;
                   // setMonthlyExpense();
                    break;
                case UPDATE_EXP_BY_BIZ_CHART:
                   // expByBizAnimate = true;
                    // We must add below condition to avoid getActivity be null exception.
                    if (isAdded()) {
                       //  updateChartData();
                    }
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + msg.what);
            }
            return true;
        }
    });

    public HomeFragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment1 newInstance(String param1, String param2) {
        HomeFragment1 fragment = new HomeFragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home1,container,false);
        llTakePhoto = (LinearLayout)view.findViewById(R.id.ll_take_photo);
        llChoosePhoto = (LinearLayout)view.findViewById(R.id.ll_choose_photo);


        llTakePhoto.setOnClickListener(this);
        llChoosePhoto.setOnClickListener(this);
        return view;
    }



    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.ll_take_photo:
                takePhoto();
                break;
            case R.id.ll_choose_photo:
                chooseImage();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_IMAGE_GALLERY && resultCode == RESULT_OK && null != data) {
            Uri imageGallery = data.getData();
            String imageAbsolutePath = AppUtils.getImageFileFromURI(getActivity(), imageGallery);
            if (null != imageAbsolutePath) {
                File pickerImage = new File(imageAbsolutePath);
                if ((pickerImage.length() / 1048576) >= 10) {
                    showToastMsg("Image size of more than 10MB not supported.",
                            SuperToast.Background.RED,
                            SuperToast.Duration.MEDIUM);
                } else {
                    startProgressToken();
                    ImageUpload.process(getActivity(), imageAbsolutePath);
                }
            }

        }

        else if (requestCode == RESULT_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            String capturedImgFile = AppUtils.getImageFilePath();
            if (null != capturedImgFile) {

                File capturedFile = new File(capturedImgFile);
                Uri contentUri = Uri.fromFile(capturedFile);
                mediaScanIntent.setData(contentUri);
                getActivity().sendBroadcast(mediaScanIntent);

                startProgressToken();
                ImageUpload.process(getActivity(), capturedImgFile);
            }
        }

        else if (resultCode == RESULT_CANCELED) {
            String capturedImgFile = AppUtils.getImageFilePath();
            if (null != capturedImgFile) {
                File capturedFile = new File(capturedImgFile);
                if (capturedFile.exists()) {
                    boolean deleteStatus = capturedFile.delete();
                    Log.d(TAG, "Cancelled, deleted=" + deleteStatus + " file=" + capturedImgFile);
                } else {
                    Log.d(TAG, "Cancelled, does not exists file=" + capturedImgFile);
                }
            }
        }
    }

    public void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = AppUtils.createImageFile();
        // Continue only if the File was successfully created
        if (null != photoFile) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            startActivityForResult(takePictureIntent, RESULT_IMAGE_CAPTURE);
        } else {
            showMessage("We seemed to have encountered issue saving Receipt image.", SuperToast.Background.RED);
        }
    }


    public void chooseImage() {
        Intent g = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(g, RESULT_IMAGE_GALLERY);
    }

    private void showMessage(final String message, final int backgroundColor) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (null != getActivity()) {
            /** getMainLooper() function of Looper class, which will provide you the Looper against the Main UI thread. */
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
                    superActivityToast.setText(message);
                    superActivityToast.setDuration(SuperToast.Duration.SHORT);
                    superActivityToast.setBackground(backgroundColor);
                    superActivityToast.setTextColor(Color.WHITE);
                    superActivityToast.setTouchToDismiss(true);
                    superActivityToast.show();
                }
            });
        }
    }

    public void showToastMsg(String msg, int backgroundColor, int duration) {
        SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
        superActivityToast.setText(msg);
        superActivityToast.setDuration(duration);
        superActivityToast.setBackground(backgroundColor);
        superActivityToast.setTextColor(Color.WHITE);
        superActivityToast.setTouchToDismiss(true);
        superActivityToast.show();
    }

    private void startProgressToken() {
        uploadImageToast = new SuperActivityToast(getActivity(), SuperToast.Type.PROGRESS);
        uploadImageToast.setText("Image Uploading.");
        uploadImageToast.setIndeterminate(true);
        uploadImageToast.setProgressIndeterminate(true);
        uploadImageToast.show();
    }

    private void setUnprocessedCount() {
        Log.d(TAG, "executing setUnprocessedCount");
        Activity activity = getActivity();
        if (activity != null) {
          //  unprocessedDocumentCount.setText(getString(R.string.processing_info, unprocessedValue));
        } else {
            Log.d(TAG, "setUnprocessedCount the response of getActivity() is null.");
        }
    }
}

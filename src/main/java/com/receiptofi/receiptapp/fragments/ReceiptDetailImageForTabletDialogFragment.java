package com.receiptofi.receiptapp.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.utils.Constants;
import com.receiptofi.receiptapp.views.TouchImageView;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReceiptDetailImageForTabletDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReceiptDetailImageForTabletDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptDetailImageForTabletDialogFragment extends DialogFragment {
    private static final String TAG = ReceiptDetailImageForTabletDialogFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mUrl = "";
    private View mView;
    private TouchImageView mReceiptImage;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private SuperActivityToast progressToast;
    private static boolean inShowingProgress = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiptDetailImageForTabletDialogFragment.
     */
    public static ReceiptDetailImageForTabletDialogFragment newInstance(String param1, String param2) {
        ReceiptDetailImageForTabletDialogFragment fragment = new ReceiptDetailImageForTabletDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ReceiptDetailImageForTabletDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mUrl = getArguments().getString(Constants.ARG_IMAGE_URL);
        }
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo);
    }

    @Override
    public void onStart() {
        super.onStart();

        /** Set the dialog size. */
        float scaleFactor = getActivity().getResources().getDisplayMetrics().density;
        float dialogWidth = getActivity().getResources().getDisplayMetrics().widthPixels / scaleFactor;
        float dialogHeight = getActivity().getResources().getDisplayMetrics().heightPixels / scaleFactor;
        getDialog().getWindow().setLayout(Math.round(dialogWidth), Math.round(dialogHeight));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_receipt_detail_image_for_tablet_dialog, container, false);

        // Setup auto dismiss my dialog if user pressed outside of the screen.
        getDialog().setCanceledOnTouchOutside(true);

        mReceiptImage = (TouchImageView) mView.findViewById(R.id.receiptImage);
        if (!TextUtils.isEmpty(mUrl)) {
            this.showProgressDialog();
            Picasso.Builder builder = new Picasso.Builder(getActivity()).indicatorsEnabled(true);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    Log.e(TAG, "failed to load image=" + e.getLocalizedMessage(), e);
                    if (null != getActivity()) {
                        SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
                        superActivityToast.setText(getResources().getString(R.string.receipt_image_failed_to_load));
                        superActivityToast.setDuration(SuperToast.Duration.MEDIUM);
                        superActivityToast.setBackground(SuperToast.Background.BLUE);
                        superActivityToast.setTextColor(Color.WHITE);
                        superActivityToast.setTouchToDismiss(true);
                        superActivityToast.show();

                        /** Popup previous detail stack since loading image has failed. */
                        getFragmentManager().popBackStack();
                    }
                }
            });
            builder.build().load(mUrl).into(mReceiptImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "on success");
                    mReceiptImage.setVisibility(View.VISIBLE);
                    progressToast.dismiss();
                    inShowingProgress = false;
                }

                @Override
                public void onError() {
                    Log.d(TAG, "on error");
                    progressToast.dismiss();
                    inShowingProgress = false;
                }
            });
        } else {
            SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
            superActivityToast.setText(getResources().getString(R.string.receipt_image_not_available));
            superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
            superActivityToast.setBackground(SuperToast.Background.BLUE);
            superActivityToast.setTextColor(Color.WHITE);
            superActivityToast.setTouchToDismiss(true);
            superActivityToast.show();
        }
        return mView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (progressToast != null && progressToast.isShowing()) {
            progressToast.dismiss();
            inShowingProgress = true;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void showProgressDialog() {
        progressToast = new SuperActivityToast(getActivity(), SuperToast.Type.PROGRESS);
        progressToast.setText("Downloading Image");
        progressToast.setIndeterminate(true);
        progressToast.setProgressIndeterminate(true);
        progressToast.show();
    }
}

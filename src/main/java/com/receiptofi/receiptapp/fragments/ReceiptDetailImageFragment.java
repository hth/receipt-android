package com.receiptofi.receiptapp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.receiptapp.BuildConfig;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.ReceiptListActivity;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.Constants;
import com.receiptofi.receiptapp.utils.OnSwipeTouchListener;
import com.receiptofi.receiptapp.views.TouchImageView;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReceiptDetailImageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReceiptDetailImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReceiptDetailImageFragment extends Fragment {
    private static final String TAG = ReceiptDetailImageFragment.class.getSimpleName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private String mUrl = "";
    private View mView;
    private TouchImageView mReceiptImage;
    private OnFragmentInteractionListener mListener;
    private SuperActivityToast superActivityProgressToast;
    private static boolean inShowingProgress = false;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiptDetailImageFragment.
     */
    public static ReceiptDetailImageFragment newInstance(String param1, String param2) {
        ReceiptDetailImageFragment fragment = new ReceiptDetailImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ReceiptDetailImageFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_receipt_detail_image, container, false);

        // Must call below method to make the fragment menu works.
        setHasOptionsMenu(true);

        mReceiptImage = (TouchImageView) mView.findViewById(R.id.receiptImage);
        mReceiptImage.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
                Log.d(TAG, "onSwipeTop");
            }

            public void onSwipeRight() {
                Log.d(TAG, "onSwipeRight");
            }

            public void onSwipeLeft() {
                getFragmentManager().popBackStack();
            }

            public void onSwipeBottom() {
                Log.d(TAG, "onSwipeBottom");
            }
        });

        boolean hasConnection = AppUtils.isNetworkConnectedOrConnecting(getActivity().getApplicationContext());
        if (!TextUtils.isEmpty(mUrl) && hasConnection) {
            this.showProgressDialog();
            Picasso.Builder builder = new Picasso.Builder(getActivity()).indicatorsEnabled(true);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception e) {
                    Log.e(TAG, "Failed to download image=" + e.getLocalizedMessage(), e);
                    if (null != getActivity()) {
                        SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
                        superActivityToast.setText(getActivity().getApplicationContext().getString(R.string.get_image_download_error) + " " + mUrl);
                        superActivityToast.setDuration(SuperToast.Duration.MEDIUM);
                        superActivityToast.setBackground(SuperToast.Background.RED);
                        superActivityToast.setTextColor(Color.WHITE);
                        superActivityToast.setAnimations(SuperToast.Animations.FLYIN);
                        superActivityToast.setTouchToDismiss(true);
                        superActivityToast.show();

                        /** Popup previous detail stack since loading image has failed. */
                        getFragmentManager().popBackStack();
                    } else {
                        Log.e(TAG, "getActivity() is null");
                    }
                }
            });
            builder.build().load(mUrl).into(mReceiptImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Successfully downloaded image from cloud " + mUrl);
                    mReceiptImage.setVisibility(View.VISIBLE);
                    superActivityProgressToast.dismiss();
                    inShowingProgress = false;
                }

                @Override
                public void onError() {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Error downloading image from cloud " + mUrl);
                    } else {
                        Log.e(TAG, "Error downloading image from cloud");
                    }
                    superActivityProgressToast.dismiss();
                    inShowingProgress = false;
                }
            });
        } else {
            if (null != getActivity()) {
                SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
                if (!hasConnection) {
                    Log.w(TAG, "No network available.");
                    superActivityToast.setText(getActivity().getApplicationContext().getString(R.string.no_network_available));
                    superActivityToast.setBackground(SuperToast.Background.RED);
                } else {
                    Log.w(TAG, "Receipt has no image.");
                    superActivityToast.setText(getActivity().getApplicationContext().getString(R.string.image_location_is_blank));
                    superActivityToast.setBackground(SuperToast.Background.BLUE);
                }
                superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
                superActivityToast.setTextColor(Color.WHITE);
                superActivityToast.setTouchToDismiss(true);
                superActivityToast.show();

                /** Popup previous detail stack since loading image has failed. */
                getFragmentManager().popBackStack();
            } else {
                Log.e(TAG, "getActivity() is null");
            }
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
            if (!(activity instanceof ReceiptListActivity)) {
                mListener = (OnFragmentInteractionListener) activity;
            }
        } catch (ClassCastException e) {
            Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
            /** Currently no interaction between fragments, so no need throw this exception. */
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                getFragmentManager().popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (inShowingProgress) {
            showProgressDialog();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (superActivityProgressToast != null && superActivityProgressToast.isShowing()) {
            superActivityProgressToast.dismiss();
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
        public void onFragmentInteraction(Uri uri);
    }

    private void showProgressDialog() {
        superActivityProgressToast = new SuperActivityToast(getActivity(), SuperToast.Type.PROGRESS);
        superActivityProgressToast.setText("Downloading Image");
        superActivityProgressToast.setIndeterminate(true);
        superActivityProgressToast.setProgressIndeterminate(true);
        superActivityProgressToast.show();
    }
}

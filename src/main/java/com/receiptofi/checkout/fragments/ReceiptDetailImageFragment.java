package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.utils.Constants;
import com.receiptofi.checkout.utils.OnSwipeTouchListener;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mUrl = "";
    private View mView;
    private ImageView mReceiptImage;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReceiptDetailImageFragment.
     */
    // TODO: Rename and change types and number of parameters
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView =  inflater.inflate(R.layout.fragment_receipt_detail_image, container, false);

        mReceiptImage = (ImageView) mView.findViewById(R.id.receiptImage);
        mReceiptImage.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
//                Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
//                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
//                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
                // Pop up self.
                getFragmentManager().popBackStack();
            }

            public void onSwipeBottom() {
//                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
            }
        });

        if (mUrl != "") {
            Picasso.Builder builder = new Picasso.Builder(getActivity()).indicatorsEnabled(true);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    exception.printStackTrace();
                }
            });
            builder.build().load(mUrl).placeholder(R.drawable.receipt_loading).into(mReceiptImage, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Log.d("TAG", "onsuccess");
                    mReceiptImage.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    Log.d("TAG", "onerror");
                }
            });

            SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
            superActivityToast.setText(mUrl);
            superActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
            superActivityToast.setBackground(SuperToast.Background.BLUE);
            superActivityToast.setTextColor(Color.WHITE);
            superActivityToast.setTouchToDismiss(true);
            superActivityToast.show();
        } else {
            SuperActivityToast superActivityToast = new SuperActivityToast(getActivity());
            superActivityToast.setText("No Receipt Image!");
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
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}

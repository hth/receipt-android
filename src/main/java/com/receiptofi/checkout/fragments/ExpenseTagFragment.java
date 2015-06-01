package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.gc.materialdesign.widgets.Dialog;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.adapters.ExpenseTagAdapter;
import com.receiptofi.checkout.adapters.TagListAdapter;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExpenseTagFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExpenseTagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExpenseTagFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GridView gridView;
    private List<ExpenseTagModel> tagModelList;
    private ExpenseTagAdapter mAdapter;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseTagFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseTagFragment newInstance(String param1, String param2) {
        ExpenseTagFragment fragment = new ExpenseTagFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ExpenseTagFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_expense_tag, container, false);
        gridView = (GridView)rootView.findViewById(R.id.gv_tag);
//        Map<String, ExpenseTagModel> expTagMap = ExpenseTagUtils.getExpenseTagModels();


        tagModelList = new LinkedList<>();

        for (int i = 0; i < 6; i ++) {
            ExpenseTagModel temp = new ExpenseTagModel("id" + i, "name" + i, "#795548");
            if (i == 1) {
                temp = new ExpenseTagModel("id" + i, "name" + i, "#795548");
            } else if (i == 2) {
                temp = new ExpenseTagModel("id" + i, "name" + i, "#FF5722");
            } else if (i == 3)  {
                temp = new ExpenseTagModel("id" + i, "name" + i, "#4CAF50");
            } else if (i == 4)  {
                temp = new ExpenseTagModel("id" + i, "name" + i, "#00BCD4");
            } else if (i == 5)  {
                temp = new ExpenseTagModel("id" + i, "name" + i, "#3F51B5");
            }
            tagModelList.add(temp);
        }

        mAdapter = new ExpenseTagAdapter(getActivity(), tagModelList);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Delivery delivery = null;
                String tag = "";
                delivery = PostOffice.newMail(getActivity())
                        .setTitle("Edit or Delete this tag:")
                        .setThemeColor(Color.BLUE)
                        .setDesign(Design.MATERIAL_LIGHT)
                        .showKeyboardOnDisplay(true)
                        .setButton(Dialog.BUTTON_POSITIVE, "Edit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton(Dialog.BUTTON_NEUTRAL, "Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .build();
                if(delivery != null)
                    delivery.show(getFragmentManager(), tag);
            }
        });
        return rootView;
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

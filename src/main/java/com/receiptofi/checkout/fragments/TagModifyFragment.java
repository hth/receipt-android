package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.r0adkll.postoffice.styles.EditTextStyle;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.adapters.TagListAdapter;
import com.receiptofi.checkout.model.Tag;
import com.shamanland.fab.FloatingActionButton;
import com.shamanland.fab.ShowHideOnScroll;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TagModifyFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TagModifyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TagModifyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public static LinkedList<Tag> mTagArrayList = new LinkedList<Tag>();
    private SwipeMenuListView mListView;
    private TagListAdapter mAdapter;

    private View view;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TagModifyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TagModifyFragment newInstance(String param1, String param2) {
        TagModifyFragment fragment = new TagModifyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public TagModifyFragment() {
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
        view =  inflater.inflate(R.layout.fragment_tag_modify, container, false);
        setupView();
        return view;
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

    private void setupView() {
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setSize(FloatingActionButton.SIZE_MINI);
        fab.setColor(Color.GREEN);
        // NOTE invoke this method after setting new values!
        fab.initBackground();
        // NOTE standard method of ImageView
        fab.setImageResource(R.drawable.ic_action_edit_dark);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Hi", Toast.LENGTH_SHORT).show();
                Delivery delivery = null;
                String tag = "";

                delivery = PostOffice.newMail(getActivity())
                        .setTitle("Please input your tag:")
                        .setThemeColor(Color.BLUE)
                        .setDesign(Design.MATERIAL_LIGHT)
                        .showKeyboardOnDisplay(true)
                        .setButton(Dialog.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
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
                        .setStyle(new EditTextStyle.Builder(getActivity())
                                .setHint("Tag:")
                                .setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                                .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                    @Override
                                    public void onAccepted(String text) {
                                        Toast.makeText(getActivity(), "TAG was accepted: " + text, Toast.LENGTH_SHORT).show();
                                        Tag test = new Tag(text, Color.RED);
                                        mTagArrayList.add(test);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }).build())
                        .build();
                if(delivery != null)
                    delivery.show(getFragmentManager(), tag);
            }
        });

        mListView = (SwipeMenuListView) view.findViewById(R.id.listView);
        mListView.setOnTouchListener(new ShowHideOnScroll(fab));
        Tag test = new Tag("CHECKING", Color.RED);
        Tag test1 = new Tag("HOME", Color.BLUE);
        Tag test2 = new Tag("PARA", getResources().getColor(R.color.blue));

        mTagArrayList.add(test);
        mTagArrayList.add(test1);
        mTagArrayList.add(test2);
        for (int i=0; i< 15; i++) {
            Tag testTemp;
            if (i%3 == 0)
                testTemp = new Tag("KK" + i, Color.RED);
            else if (i%3 == 1)
                testTemp = new Tag("KK" + i, Color.GREEN);
            else if (i%3 == 2)
                testTemp = new Tag("KK" + i, Color.BLUE);
            else
                testTemp = new Tag("KK" + i, Color.CYAN);
            mTagArrayList.add(testTemp);
        }
        mAdapter = new TagListAdapter(getActivity(), mTagArrayList);
//        lv.setAdapter(mAdapter);
        mListView.setAdapter(mAdapter);
        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set item title
                openItem.setTitle("Edit");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);
        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                Tag item = mTagArrayList.get(position);
                Log.i("Kevin", "The postion is: " + position);
                switch (index) {
                    case 0:
                        Delivery delivery = null;
                        String tag = "";
//                Design holoDesign = isLight() ? Design.HOLO_LIGHT : Design.HOLO_DARK;
//                Design mtrlDesign = isLight() ? Design.MATERIAL_LIGHT : Design.MATERIAL_DARK;

                        delivery = PostOffice.newMail(getActivity())
                                .setTitle("Please input your tag:")
                                .setThemeColor(Color.BLUE)
                                .setDesign(Design.MATERIAL_LIGHT)
                                .showKeyboardOnDisplay(true)
                                .setButton(Dialog.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
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
                                .setStyle(new EditTextStyle.Builder(getActivity())
                                        .setHint("Tag:")
                                        .setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                                        .setOnTextAcceptedListener(new EditTextStyle.OnTextAcceptedListener() {
                                            @Override
                                            public void onAccepted(String text) {
                                                Toast.makeText(getActivity(), "TAG was accepted: " + text, Toast.LENGTH_SHORT).show();

                                                mTagArrayList.get(position).setTag(text);
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }).build())
                                .build();
                        if(delivery != null)
                            delivery.show(getFragmentManager(), tag);

                        break;
                    case 1:
                        // delete
//					delete(item);
                        mTagArrayList.remove(position);
                        Log.i("Kevin", "after remove :" + mTagArrayList.get(position).getTag());
                        mAdapter.updateList(mTagArrayList);
                        mAdapter.notifyDataSetChanged();

                        break;
                }
                return false;
            }
        });
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

}

package com.receiptofi.checkout.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.widgets.Dialog;
import com.r0adkll.postoffice.PostOffice;
import com.r0adkll.postoffice.model.Delivery;
import com.r0adkll.postoffice.model.Design;
import com.receiptofi.checkout.R;
import com.receiptofi.checkout.adapters.ExpenseTagAdapter;
import com.receiptofi.checkout.http.API;
import com.receiptofi.checkout.http.ExternalCall;
import com.receiptofi.checkout.http.ExternalCallWithOkHttp;
import com.receiptofi.checkout.http.ResponseHandler;
import com.receiptofi.checkout.model.ExpenseTagModel;
import com.receiptofi.checkout.model.types.IncludeAuthentication;
import com.receiptofi.checkout.service.DeviceService;
import com.receiptofi.checkout.utils.JsonParseUtils;
import com.receiptofi.checkout.utils.db.ExpenseTagUtils;
import com.receiptofi.checkout.views.ToastBox;
import com.receiptofi.checkout.views.dialog.ExpenseTagDialog;
import com.squareup.okhttp.Headers;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

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
    private static final String TAG = ExpenseTagFragment.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GridView gridView;
    private List<ExpenseTagModel> tagModelList;
    private ExpenseTagAdapter mAdapter;
    private ButtonFloat fbAddTag;

    private OnFragmentInteractionListener mListener;

    public static final int EXPENSE_TAG_DELETED = 0x1561;
    public static final int EXPENSE_TAG_UPDATED = 0x1562;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case EXPENSE_TAG_DELETED:
                    notifyList();
                    break;
                case EXPENSE_TAG_UPDATED:
                    notifyList();
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });

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
        fbAddTag = (ButtonFloat)rootView.findViewById(R.id.buttonFloat);
        fbAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment editTagDialog = ExpenseTagDialog.newInstance(null);
                editTagDialog.show(ft, "dialog");
            }
        });

        Map<String, ExpenseTagModel> expTagMap = ExpenseTagUtils.getExpenseTagModels();
        tagModelList = new LinkedList<>(expTagMap.values());
        mAdapter = new ExpenseTagAdapter(getActivity(), tagModelList);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ExpenseTagModel tagModel = tagModelList.get(i);
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
                                FragmentTransaction ft = getFragmentManager().beginTransaction();
                                Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                                if (prev != null) {
                                    ft.remove(prev);
                                }
                                ft.addToBackStack(null);

                                // Create and show the dialog.
                                DialogFragment editTagDialog = ExpenseTagDialog.newInstance(tagModel.getId());
                                editTagDialog.show(ft, "dialog");
                                dialog.dismiss();
                            }
                        })
                        .setButton(Dialog.BUTTON_NEUTRAL, "Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AlertDialog.Builder(getActivity())
                                        .setTitle(getString(R.string.expense_tag_dialog_delete_label))
                                        .setMessage(getString(R.string.expense_tag_dialog_text, tagModel.getName()))
                                        .setNegativeButton(getString(R.string.expense_tag_dialog_button_cancel), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                            }
                                        })
                                        .setPositiveButton(getString(R.string.expense_tag_dialog_button_delete), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                String tagId = tagModel.getId();
                                                String tagName = tagModel.getName();

                                                if (null != tagId || null != tagName) {
                                                    JSONObject postData = new JSONObject();
                                                    try {
                                                        postData.put("tagId", tagId);
                                                        postData.put("tagName", tagName);

                                                        ExternalCallWithOkHttp.doPost(getActivity(), postData, API.DELETE_EXPENSE_TAG, IncludeAuthentication.YES, new ResponseHandler() {
                                                            @Override
                                                            public void onSuccess(Headers headers, String body) {
                                                                DeviceService.onSuccess(headers, body);
                                                                updateHandler.sendEmptyMessage(EXPENSE_TAG_DELETED);
                                                            }

                                                            @Override
                                                            public void onError(int statusCode, String error) {
                                                                Log.d(TAG, "executing DELETE_EXPENSE_TAG: onError: " + error);
                                                                ToastBox.makeText(getActivity(), JsonParseUtils.parseError(error), Toast.LENGTH_SHORT).show();
                                                            }

                                                            @Override
                                                            public void onException(Exception exception) {
                                                                Log.d(TAG, "executing DELETE_EXPENSE_TAG: onException: " + exception.getMessage());
                                                                ToastBox.makeText(getActivity(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    } catch (JSONException e) {
                                                        Log.e(TAG, "Exception while deleting expense Tag=" + tagName + "reason=" + e.getMessage(), e);
                                                    }
                                                }
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
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

    private void notifyList() {
        Map<String, ExpenseTagModel> expTagMap = ExpenseTagUtils.getExpenseTagModels();
        tagModelList = new LinkedList<>(expTagMap.values());
        if (mAdapter != null) {
            mAdapter.updateList(tagModelList);
            mAdapter.notifyDataSetChanged();
            mAdapter.notifyDataSetChanged();
        }
    }

}

package com.receiptofi.receiptapp.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.gc.materialdesign.views.ButtonFloat;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.receiptofi.receiptapp.R;
import com.receiptofi.receiptapp.adapters.ExpenseTagAdapter;
import com.receiptofi.receiptapp.http.API;
import com.receiptofi.receiptapp.http.ExternalCallWithOkHttp;
import com.receiptofi.receiptapp.http.ResponseHandler;
import com.receiptofi.receiptapp.http.types.ExpenseTagSwipe;
import com.receiptofi.receiptapp.model.ExpenseTagModel;
import com.receiptofi.receiptapp.model.types.IncludeAuthentication;
import com.receiptofi.receiptapp.service.DeviceService;
import com.receiptofi.receiptapp.utils.AppUtils;
import com.receiptofi.receiptapp.utils.JsonParseUtils;
import com.receiptofi.receiptapp.utils.db.ExpenseTagUtils;
import com.receiptofi.receiptapp.views.dialog.ExpenseTagDialog;
import com.squareup.okhttp.Headers;

import junit.framework.Assert;

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
public class ExpenseTagFragment extends Fragment implements DialogInterface.OnDismissListener {
    /**
     * List Style implementation
     */
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private SwipeMenuListView mListView;
    private ExpenseTagAdapter mAdapter;
    private List<ExpenseTagModel> tagModelList;
    private static final String TAG = ExpenseTagFragment.class.getSimpleName();
    private ButtonFloat fbAddTag;

    private View view;
    public static final int EXPENSE_TAG_DELETED = 0x1561;
    public static final int EXPENSE_TAG_UPDATED = 0x1562;

    private Drawable edit;
    private Drawable delete;
    private Drawable alert;

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
     * @return A new instance of fragment TagModifyFragment.
     */
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_expense_tag_style_listview, container, false);
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
            Log.e(TAG, "reason=" + e.getLocalizedMessage(), e);
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
        void onFragmentInteraction(Uri uri);
    }

    private void setupView() {
        fbAddTag = (ButtonFloat) view.findViewById(R.id.buttonFloat);
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

        mListView = (SwipeMenuListView) view.findViewById(R.id.listView);

        Map<String, ExpenseTagModel> expTagMap = ExpenseTagUtils.getExpenseTagModels();
        tagModelList = new LinkedList<>(expTagMap.values());
        mAdapter = new ExpenseTagAdapter(getActivity(), tagModelList);
        mListView.setAdapter(mAdapter);

        edit = new IconDrawable(getActivity(), Iconify.IconValue.fa_pencil_square_o)
                .colorRes(R.color.white)
                .sizePx(64);

        delete = new IconDrawable(getActivity(), Iconify.IconValue.fa_trash_o)
                .colorRes(R.color.white)
                .sizePx(64);

        alert = new IconDrawable(getActivity(), Iconify.IconValue.fa_exclamation_triangle)
                .colorRes(R.color.red)
                .actionBarSize();

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set a icon
                openItem.setIcon(edit);
                // add to menu
                menu.addMenuItem(openItem);
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(delete);
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
                ExpenseTagSwipe expenseTagSwipe = ExpenseTagSwipe.findSwipeTypeByCode(index);
                Assert.assertNotNull(expenseTagSwipe);
                Log.d(TAG, "Selected swipe action is: " + expenseTagSwipe.name());

                final ExpenseTagModel tagModel = tagModelList.get(position);
                Log.d(TAG, "Selected tag name is: " + tagModel.getName());
                switch (expenseTagSwipe) {
                    case EDIT:
                        FragmentTransaction ft = getFragmentManager().beginTransaction();
                        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        ft.addToBackStack(null);

                        /** Create and show the dialog.*/
                        DialogFragment editTagDialog = ExpenseTagDialog.newInstance(tagModel.getId());
                        editTagDialog.show(ft, "dialog");

                        break;
                    case DELETE:
                        deleteExpenseTag(tagModel);
                        break;
                    default:
                        Log.e(TAG, "Reached unsupported condition, expense tag swipe index=" + index);
                        throw new RuntimeException("Reached unreachable condition");
                }
                return false;
            }
        });
    }

    private void deleteExpenseTag(final ExpenseTagModel tagModel) {
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
                                        Log.d(TAG, "onError=" + error);
                                        if (null != getActivity()) {
                                            showMessage(JsonParseUtils.parseForErrorReason(error), SuperToast.Background.RED, getActivity());
                                        }
                                    }

                                    @Override
                                    public void onException(Exception e) {
                                        Log.d(TAG, "reason=" + e.getLocalizedMessage(), e);
                                        if (null != getActivity()) {
                                            showMessage(e.getMessage(), SuperToast.Background.RED, (Activity) AppUtils.getHomePageContext());
                                        }
                                    }
                                });

                            } catch (JSONException e) {
                                Log.e(TAG, "Exception while deleting expense Tag=" + tagName + "reason=" + e.getMessage(), e);
                            }
                        }
                    }
                })
                .setIcon(alert)
                .show();
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void notifyList() {
        Map<String, ExpenseTagModel> expTagMap = ExpenseTagUtils.getExpenseTagModels();
        tagModelList = new LinkedList<>(expTagMap.values());
        if (mAdapter != null) {
            mAdapter.updateList(tagModelList);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        updateHandler.sendEmptyMessage(EXPENSE_TAG_UPDATED);
    }

    /**
     * Show Toast message.
     *
     * @param message
     * @param context
     */
    public static void showMessage(final String message, final Activity context) {
        showMessage(message, SuperToast.Background.BLUE, context);
    }

    public static void showMessage(final String message, final int backgroundColor, final Activity context) {
        Assert.assertNotNull("Context should not be null", context);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        /** getMainLooper() function of Looper class, which will provide you the Looper against the Main UI thread. */
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                SuperActivityToast superActivityToast = new SuperActivityToast(context);
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

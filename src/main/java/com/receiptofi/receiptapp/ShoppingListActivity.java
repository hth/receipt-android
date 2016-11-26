package com.receiptofi.receiptapp;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.receiptofi.receiptapp.adapters.ShoppingListAdapter;
import com.receiptofi.receiptapp.http.types.ExpenseTagSwipe;
import com.receiptofi.receiptapp.model.ShoppingItemModel;
import com.receiptofi.receiptapp.utils.db.ShoppingItemUtils;
import com.receiptofi.receiptapp.views.dialog.ExpenseTagDialog;

import junit.framework.Assert;

import java.util.List;

/**
 * User: hitender
 * Date: 12/12/15 3:10 AM
 */
public class ShoppingListActivity extends ListActivity {
    private static final String TAG = ShoppingListActivity.class.getSimpleName();
    public static final String BUNDLE_KEY_BIZ_NAME = "BIZ_NAME";

    private FloatingActionButton fbAddTag;

    private SwipeMenuListView mListView;
    private ShoppingListAdapter mAdapter;
    private List<ShoppingItemModel> tagModelList;

    private Drawable edit;
    private Drawable delete;
    private Drawable alert;

    public static final int UPDATE_SHOPPING_LIST = 0x1081;

    public final Handler updateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final int what = msg.what;
            switch (what) {
                case UPDATE_SHOPPING_LIST:
                    final ShoppingListAdapter.MessageShopping messageShopping = (ShoppingListAdapter.MessageShopping) msg.obj;

                    /** Show animation only when checked. */
                    if (messageShopping.isChecked()) {
                        Animation anim = AnimationUtils.loadAnimation(ShoppingListActivity.this, android.R.anim.slide_out_right);
                        anim.setDuration(500);
                        mListView.getChildAt(messageShopping.getPosition()).startAnimation(anim);

                        // http://stackoverflow.com/questions/3928193/how-to-animate-addition-or-removal-of-android-listview-rows
                        new Handler().postDelayed(new Runnable() {

                            public void run() {
                                mAdapter.notifyDataSetChanged();

                            }

                        }, anim.getDuration());
                    }
                    break;
                default:
                    Log.e(TAG, "Update handler not defined for: " + what);
            }
            return true;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate the layout for this fragment
        setContentView(R.layout.fragment_shopping_list);

        /** Setup back up button with its own icon. */
        int upId = Resources.getSystem().getIdentifier("up", "id", "android");
        if (upId > 0) {
            ImageView up = (ImageView) findViewById(upId);
            up.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_chevron_left)
                    .colorRes(R.color.white)
                    .actionBarSize());
        }

        setupView();
    }

    private void setupView() {
        fbAddTag = (FloatingActionButton) findViewById(R.id.buttonFloat);
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


        Bundle receiveBundle = this.getIntent().getExtras();
        List<ShoppingItemModel> shoppingItemModels = ShoppingItemUtils.getShoppingItems(receiveBundle.getString(BUNDLE_KEY_BIZ_NAME));
        setupView(shoppingItemModels);
    }

    private void addAnimation() {
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(200);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 50.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(10);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);

        mListView.setLayoutAnimation(controller);
    }

    private void setupView(List<ShoppingItemModel> shoppingItemModels) {
        if (!shoppingItemModels.isEmpty()) {
            mListView = (SwipeMenuListView) findViewById(R.id.shopping_list_listView);
            mAdapter = new ShoppingListAdapter(this, ShoppingListActivity.this, shoppingItemModels);
            mListView.setAdapter(mAdapter);
            addAnimation();

            TextView textView = (TextView) findViewById(R.id.shopping_place);
            textView.setText(shoppingItemModels.get(0).getBizName());
        }

        edit = new IconDrawable(this, FontAwesomeIcons.fa_pencil_square_o)
                .colorRes(R.color.white)
                .sizePx(64);

        delete = new IconDrawable(this, FontAwesomeIcons.fa_trash_o)
                .colorRes(R.color.white)
                .sizePx(64);

        alert = new IconDrawable(this, FontAwesomeIcons.fa_exclamation_triangle)
                .colorRes(R.color.red)
                .actionBarSize();

        // step 1. create a MenuCreator
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(ShoppingListActivity.this);
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                // set item width
                openItem.setWidth(dp2px(90));
                // set a icon
                openItem.setIcon(edit);
                // add to menu
                menu.addMenuItem(openItem);
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(ShoppingListActivity.this);
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

                final ShoppingItemModel tagModel = tagModelList.get(position);
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
                        DialogFragment editTagDialog = ExpenseTagDialog.newInstance(tagModel.getName());
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

    private void deleteExpenseTag(final ShoppingItemModel shoppingItemModel) {

    }

    public void showTask(String bizName) {
    }


    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}

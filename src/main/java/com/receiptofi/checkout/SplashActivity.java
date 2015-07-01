package com.receiptofi.checkout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class SplashActivity extends Activity implements ViewPager.OnPageChangeListener {
    private static final String TAG = SplashActivity.class.getSimpleName();

    private List<ImageView> imageViewList;
    private TextView tvDescription;
    private LinearLayout llPoints;
    private String[] imageDescriptions;
    private int previousSelectPosition = 0;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setView();
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setView() {
        this.getActionBar().hide();
        setContentView(R.layout.activity_splash);

		/*// Auto change feature
        new Thread(new Runnable() {

			@Override
			public void run() {
				while (isLoop) {
					SystemClock.sleep(2000);
					handler.sendEmptyMessage(0);
				}
			}
		}).start();*/
    }

    public void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        tvDescription = (TextView) findViewById(R.id.tv_image_description);
        llPoints = (LinearLayout) findViewById(R.id.ll_points);

        prepareData();

        ViewPagerAdapter adapter = new ViewPagerAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.setOnPageChangeListener(this);

        tvDescription.setText(imageDescriptions[previousSelectPosition]);
        llPoints.getChildAt(previousSelectPosition).setEnabled(true);

        /**
         * 2147483647 / 2 = 1073741820 - 1
         */
        int n = Integer.MAX_VALUE / 2 % imageViewList.size();
        int itemPosition = Integer.MAX_VALUE / 2 - n;

        mViewPager.setCurrentItem(itemPosition);
    }

    private void prepareData() {
        imageViewList = new ArrayList<>();
        int[] imageResIDs = getImageResIDs();
        imageDescriptions = getImageDescription();

        ImageView iv;
        View view;
        for (int imageResID : imageResIDs) {
            iv = new ImageView(this);
            iv.setBackgroundResource(imageResID);
            imageViewList.add(iv);

            // Add new View.
            view = new View(this);
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.point_background));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(5, 5);
            lp.leftMargin = 10;
            view.setLayoutParams(lp);
            view.setEnabled(false);
            llPoints.addView(view);
        }
    }

    private int[] getImageResIDs() {
        return new int[]{
                R.drawable.splash,
                R.drawable.splash2,
                R.drawable.splash3,
                R.drawable.splash,
                R.drawable.splash2
        };
    }

    private String[] getImageDescription() {
        return new String[]{
                "Instruction Page1",
                "Instruction Page2",
                "Instruction Page3",
                "Instruction Page4",
                "Instruction Page5"
        };
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        /**
         * Judge the output view is same as input view
         */
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        /**
         * Destroy the item.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(imageViewList.get(position % imageViewList.size()));
        }

        /**
         * Create a new view
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(imageViewList.get(position % imageViewList.size()));
            return imageViewList.get(position % imageViewList.size());
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int position) {
        // Update the description of image information.
        Log.i(TAG, "position is:" + position);
        if (position % imageViewList.size() == imageViewList.size() - 1) {

            Intent i = new Intent(SplashActivity.this, LaunchActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
        tvDescription.setText(imageDescriptions[position % imageViewList.size()]);
        // switch the choice point
        llPoints.getChildAt(previousSelectPosition).setEnabled(false);    // set the former pointer state as normal
        llPoints.getChildAt(position % imageViewList.size()).setEnabled(true);        // set the selected view state.
        previousSelectPosition = position % imageViewList.size();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

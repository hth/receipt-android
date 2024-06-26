package com.receiptofi.receiptapp.test;

import com.receiptofi.receiptapp.LaunchActivity;
import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;


public class LoginPaymentSyncTest extends ActivityInstrumentationTestCase2<LaunchActivity> {
  	private Solo solo;
  	
  	public LoginPaymentSyncTest() {
		super(LaunchActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation());
		getActivity();
  	}
  
   	@Override
   	public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
  	}
  
	public void testRun() {
        //Wait for activity: 'com.receiptofi.receipts.LaunchActivity'
		solo.waitForActivity(com.receiptofi.receiptapp.LaunchActivity.class, 2000);
        //Sleep for 3381 milliseconds
		solo.sleep(3381);
        //Click on LOG IN
		solo.clickOnView(solo.getView(com.receiptofi.receiptapp.R.id.sign_in_button));
        //Wait for activity: 'com.receiptofi.receipts.LogInActivity'
		assertTrue("com.receiptofi.receipts.LogInActivity is not found!", solo.waitForActivity(com.receiptofi.receiptapp.LogInActivity.class));
        //Sleep for 12945 milliseconds
		solo.sleep(12945);
        //Enter the text: 'li@receiptofi.com'
		solo.clearEditText((android.widget.EditText) solo.getView(com.receiptofi.receiptapp.R.id.email));
		solo.enterText((android.widget.EditText) solo.getView(com.receiptofi.receiptapp.R.id.email), "li@receiptofi.com");
        //Sleep for 518 milliseconds
		solo.sleep(518);
        //Click on Empty Text View
		solo.clickOnView(solo.getView(com.receiptofi.receiptapp.R.id.password));
        //Sleep for 5762 milliseconds
		solo.sleep(5762);
        //Enter the text: 'Chongzhi'
		solo.clearEditText((android.widget.EditText) solo.getView(com.receiptofi.receiptapp.R.id.password));
		solo.enterText((android.widget.EditText) solo.getView(com.receiptofi.receiptapp.R.id.password), "Chongzhi");
        //Sleep for 769 milliseconds
		solo.sleep(769);
        //Click on LOG IN
		solo.clickOnView(solo.getView(com.receiptofi.receiptapp.R.id.login_button));
        //Wait for activity: 'com.receiptofi.receipts.MainMaterialDrawerActivity'
		assertTrue("com.receiptofi.receipts.MainMaterialDrawerActivity is not found!", solo.waitForActivity(com.receiptofi.receiptapp.MainMaterialDrawerActivity.class));
        //Sleep for 10611 milliseconds
		solo.sleep(10611);
        //Click on ImageView
		solo.clickOnView(solo.getView(android.widget.ImageButton.class, 0));
        //Sleep for 6888 milliseconds
		solo.sleep(6888);
        //Click on Monthly 10 Process 10 receipts every month $0.02
		solo.clickInList(1, 0);
        //Wait for activity: 'com.receiptofi.receipts.SubscriptionUserActivity'
		assertTrue("com.receiptofi.receipts.SubscriptionUserActivity is not found!", solo.waitForActivity(com.receiptofi.receiptapp.SubscriptionUserActivity.class));
        //Sleep for 2198 milliseconds
		solo.sleep(2198);
        //Click on HomeView Purchase
		solo.clickOnView(solo.getView(android.widget.LinearLayout.class, 6));
        //Sleep for 1579 milliseconds
		solo.sleep(1579);
        //Click on Monthly 30 Process 30 receipts every month $0.04
		solo.clickInList(2, 0);
        //Wait for activity: 'com.receiptofi.receipts.SubscriptionUserActivity'
		assertTrue("com.receiptofi.receipts.SubscriptionUserActivity is not found!", solo.waitForActivity(com.receiptofi.receiptapp.SubscriptionUserActivity.class));
        //Wait for activity: 'com.braintreepayments.api.dropin.BraintreePaymentActivity'
		assertTrue("com.braintreepayments.api.dropin.BraintreePaymentActivity is not found!", solo.waitForActivity(com.braintreepayments.api.dropin.BraintreePaymentActivity.class));
        //Sleep for 4935 milliseconds
		solo.sleep(4935);
        //Press menu back key
		solo.goBack();
        //Sleep for 1373 milliseconds
		solo.sleep(1373);
        //Press menu back key
		solo.goBack();
        //Sleep for 2436 milliseconds
		solo.sleep(2436);
        //Press menu back key
		solo.goBack();
        //Sleep for 1536 milliseconds
		solo.sleep(1536);
        //Click on ImageView
		solo.clickOnView(solo.getView(android.widget.ImageButton.class, 0));
        //Sleep for 2601 milliseconds
		solo.sleep(2601);
        //Click on LinearLayout Sync Data  LinearLayout
		solo.clickInList(8, 0);
        //Sleep for 1002 milliseconds
		solo.sleep(1002);
        //Click on OK
		solo.clickOnView(solo.getView(android.R.id.button1));
        //Sleep for 11903 milliseconds
		solo.sleep(11903);
        //Scroll to LinearLayout About receipts  LinearLayout
		android.widget.ListView listView0 = (android.widget.ListView) solo.getView(android.widget.ListView.class, 0);
		solo.scrollListToLine(listView0, 2);
        //Click on LinearLayout About receipts  LinearLayout
		solo.clickOnText(java.util.regex.Pattern.quote("About receipts"));
        //Sleep for 1137 milliseconds
		solo.sleep(1137);
        //Click on Got it
		solo.clickOnView(solo.getView(android.R.id.button1));
        //Sleep for 2269 milliseconds
		solo.sleep(2269);
        //Click on LinearLayout Delete App Data  LinearLayout
		solo.clickOnText(java.util.regex.Pattern.quote("Delete App Data"));
        //Sleep for 1180 milliseconds
		solo.sleep(1180);
        //Click on OK
		solo.clickOnView(solo.getView(android.R.id.button1));
        //Wait for activity: 'com.receiptofi.receipts.LaunchActivity'
		assertTrue("com.receiptofi.receipts.LaunchActivity is not found!", solo.waitForActivity(com.receiptofi.receiptapp.LaunchActivity.class));
	}
}

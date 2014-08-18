package com.receiptofi.android;

import com.receiptofi.android.utils.UserUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends ParentActivity {
	
	public static boolean isLoggedIn=false;
	
	EditText userName,password;
	String userNameStr,passwordStr;
	
	StringBuilder errors= new StringBuilder();
	TextView signupText;
	Handler uiThread=new Handler();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if(isLoggedIn){
        	finish();
        	startActivity(new Intent(this,HomePageActivity.class));
        }
        setContentView(R.layout.login_page);
        userName=(EditText)findViewById(R.id.userName);
        password=(EditText)findViewById(R.id.password);
        signupText=(TextView)findViewById(R.id.signupText);
        
        userName.setOnFocusChangeListener(edittextListner);
        password.setOnFocusChangeListener(edittextListner);
        
        signupText.setText(Html.fromHtml("Don’t have an account? <u><font color=\"blue\">Sign up</font> </u>,it’s free!"));
    }


    
    private static OnFocusChangeListener edittextListner = new OnFocusChangeListener(){

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			
			if(hasFocus){
				((EditText)v).setHint("");
			}else {
				String input=((EditText)v).getText().toString();
				if(input!=null && input.length()==0){
				int id=	((EditText)v).getId();
					if(id==R.id.userName){
						((EditText)v).setHint("Email");
					}else if (id==R.id.password) {
						((EditText)v).setHint("Password");
					}
				}
			}
			
		}
    	
    };
    
    public void login(View view){
    	//getting username and password
    	userNameStr=userName.getText().toString();
    	passwordStr=password.getText().toString();
    
    	if(userNameStr==null || (userNameStr!=null && userNameStr.length()==0)){
    		errors.append(this.getResources().getString(R.string.enter_email));
    	}else {
			if(!UserUtils.isValidEmail(userNameStr))
			{
				addErrorMsg(this.getResources().getString(R.string.enter_valid_email));
			}
		}
    	if(passwordStr==null || (passwordStr!=null && passwordStr.length()==0)){
    			addErrorMsg(this.getResources().getString(R.string.enter_password));
    	}
    	//error string is for keeping the error that needs to be shown to the user.
    	if(errors.length()>0){
    		Toast toast= Toast.makeText(this, errors, Toast.LENGTH_SHORT);
    		toast.setGravity(Gravity.TOP, 0, 20);
    		toast.show();
    		errors.delete(0, errors.length());
    	}else {
    		isLoggedIn=true;
    		showloader(this.getResources().getString(R.string.login_auth_msg));
    		uiThread.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					hideloader();
					finish();
					startActivity(new Intent(LoginActivity.this,HomePageActivity.class));
					LoginActivity.this.overridePendingTransition(R.anim.right_slide_in,R.anim.right_slide_out );
				}
			}, 7000);
    		
    		
		}
    }
  
    
    private void addErrorMsg(String msg){
    	if(errors.length()==0){
			errors.append(msg);
		}else {
			errors.append("\n").append("\n").append(msg);
		}
    }
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	addTobackStack(this);
    }
}

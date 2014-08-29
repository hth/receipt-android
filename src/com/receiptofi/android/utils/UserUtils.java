package com.receiptofi.android.utils;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.receiptofi.android.db.DBHelper;
import com.receiptofi.android.http.API;
import com.receiptofi.android.http.ApiParser;
import com.receiptofi.android.http.HTTPUtils;
import com.receiptofi.android.http.ResponseHandler;
import com.receiptofi.android.models.ReceiptModel;

public class UserUtils {
	static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

	public static boolean isValidEmail(String email) {
		if (email.matches(emailPattern)) {
			return true;
		} else {
			return false;
		}
	}

	public static String getEmail() {
		return DBHelper.getValue(DBHelper.key.XR_MAIL);
	}

	public static String getAuth() {
		return DBHelper.getValue(DBHelper.key.XR_AUTH);
	}

	public static boolean isValidAppUser() {
		String mail = UserUtils.getEmail();
		String auth = UserUtils.getAuth();

		if (mail != null && mail.trim().length() > 0 && auth != null
				&& auth.trim().length() > 0) {
			return true;
		} else {
			return false;
		}
	}
	


}

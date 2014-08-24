package com.receiptofi.android.utils;

import com.receiptofi.android.db.DBHelperA;

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
		return DBHelperA.getValue(DBHelperA.key.XR_MAIL);
	}

	public static String getAuth() {
		return DBHelperA.getValue(DBHelperA.key.XR_AUTH);
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

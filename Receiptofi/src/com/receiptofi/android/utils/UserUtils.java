package com.receiptofi.android.utils;

public class UserUtils {
	static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

	public static boolean isValidEmail(String email) {
		if (email.matches(emailPattern)) {
			return true;
		} else {
			return false;
		}
	}
}

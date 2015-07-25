package com.receiptofi.receipts.utils;

/**
 * User: PT
 * Date: 12/12/14 6:40 PM
 */
public class Validation {

    // String length min-max
    public static final int NAME_MIN_LENGTH = 2;
    public static final int EMAIL_MIN_LENGTH = 5;
    public static final int PASSWORD_MIN_LENGTH = 6;

    public static boolean isAlphaNumeric(String s){
        String pattern= "^[a-zA-Z0-9]*$";
        if(s.matches(pattern)){
            return true;
        }
        return false;
    }

    public static boolean isNumeric(String s){
        String pattern= "^[0-9]*$";
        if(s.matches(pattern)){
            return true;
        }
        return false;
    }
}

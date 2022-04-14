package com.epoch.loan.workshop.common.util;

import org.apache.commons.validator.routines.EmailValidator;

public class EmailUtils {
    private static EmailValidator instance = null;

    static {
        instance = EmailValidator.getInstance();
    }

    public static boolean isEmail(String email) {
        boolean flag = false;
        flag = instance.isValid(email);
        return flag;
    }

//	public static void main(String args[])  {
//		String email="dfgf dg@bb.cc";
//		boolean b=EmailUtils.isEmail(email);
//		System.out.println(b);
//	}
}

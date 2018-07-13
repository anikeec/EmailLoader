package com.apu.emailloader.email;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class EmailUtils {

	private static int EMAIL_SUBJECT_MAX_LENGTH = 80;
    
    public static String checkEmailSubject(String subject) {  	
    	if(subject == null) 
    		subject = "";
    	
    	subject = StringUtils.removePunct(subject);
    	
    	return subject;
    }
    
    public static String checkEmailDirectory(String str) {
        return StringUtils.lengthRestriction(str, EMAIL_SUBJECT_MAX_LENGTH);
    }
    
    public static String chechFileNameCoding(String fileName) throws UnsupportedEncodingException {
        if((fileName != null) && (fileName.contains("UTF-8"))) {
            String substrCoding = "=?UTF-8?B?";
            String substring = fileName.substring(substrCoding.length());
            byte[] decoded = Base64.getMimeDecoder().decode(substring);
            fileName = new String(decoded, "UTF-8");
        }
        return fileName;
    }
	
}

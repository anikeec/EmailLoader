package com.apu.emailloader.email;

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
	
}

package com.apu.emailloader.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class EmailUtils {

    private static int EMAIL_SUBJECT_MAX_LENGTH = 80;

    public static String checkEmailSubject(String subject) {
        if (subject == null)
            subject = "";

        subject = StringUtils.removePunct(subject);

        return subject;
    }

    public static String checkEmailDirectory(String str) {
        return StringUtils.lengthRestriction(str, EMAIL_SUBJECT_MAX_LENGTH);
    }
    
    public static String chechFileNameCoding(String fileName) throws IOException {
        StringBuilder retFileName = new StringBuilder();
        if ((fileName != null) && (fileName.toUpperCase().contains("UTF-8"))) {
            List<String> strs = splitStrByPattern(fileName, "=?", "?=");         

            String substrCoding;                        
            for (String str : strs) {
                if (str.contains("utf-8")) {
                    substrCoding = "utf-8?B?";
                } else if(str.contains("UTF-8")) {
                    substrCoding = "UTF-8?B?";
                } else {
                    substrCoding = "";
                }
                if (str.length() == 0)
                    continue;
                String substring = str.substring(substrCoding.length());
                byte[] decoded = Base64.getDecoder().decode(substring);
                retFileName.append(new String(decoded, "UTF-8"));
            }
        } else {
            if(fileName != null)
                retFileName.append(fileName);
            else 
                return null;
        }
        return retFileName.toString();
    }
    
    private static List<String> splitStrByPattern(String str, String startStr, String endStr) {
        List<String> strs = new ArrayList<>();
        int startIndex = 0, finishIndex = 0;            
        while(true) {
            startIndex = str.indexOf(startStr);
            if(startIndex == -1)
                break;
            finishIndex = str.indexOf(endStr, startIndex + 1);
            if(finishIndex == -1)
                break;
            strs.add(str.substring(startIndex + startStr.length(), finishIndex));
            str = str.substring(finishIndex + endStr.length());
        }
        return strs;
    }

}

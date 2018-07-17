package com.apu.emailloader.email;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailUtils {
    
    private static String START_ENCODED_SYMBOLS = "=?";
    private static String END_ENCODED_SYMBOLS = "?=";

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
        if(fileName == null)
            return null;
        String str = getDecodedStr(fileName);
        return StringUtils.removePunct(str);
        
//        StringBuilder retFileName = new StringBuilder();
//        if ((fileName != null) && (fileName.toUpperCase().contains("KOI8-R"))) {
//            List<String> strs = splitStrByPattern(fileName, START_ENCODED_SYMBOLS, END_ENCODED_SYMBOLS);
//            String substrCoding;                        
//            for (String str : strs) {
//                boolean needDecode = true;
//                if (str.contains("koi8-r")) {
//                    substrCoding = "koi8-r?B?";
//                } else if(str.contains("KOI8-R")) {
//                    substrCoding = "KOI8-R?B?";
//                } else {
//                    substrCoding = "";
//                    needDecode = false;
//                }
//                if (str.length() == 0)
//                    continue;
//                if(needDecode) {
//                    String substring = str.substring(substrCoding.length());
//                    byte[] decoded = Base64.getDecoder().decode(substring);
//                    String koiStr = new String(decoded, "KOI8-R");
//                    decoded = koiStr.getBytes("UTF-8");
//                    String koiStrDec = new String(decoded, "UTF-8");
//                    retFileName.append(koiStrDec);
//                } else {
//                    retFileName.append(str);
//                }
//            }           
//            
//        }
//        else 
//        if ((fileName != null) && (fileName.toUpperCase().contains("UTF-8"))) {
//            List<String> strs = splitStrByPattern(fileName, START_ENCODED_SYMBOLS, END_ENCODED_SYMBOLS);         
//
//            String substrCoding;                        
//            for (String str : strs) {
//                if (str.contains("utf-8")) {
//                    substrCoding = "utf-8?B?";
//                } else if(str.contains("UTF-8")) {
//                    substrCoding = "UTF-8?B?";
//                } else {
//                    substrCoding = "";
//                }
//                if (str.length() == 0)
//                    continue;
//                String substring = str.substring(substrCoding.length());
//                byte[] decoded = Base64.getDecoder().decode(substring);
//                retFileName.append(new String(decoded, "UTF-8"));
//            }
//        } else {
//            if(fileName != null)
//                retFileName.append(fileName);
//            else 
//                return null;
//        }
//        return retFileName.toString();        
    }
    
    private static final String PUNCT_SUBJECT_EMAIL = "!\"#$%&'()*+,/:;?[\\]^`{|}~";
    
    public static String removePunctFromEmail(String str) {
        String result = str.replaceAll("(\\r\\n)+", "");
        return StringUtils.removePunct(result, PUNCT_SUBJECT_EMAIL);
    }
    
//    public static List<String> splitStrByPattern(String str, String startStr, String endStr) {        
//        List<String> strs = new ArrayList<>();
//        if((str == null)||(str.length() < 1))
//            return strs;
//        int startIndex = 0, finishIndex = 0;            
//        while(true) {
//            startIndex = str.indexOf(startStr);
//            if(startIndex == -1)
//                break;
//            finishIndex = str.indexOf(endStr, startIndex + 1);
//            if(finishIndex == -1)
//                break;
//            strs.add(str.substring(startIndex + startStr.length(), finishIndex));
//            str = str.substring(finishIndex + endStr.length());
//        }
//        return strs;
//    }
    
    public static String getDecodedStr(String str) {
        RowPart findedPart;
        do {
            findedPart = getNextEncodedPart(str, START_ENCODED_SYMBOLS, END_ENCODED_SYMBOLS);
            if((findedPart != null)&&(findedPart.encoded == true)) {
                findedPart.strDecoded = decodePart(findedPart.str);
                str = replaceEncodedPart(str, findedPart);
            }
        } while(findedPart != null);
        
        return str;
    }
    
    public static RowPart getNextEncodedPart(String str, String startStr, String endStr) {
        int startIndex = str.indexOf(startStr);
        if (startIndex == -1)
            return null;
        int endIndex = str.indexOf(endStr, startIndex + 1);
        if (endIndex == -1)
            return null;
        String subStr = str.substring(startIndex + startStr.length(), endIndex);        
        RowPart retPart = new RowPart(subStr, true, startIndex, endIndex + endStr.length());        
        return retPart;
    }
    
    public static String decodePart(String str) {
        if (str.length() < 5)
                return str;
        boolean needDecode = true;
        String substrCoding;
        if (str.toUpperCase().contains("KOI8-R")) {                                   
            if (str.contains("koi8-r")) {
                substrCoding = "koi8-r?B?";
            } else if(str.contains("KOI8-R")) {
                substrCoding = "KOI8-R?B?";
            } else {
                substrCoding = "";
                needDecode = false;
            }            
            if(needDecode) {
                String substring = str.substring(substrCoding.length());
                byte[] decoded = Base64.getDecoder().decode(substring);
                try {
                    String koiStr = new String(decoded, "KOI8-R");
                    decoded = koiStr.getBytes("UTF-8");
                    return new String(decoded, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(EmailUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }                                    
        } else if (str.toUpperCase().contains("UTF-8")) {                               
            if (str.contains("utf-8")) {
                substrCoding = "utf-8?B?";
            } else if(str.contains("UTF-8")) {
                substrCoding = "UTF-8?B?";
            } else {
                substrCoding = "";
                needDecode = false;
            }
            if(needDecode) {
                String substring = str.substring(substrCoding.length());
                byte[] decoded = Base64.getDecoder().decode(substring);
                try {
                    return new String(decoded, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(EmailUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else { 
            return str;
        }        
        return null;
    }
    
    public static String replaceEncodedPart(String strSrc, RowPart rowPart) {
        StringBuilder sb = new StringBuilder();
        sb.append(strSrc.substring(0, rowPart.startPosition));
        sb.append(rowPart.strDecoded);
        sb.append(strSrc.substring(rowPart.endPosition, strSrc.length()));
        return sb.toString();
    }
    
    public static class RowPart {
        String str;
        boolean encoded;
        String strDecoded;
        int startPosition;
        int endPosition;
        
        public RowPart(String str, boolean encoded, int startPosition, int endPosition) {
            this.str = str;
            this.encoded = encoded;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }
        
    }

}

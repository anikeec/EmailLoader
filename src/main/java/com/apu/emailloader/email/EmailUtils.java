package com.apu.emailloader.email;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tika.parser.txt.CharsetDetector;
import org.apache.tika.parser.txt.CharsetMatch;
import org.mozilla.intl.chardet.HtmlCharsetDetector;
import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;
import org.mozilla.intl.chardet.nsPSMDetector;

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
    
    private static String checkForErrorDecoding(String str) throws UnsupportedEncodingException {        
//        String coding = null;
        
        byte bytes[] = str.getBytes("koi8-r");
        
        CharsetDetector detector = new CharsetDetector();
        detector.setText(bytes);
        CharsetMatch charset = detector.detect();
        String charsetName = charset.getName();
        bytes = str.getBytes(charsetName);//"ISO-8859-1"
        
        detector.setText(bytes);
        charset = detector.detect();
        charsetName = charset.getName();        
        String retStr = new String(bytes, charsetName);
        
        return retStr;   
        
//        bytes = str.getBytes("UTF-8");
//        String s_utf8 = new String(bytes, "UTF-8");
        
//        if (s_iso8859_1.equals(str)) {
//            coding = "ISO-8859-1";
//        } else if (s_utf8.equals(str)){
//            coding = "UTF-8";
//        }
        
//        CharsetEncoder  enc = Charset.forName("UTF-8").newEncoder();
//        if (enc.canEncode(str)) {
//            coding = "UTF-8";
//        }
        
//        enc = Charset.forName("ISO-8859-1").newEncoder();
//        if (enc.canEncode(str)) {
//            coding = "ISO-8859-1";
//        }       
        
//        // Initalize the nsDetector() ;
//        int lang = nsPSMDetector.ALL ;
//        nsDetector det = new nsDetector(lang) ;
//
//        // Set an observer...
//        // The Notify() will be called when a matching charset is found.
//
//        det.Init(new nsICharsetDetectionObserver() {
//                public void Notify(String charset) {
//                    HtmlCharsetDetector.found = true ;
//                    System.out.println("CHARSET = " + charset);
//                }
//        });
//        
//        boolean isAscii = true;
//        byte[] buffer = str.getBytes(); 
//        int length = buffer.length;
//        isAscii = det.isAscii(buffer, length);
//        boolean done = false;
//        while(!isAscii && !done) {
//            done = det.DoIt(buffer, length, false);
//        }
//        det.DataEnd();
//        if (isAscii) {
//            System.out.println("CHARSET = ASCII");
//        }
        
//        URL url = new URL(argv[0]);
//        BufferedInputStream imp = new BufferedInputStream(str.chars());
//
//        byte[] buf = new byte[1024] ;
//        int len;
//        boolean done = false ;
//        boolean isAscii = true ;
//
//        while( (len=imp.read(buf,0,buf.length)) != -1) {
//
//                // Check if the stream is only ascii.
//                if (isAscii)
//                    isAscii = det.isAscii(buf,len);
//
//                // DoIt if non-ascii and not done yet.
//                if (!isAscii && !done)
//                    done = det.DoIt(buf,len, false);
//        }
//        det.DataEnd();

//        if (isAscii) {
//           System.out.println("CHARSET = ASCII");
//           found = true ;
//        }
        
        
//        Charset koi8 = Charset.forName("KOI8-R");
//        bytes = str.getBytes("UTF-8");
//        int counter = 0;
//        for(byte b:bytes) {
//            if(b == (byte)0xC3)
//                counter++;
//        }
//        if(counter >= (bytes.length*1)/4) {
////            byte[] bytesRemake = new byte[bytes.length - counter];
////            int i = 0;
////            for(byte b:bytes) {
////                if(b != (byte)0xC3)
////                    bytesRemake[i++] = b;
////            }
//            String strNew = new String(str.getBytes("ISO-8859-1"), "windows-1251");
//            return strNew;//new String(bytesRemake, "CP866");
//        } else {
//            return str;
//        }        
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
    
    public static String getDecodedStr(String str) throws UnsupportedEncodingException {
        RowPart findedPart;
        do {
            findedPart = getNextEncodedPart(str, START_ENCODED_SYMBOLS, END_ENCODED_SYMBOLS);
            if((findedPart != null)&&(findedPart.encoded == true)) {
                findedPart.strDecoded = decodePart(findedPart.str);
                str = replaceEncodedPart(str, findedPart);
            }
        } while(findedPart != null);
        str = checkForErrorDecoding(str);
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
        if (str.toUpperCase().contains("KOI8-R?B?")) {
            substrCoding = "KOI8-R?B?";
//            if (str.contains("koi8-r?B?")) {
//                substrCoding = "koi8-r?B?";
//            } else if(str.contains("KOI8-R?B?")) {
//                substrCoding = "KOI8-R?B?";
//            } else {
//                substrCoding = "";
//                needDecode = false;
//            }            
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
        } else if (str.toUpperCase().contains("UTF-8?B?")) { 
            substrCoding = "UTF-8?B?";
//            if (str.contains("utf-8")) {
//                substrCoding = "utf-8?B?";
//            } else if(str.contains("UTF-8")) {
//                substrCoding = "UTF-8?B?";
//            } else {
//                substrCoding = "";
//                needDecode = false;
//            }
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

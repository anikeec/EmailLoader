/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.apu.emailloader.email;

import java.io.UnsupportedEncodingException;
import java.util.List;
import org.junit.Assert;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author apu
 */
public class EmailUtilsTest {
    
    public EmailUtilsTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of checkEmailSubject method, of class EmailUtils.
     */
    @Test
    public void testCheckEmailSubject() {
        System.out.println("checkEmailSubject");
        String subject = "This is+ test*\\& message";
        String expResult = "This is test message";
        String result = EmailUtils.checkEmailSubject(subject);
        assertEquals(expResult, result);
    }

    /**
     * Test of checkEmailDirectory method, of class EmailUtils.
     */
    @Test
    public void testCheckEmailDirectory() {
        System.out.println("checkEmailDirectory");
        String str = "This is the most long firectory name that I have ever "
                + "seen and I think it can not be longer";
        String expResult = "This is the most long firectory name that I have ever "
                + "seen and I think it can no";
        String result = EmailUtils.checkEmailDirectory(str);
        assertEquals(expResult, result);
    }

    /**
     * Test of chechFileNameCoding method, of class EmailUtils.
     */
    @Test
    public void testChechFileNameCoding() throws Exception {
        System.out.println("chechFileNameCoding");
        String fileName = "This is+ test*\\& file name";
        String expResult = "This is test file name";
        String result = EmailUtils.chechFileNameCoding(fileName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDecodedStr method, of class EmailUtils.
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void testGetDecodedStr() throws UnsupportedEncodingException {
        System.out.println("getDecodedStr");
        String str = "=?koi8-r?B?9MXT1M/X2cogz9TQ0sHXydTFzNg=?= <test@mail.com.ua>";
        String expResult = "Тестовый отправитель <test@mail.com.ua>";
        String result = EmailUtils.getDecodedStr(str);
        assertEquals(expResult, result);
        
        str = "<test@mail.com.ua>";
        expResult = "<test@mail.com.ua>";
        result = EmailUtils.getDecodedStr(str);
        assertEquals(expResult, result);
        
        str = "=?<test@mail.com.ua>";
        expResult = "=?<test@mail.com.ua>";
        result = EmailUtils.getDecodedStr(str);
        assertEquals(expResult, result);
        
        try {
            EmailUtils.getDecodedStr(null);
            Assert.fail("Must be NullPointerException");
        } catch(NullPointerException e) { }
    }

    /**
     * Test of getNextEncodedPart method, of class EmailUtils.
     */
    @Test
    public void testGetNextEncodedPart() {
        System.out.println("getNextEncodedPart");
        String str = "=?koi8-r?B?9MXT1M/X2cogz9TQ0sHXydTFzNg=?= <test@mail.com.ua>";
        String startStr = "=?";
        String endStr = "?=";
        String expResult = null;
        EmailUtils.RowPart result = EmailUtils.getNextEncodedPart(str, startStr, endStr);
        assertEquals(result.str, "koi8-r?B?9MXT1M/X2cogz9TQ0sHXydTFzNg=");
        assertTrue(result.startPosition == 0);
        assertTrue(result.endPosition == 41);
        
        str = "=?utf-8?q?Prom=2Eua?= <support@prom.ua>";
        startStr = "=?";
        endStr = "?=";
        expResult = null;
        result = EmailUtils.getNextEncodedPart(str, startStr, endStr);
        assertEquals(result.str, "utf-8?q?Prom=2Eua");
        assertTrue(result.startPosition == 0);
        assertTrue(result.endPosition == 21);
        
        try {
            EmailUtils.getNextEncodedPart(null, "", "");
            Assert.fail("Must be NullPointerException");
        } catch(NullPointerException e) { }
        
        try {
            EmailUtils.getNextEncodedPart("", null, "");
            Assert.fail("Must be NullPointerException");
        } catch(NullPointerException e) { }
        
        try {
            EmailUtils.getNextEncodedPart("", "", null);
            Assert.fail("Must be NullPointerException");
        } catch(NullPointerException e) { }
    }

    /**
     * Test of encodePart method, of class EmailUtils.
     */
    @Test
    public void testDecodePart() {
        System.out.println("decodePart");
        String str = "UTF-8?B?0J/RgNC40YHQvtC10LTQuNC90Y/QudGC0LXRgdGMINC6INCy0LXQsdC40L3QsNGA0YM=";
        String expResult = "Присоединяйтесь к вебинару";
        String result = EmailUtils.decodePart(str);
        assertEquals(expResult, result);
        
        str = "koi8-r?B?9MXT1M/X2cogz9TQ0sHXydTFzNg=";
        expResult = "Тестовый отправитель";
        result = EmailUtils.decodePart(str);
        assertEquals(expResult, result);
        
        str = "utf-8?q?Prom=2Eua";
        expResult = "Prom=2Eua";
        result = EmailUtils.decodePart(str);
        assertEquals(expResult, result);
        
        try {
            EmailUtils.decodePart(null);
            Assert.fail("Must be NullPointerException");
        } catch(NullPointerException e) { }
    }

    /**
     * Test of replaceEncodedPart method, of class EmailUtils.
     */
    @Test
    public void testReplaceEncodedPart() {
        System.out.println("replaceEncodedPart");
        String strSrc = "=?koi8-r?B?9MXT1M/X2cogz9TQ0sHXydTFzNg=?= <test@mail.com.ua>";
        EmailUtils.RowPart rowPart = 
            new EmailUtils.RowPart("koi8-r?B?9MXT1M/X2cogz9TQ0sHXydTFzNg=", true, 0, 41);
        rowPart.strDecoded = "Тестовый отправитель";
        String expResult = "Тестовый отправитель <test@mail.com.ua>";
        String result = EmailUtils.replaceEncodedPart(strSrc, rowPart);
        assertEquals(expResult, result);
        
        try {
            EmailUtils.replaceEncodedPart(null, rowPart);
            Assert.fail("Must be NullPointerException");
        } catch(NullPointerException e) { }
        
        try {
            EmailUtils.replaceEncodedPart("", null);
            Assert.fail("Must be NullPointerException");
        } catch(NullPointerException e) { }
    }

    /**
     * Test of removePunctFromEmail method, of class EmailUtils.
     */
    @Test
    public void testRemovePunctFromEmail() {
        System.out.println("removePunctFromEmail");
        String str = "Тестовый отправитель <test@mail.com.ua>";
        String expResult = "Тестовый отправитель <test@mail.com.ua>";
        String result = EmailUtils.removePunctFromEmail(str);
        assertEquals(expResult, result);
        
        str = "Тестовый отправитель\r\n <test@mail.com.ua>";
        expResult = "Тестовый отправитель <test@mail.com.ua>";
        result = EmailUtils.removePunctFromEmail(str);
        assertEquals(expResult, result);
    }
    
}

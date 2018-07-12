package com.apu.emailloader;

import java.io.PrintStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegrationManagement;

import com.apu.emailloader.utils.LoggingOutputStream;

@EnableIntegrationManagement(defaultLoggingEnabled = "true")
public class InboundImapIdleAdapterTestApp {
    
    private static Logger logger = Logger.getLogger(InboundImapIdleAdapterTestApp.class.getName());
    
    static DirectChannel outputChannel;

    public static void main (String[] args) throws Exception {
        System.setOut(new PrintStream(new LoggingOutputStream(Logger.getLogger("outLog"), Level.ALL), true));
        @SuppressWarnings("resource")
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext(
                "/META-INF/spring/integration/gmail-imap-idle-config.xml");        
    }
}

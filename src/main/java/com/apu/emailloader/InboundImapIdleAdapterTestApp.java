package com.apu.emailloader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.channel.DirectChannel;

public class InboundImapIdleAdapterTestApp {
    private static Log logger = LogFactory.getLog(InboundImapIdleAdapterTestApp.class);
    static DirectChannel outputChannel;

    public static void main (String[] args) throws Exception {
        @SuppressWarnings("resource")
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext(
                "/META-INF/spring/integration/gmail-imap-idle-config.xml");        
    }
}

package com.apu.emailloader.email;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class EmailService {
    
    private static Logger LOGGER = LogManager.getLogger(EmailService.class.getName());
    
    private void copyEmailToAnotherMailbox(MimeMessage eMailMessage) {
        try {
            Date emailSentDate = eMailMessage.getSentDate();
            String subjectEdited = StringUtils.dateFormat(emailSentDate) + " - " + eMailMessage.getSubject();
            eMailMessage.setSubject(subjectEdited);
            
            FileInputStream fis;
            Properties secondEmailProps = new Properties();
            fis = new FileInputStream("src/main/resources/email.properties");
            secondEmailProps.load(fis);

            String emailServer = secondEmailProps.getProperty("sec.email.server");
            String emailHost = secondEmailProps.getProperty("sec.email.host");
            String emailLogin = secondEmailProps.getProperty("sec.email.login");
            String emailPassword = secondEmailProps.getProperty("sec.email.password");
            
            Properties globalEmailProps = System.getProperties();
            globalEmailProps.setProperty("mail.store.protocol", "imaps");
            Session session = Session.getDefaultInstance(globalEmailProps, null);
            Store store = session.getStore("imaps");
            store.connect(emailServer, emailLogin + "@" + emailHost, emailPassword);
            //LOGGER.info(store);
            
            Folder fooFolder = store.getFolder("input");
            fooFolder.open(Folder.READ_WRITE);
            fooFolder.appendMessages(new MimeMessage[]{eMailMessage});
            fooFolder.close(false);
            store.close();        
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        
    }
    
    @ServiceActivator
    public List<Message<?>> handle(javax.mail.Message eMailMessage) {
        List<Message<?>> list = this.handleMail((MimeMessage)eMailMessage);
        //this.copyEmailToAnotherMailbox((MimeMessage)eMailMessage);
    	return list;
    }
    
    public List<Message<?>> handleMail(MimeMessage eMailMessage) {

    	List<EmailFragment> emailFragments = new ArrayList<>();
    	
        Folder folder = eMailMessage.getFolder();
        
        final List<Message<?>> messages;
        
        try {
        
            folder.open(Folder.READ_WRITE);			

            javax.mail.Message[] emailMessages = folder.getMessages();			

            FetchProfile contentsProfile = new FetchProfile();
            contentsProfile.add(FetchProfile.Item.ENVELOPE);
            contentsProfile.add(FetchProfile.Item.CONTENT_INFO);
            contentsProfile.add(FetchProfile.Item.FLAGS);     
            folder.fetch(emailMessages, contentsProfile);

            messages = new ArrayList<Message<?>>();
            
            for (int i = 0; i < emailMessages.length; i++) {	
                MimeMessage mimeMessage = (MimeMessage) emailMessages[i];	
                //mimeMessage.setFlag(Flags.Flag.DELETED, true);
                LOGGER.info("SUBJECT: " + mimeMessage.getSubject());
                Address senderAddress = mimeMessage.getFrom()[0];
                LOGGER.info("SENDER " + senderAddress.toString());
                Object content = eMailMessage.getContent();
                Multipart multipart = null;
                if(content instanceof Multipart) {
                    multipart = (Multipart) content;
                    extractDetailsAndDownload(eMailMessage, multipart, mimeMessage, emailFragments);
                } else if(content instanceof String) {
                    extractDetailsAndDownload(eMailMessage, multipart, mimeMessage, emailFragments);
                } else {

                }
                for (EmailFragment emailFragment : emailFragments) {
                    Message<?> message = MessageBuilder.withPayload(emailFragment.getData())
                                                    .setHeader(FileHeaders.FILENAME, emailFragment.getFilename())
                                                    .setHeader("directory", emailFragment.getDirectory())
                                                    .build();
                    messages.add(message);
                }
            }  
            folder.close(true);
        } catch (MessagingException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

        return messages;
    }

    private void extractDetailsAndDownload(javax.mail.Message message, Multipart multipart, MimeMessage mimeMessage, List<EmailFragment> fragments) throws MessagingException, IOException {

    	String emailSubject = message.getSubject();
        String emailFrom = ((InternetAddress)(message.getFrom()[0])).getAddress();
        Date emailSentDate = message.getSentDate();        
        
        String directoryName = StringUtils.dateFormat(emailSentDate) + " - " +
		        					emailFrom +	" - " + EmailUtils.checkEmailSubject(emailSubject);
        directoryName = EmailUtils.checkEmailDirectory(directoryName).trim();
        
        //create new directory
        File directory = new File(directoryName);
        
        if(multipart == null) {
        	fragments.add(new EmailFragment(directory, "message.txt", message.getContent()));
        	return;
        }
        
        LOGGER.info("Mail has " + multipart.getCount() + " elements.");
        
        Object content;
        String fileName;
        BodyPart bodyPart;
        String disposition;
        for (int j = 0; j < multipart.getCount(); j++) {

            bodyPart = multipart.getBodyPart(j);      
            
            disposition = bodyPart.getDisposition();

            if (disposition != null && Part.ATTACHMENT.equalsIgnoreCase(disposition)) { // BodyPart.ATTACHMENT doesn't work for gmail
            	//Download mail attachments
            	LOGGER.info("Mail has some attachments");                

    			content = emailBodypartToFile(bodyPart);
    			
    			fragments.add(new EmailFragment(directory, EmailUtils.chechFileNameCoding(bodyPart.getFileName()), content));
            } else {
//                Log mail contents
            	content = bodyPart.getContent();
            	if (content instanceof Multipart) {
    				Multipart innerMultipart = (Multipart) content;
    				extractDetailsAndDownload(message, innerMultipart, null, fragments);
    			} else {
    				fileName = bodyPart.getFileName();
    				fileName = EmailUtils.chechFileNameCoding(fileName); 
    				if(fileName == null) {
	    				if (bodyPart instanceof MimeBodyPart) {
	    					
	    				    String contentType;
	    					try {
	    						contentType = bodyPart.getContentType();
	    					}
	    					catch (MessagingException e) {
	    						throw new IllegalStateException("Unable to retrieve body part meta data.", e);
	    					}	    					
	    					
	    						String messageType = "txt";
	    						int index = contentType.indexOf(";");
	    						if(index != (-1)) {
	    							String messageTypeStr = contentType.substring(0, index);
	    							switch(messageTypeStr) {
		    							case "text/html":
															messageType = "html";
															break;
		    							case "text/plain":
		    												messageType = "txt";
		    												break;
		    						}
	    						}   						
	
	    						fileName = "Message" + j + "." + messageType;
	    				}
    				}
    				fragments.add(new EmailFragment(directory, fileName, content));
    			}  	 
            }         
        }
    }
    
    private File emailBodypartToFile(BodyPart bodyPart) throws MessagingException, IOException {
    	String fileName = bodyPart.getFileName();
    	fileName = EmailUtils.chechFileNameCoding(fileName);    	
        File file = new File(fileName);
		InputStream in = ((MimeBodyPart) bodyPart).getInputStream();
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(file));
			byte[] buf = new byte[8192];
			int len;
			while ((len = in.read(buf)) > 0)
				out.write(buf, 0, len); 
		} finally {
			// close streams, but don't mask original exception, if any
			try {
				if (in != null)
					in.close();
			} catch (IOException ex) { }
			try {
			    if (out != null)
			    	out.close();
			} catch (IOException ex) { }
		}		
		return file;
    }
    
}

package com.apu.emailloader.email;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.SearchTerm;

import org.springframework.integration.mail.SearchTermStrategy;

class AcceptAllSearchTermStrategy implements SearchTermStrategy {
    
    @Override
    public SearchTerm generateSearchTerm(Flags flags, Folder folder) {
        return new AcceptAllSearchTerm();
    }
    
    private class AcceptAllSearchTerm extends SearchTerm {
    
        public boolean match(Message mesg){
            try {
                if(mesg.isSet(javax.mail.Flags.Flag.SEEN) == true)
                    return false;
            } catch (MessagingException e1) { }
            return true;
        }
    }
}

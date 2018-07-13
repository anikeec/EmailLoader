package com.apu.emailloader.email;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.search.AndTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.SearchTerm;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.integration.mail.SearchTermStrategy;

class AcceptAllSearchTermStrategy implements SearchTermStrategy {
    
    private static Logger LOGGER = LogManager.getLogger(AcceptAllSearchTermStrategy.class.getName());
    
    @Override
    public SearchTerm generateSearchTerm(Flags supportedFlags, Folder folder) {
        //return new AcceptAllSearchTerm();
        SearchTerm searchTerm = null;
        boolean recentFlagSupported = false;
        if (supportedFlags != null) {
            recentFlagSupported = supportedFlags.contains(Flags.Flag.RECENT);
            if (recentFlagSupported) {
                searchTerm = new FlagTerm(new Flags(Flags.Flag.RECENT), true);
            }
            if (supportedFlags.contains(Flags.Flag.ANSWERED)) {
                NotTerm notAnswered = new NotTerm(new FlagTerm(new Flags(Flags.Flag.ANSWERED), true));
                if (searchTerm == null) {
                    searchTerm = notAnswered;
                }
                else {
                    searchTerm = new AndTerm(searchTerm, notAnswered);
                }
            }
            if (supportedFlags.contains(Flags.Flag.DELETED)) {
                NotTerm notDeleted = new NotTerm(new FlagTerm(new Flags(Flags.Flag.DELETED), true));
                if (searchTerm == null) {
                    searchTerm = notDeleted;
                }
                else {
                    searchTerm = new AndTerm(searchTerm, notDeleted);
                }
            }
            if (supportedFlags.contains(Flags.Flag.SEEN)) {
                NotTerm notSeen = new NotTerm(new FlagTerm(new Flags(Flags.Flag.SEEN), true));
                if (searchTerm == null) {
                    searchTerm = notSeen;
                }
                else {
                    searchTerm = new AndTerm(searchTerm, notSeen);
                }
            }
        }

        if (!recentFlagSupported) {
            NotTerm notFlagged = null;
            /*
            if (folder.getPermanentFlags().contains(Flags.Flag.USER)) {
                LOGGER.debug("This email server does not support RECENT flag, but it does support " +
                        "USER flags which will be used to prevent duplicates during email fetch." +
                        " This receiver instance uses flag: " + getUserFlag());
                Flags siFlags = new Flags();
                siFlags.add(getUserFlag());
                notFlagged = new NotTerm(new FlagTerm(siFlags, true));
            }*/
            //else {
                LOGGER.debug("This email server does not support RECENT or USER flags. " +
                        "System flag 'Flag.FLAGGED' will be used to prevent duplicates during email fetch.");
                notFlagged = new NotTerm(new FlagTerm(new Flags(Flags.Flag.FLAGGED), true));
            //}
            if (searchTerm == null) {
                searchTerm = notFlagged;
            }
            else {
                searchTerm = new AndTerm(searchTerm, notFlagged);
            }
        }
        return searchTerm;
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

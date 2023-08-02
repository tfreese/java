package de.freese.sonstiges.imap;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.mail.Authenticator;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class MailReader implements AutoCloseable {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MailReader.class);

    private Session session;

    private Store store;

    @Override
    public void close() throws MessagingException {
        if (store != null) {
            store.close();
        }

        store = null;
        session = null;
    }

    public void login(String host, String user, String password) throws Exception {
        login(host, new PasswordAuthentication(user, password));
    }

    public void login(String host, PasswordAuthentication authentication) throws Exception {
        Properties props = new Properties(System.getProperties());
        props.put("mail.debug", Boolean.FALSE.toString());
        props.put("mail.event.executor", ForkJoinPool.commonPool());
        props.put("mail.host", host);

        String protocol = "imaps";
        props.put("mail.store.protocol", protocol);

        if ("imaps".equals(protocol)) {
            props.put("mail.imaps.port", "993");
            props.put("mail.imaps.auth", "true");
            props.put("mail.imaps.ssl.enable", "true");
            props.put("mail.imaps.ssl.checkserveridentity", "true");
        }
        else {
            // imap = ohne SSL
            props.put("mail.imap.host", host);
            props.put("mail.imap.auth", "true");
            props.put("mail.imap.starttls.enable", "true");
        }

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return authentication;
            }
        };

        session = Session.getInstance(props, authenticator);
        // session.setDebug(false); // Wird mit "mail.debug" gesteuert

        // Wird mit "mail.store.protocol" gesteuert.
        store = session.getStore();

        // store.connect(null, null, null);
        store.connect();

        LOGGER.info("connection established to: {}", host);
    }

    public void read(String folderName, Function<Folder, List<Message>> messageSelector, Consumer<Message> messageHandler) throws Exception {
        LOGGER.info("reading mails: {}", folderName);

        Folder folder = null;

        //        folder = store.getDefaultFolder();
        //        LOGGER.info("Getting the default folder: {}", folder.getFullName());
        //
        //        // Alle Folder anzeigen.
        //        for (Folder f : folder.list("*"))
        //        {
        //            if ((f.getType() & Folder.HOLDS_MESSAGES) != 0)
        //            {
        //                LOGGER.info("{}: {}", f.getFullName(), f.getMessageCount());
        //            }
        //        }

        folder = store.getFolder(folderName);

        if (folder == null) {
            LOGGER.warn("Folder not exist: {}", folderName);

            return;
        }

        if ((folder.getType() & Folder.HOLDS_MESSAGES) == 0) {
            LOGGER.warn("Folder can not contain messages: {}", folderName);
            folder.close();

            return;
        }

        try {
            // checkRead
            if (!folder.isOpen()) {
                folder.open(Folder.READ_ONLY);
            }

            List<Message> messages = messageSelector.apply(folder);

            if (messages == null) {
                return;
            }

            for (Message message : messages) {
                messageHandler.accept(message);
            }
        }
        finally {
            try {
                if ((folder != null) && folder.isOpen()) {
                    folder.close(false);
                }
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }
}

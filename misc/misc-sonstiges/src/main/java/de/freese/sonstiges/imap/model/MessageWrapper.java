package de.freese.sonstiges.imap.model;

import java.io.OutputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.UIDFolder;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.imap.textpart.AbstractTextPart;
import de.freese.sonstiges.imap.textpart.HtmlTextPart;
import de.freese.sonstiges.imap.textpart.PlainTextPart;

/**
 * @author Thomas Freese
 */
public class MessageWrapper {
    private static final Set<Character> ASCII_CHARS_KEEP = Set.of(
            // 228
            'ä',
            // 196
            'Ä',
            // 252
            'ü',
            // 220
            'Ü',
            // 246
            'ö',
            // 214
            'Ö',
            // 223
            'ß');
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageWrapper.class);

    private static String getMessageId(final Message message) throws Exception {
        String messageId = null;

        if (message instanceof MimeMessage mm) {
            messageId = mm.getMessageID();
        }

        if (messageId == null) {
            String[] messageIdHeader = message.getHeader("Message-ID");

            if (messageIdHeader != null && messageIdHeader.length > 0) {
                messageId = messageIdHeader[0];
            }
        }

        if (messageId == null) {
            Date date = message.getReceivedDate() != null ? message.getReceivedDate() : message.getSentDate();

            LOGGER.warn("no messageId, generating one: {} - {} - {}", date, message.getSubject(), message.getFrom());

            messageId = Optional.ofNullable(message.getFrom()).map(addresses -> addresses[0]).map(InternetAddress.class::cast).map(InternetAddress::getAddress).orElse("");

            if (date != null) {
                Instant instant = date.toInstant();
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                messageId += "-" + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(localDateTime);
            }

            messageId += "-" + message.getSubject().replace("  ", "").replace(" ", "_");
            messageId += "-" + message.getSize();
            messageId += "-generated";
        }

        return messageId;
    }

    private static List<AbstractTextPart> getTextParts(final Part part) throws Exception {
        List<AbstractTextPart> textParts = new ArrayList<>();

        if (part.isMimeType("text/*")) {
            if (!(part.getContent() instanceof String text)) {
                return Collections.emptyList();
            }

            if (part.isMimeType("text/plain")) {
                textParts.add(new PlainTextPart(text));
            }
            else if (part.isMimeType("text/html")) {
                textParts.add(new HtmlTextPart(text));
            }
        }
        else if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();

            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);

                List<AbstractTextPart> tp = getTextParts(bp);

                textParts.addAll(tp);
            }
        }

        return textParts;
    }

    private static String removeNonAscii(final String input) {
        if (input == null || input.isBlank()) {
            return "";
        }

        int pos = 0;
        char[] chars = input.toCharArray();

        for (char c : chars) {
            if (!ASCII_CHARS_KEEP.contains(c) && (c < 32 || c > 126)) {
                continue;
            }

            chars[pos++] = c;
        }

        return new String(chars, 0, pos);
    }

    private final String folderName;

    private final Message message;

    private final String messageId;

    private boolean isSpam;

    public MessageWrapper(final Message message) throws Exception {
        this(message, message.getFolder().getName());
    }

    public MessageWrapper(final Message message, final String folderName) throws Exception {
        super();

        this.message = Objects.requireNonNull(message, "message required");
        this.folderName = Objects.requireNonNull(folderName, "folderName required");
        this.messageId = getMessageId(message);
    }

    public Date getDate() throws MessagingException {
        return message.getReceivedDate() != null ? message.getReceivedDate() : message.getSentDate();
    }

    public String getFolderName() {
        return folderName;
    }

    public String getFrom() throws MessagingException {
        return Optional.ofNullable(message.getFrom()).map(addresses -> addresses[0]).map(InternetAddress.class::cast).map(InternetAddress::getAddress).orElse(null);
    }

    public String getMessageId() {
        return this.messageId;
    }

    public long getMessageUid() throws Exception {
        Message msg = getMessage();
        Folder folder = msg.getFolder();

        if (folder instanceof UIDFolder uidFolder) {
            return uidFolder.getUID(msg);
        }

        return Objects.hash(folder.getName(), getDate(), msg.getSubject(), Arrays.hashCode(msg.getFrom()));
    }

    public String getSubject() throws MessagingException {
        return removeNonAscii(message.getSubject()).strip();
    }

    public List<AbstractTextPart> getTextParts() throws Exception {
        return getTextParts(getMessage());
    }

    public boolean isSpam() {
        return this.isSpam;
    }

    public void setSpam(final boolean isSpam) {
        this.isSpam = isSpam;
    }

    public void writeTo(final OutputStream os) throws Exception {
        getMessage().writeTo(os);
    }

    protected Message getMessage() {
        return this.message;
    }
}

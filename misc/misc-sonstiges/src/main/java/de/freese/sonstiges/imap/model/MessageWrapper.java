package de.freese.sonstiges.imap.model;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;

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

    private boolean isSpam;

    private String messageId;

    public MessageWrapper(final Message message) {
        this(message, message.getFolder().getName());
    }

    public MessageWrapper(final Message message, String folderName) {
        super();

        this.message = Objects.requireNonNull(message, "message required");
        this.folderName = Objects.requireNonNull(folderName, "folderName required");
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

    public Message getMessage() {
        return this.message;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public String getSubject() throws MessagingException {
        return removeNonAscii(message.getSubject()).strip();
    }

    public boolean isSpam() {
        return this.isSpam;
    }

    public void setMessageId(final String messageId) {
        this.messageId = messageId;
    }

    public void setSpam(final boolean isSpam) {
        this.isSpam = isSpam;
    }
}

package de.freese.sonstiges.imap.supplier;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;

import org.eclipse.angus.mail.imap.IMAPFolder;
import org.eclipse.angus.mail.imap.SortTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.imap.MailClient;
import de.freese.sonstiges.imap.model.MessageWrapper;

/**
 * @author Thomas Freese
 * @since 28.08.2023
 */
public class RemoteMailSupplier implements MailSupplier {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteMailSupplier.class);

    private final Map<String, Boolean> folders;
    private final Function<Folder, List<Message>> messageSelector;

    public RemoteMailSupplier(final Map<String, Boolean> folders) {
        this(folders, folder -> {
            try {
                if (folder instanceof final IMAPFolder iFolder) {
                    // All Mails, oldest first.
                    return List.of(iFolder.getSortedMessages(new SortTerm[]{SortTerm.ARRIVAL}));
                }

                // All Mails, oldest first.
                return List.of(folder.getMessages());
            }
            catch (final MessagingException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public RemoteMailSupplier(final Map<String, Boolean> folders, final Function<Folder, List<Message>> messageSelector) {
        super();

        this.folders = Objects.requireNonNull(folders, "folders required");
        this.messageSelector = Objects.requireNonNull(messageSelector, "messageSelector required");
    }

    public void saveLocal(final Path basePath) throws Exception {
        final Consumer<MessageWrapper> messageConsumer = message -> {
            try {
                final Path path = basePath.resolve(message.getFolderName()).resolve(message.getMessageUid() + ".mail");

                if (!Files.exists(path.getParent())) {
                    Files.createDirectories(path.getParent());
                }

                LOGGER.info("save: {}", path);

                try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(path))) {
                    message.writeTo(outputStream);
                }
            }
            catch (final Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        };

        supply(messageConsumer);
    }

    @Override
    public void supply(final Consumer<MessageWrapper> messageConsumer) throws Exception {
        try (MailClient mailClient = new MailClient()) {
            mailClient.login();

            for (final Map.Entry<String, Boolean> entry : folders.entrySet()) {
                final String folderName = entry.getKey();
                final boolean isSpam = entry.getValue();

                mailClient.readRemote(folderName, messageSelector, message -> {
                    try {
                        final MessageWrapper messageWrapper = new MessageWrapper(message, folderName);
                        messageWrapper.setSpam(isSpam);
                        messageConsumer.accept(messageWrapper);
                    }
                    catch (final RuntimeException ex) {
                        throw ex;
                    }
                    catch (final Exception ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
    }
}

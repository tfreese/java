package de.freese.sonstiges.imap.supplier;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import de.freese.sonstiges.imap.MailClient;
import de.freese.sonstiges.imap.model.MessageWrapper;

/**
 * @author Thomas Freese
 * @since 28.08.2023
 */
public class LocalMailSupplier implements MailSupplier {

    private final Path basePath;
    private final Map<String, Boolean> folders;

    public LocalMailSupplier(final Map<String, Boolean> folders, final Path basePath) {
        super();

        this.folders = Objects.requireNonNull(folders, "folders required");
        this.basePath = Objects.requireNonNull(basePath, "basePath required");
    }

    @Override
    public void supply(final Consumer<MessageWrapper> messageConsumer) throws Exception {
        try (MailClient mailClient = new MailClient()) {
            for (final Map.Entry<String, Boolean> entry : folders.entrySet()) {
                final String folderName = entry.getKey();
                final boolean isSpam = entry.getValue();

                mailClient.readLocal(basePath.resolve(folderName), message -> {
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

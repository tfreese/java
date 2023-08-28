// Created: 28.08.23
package de.freese.sonstiges.imap.supplier;

import java.util.function.Consumer;

import de.freese.sonstiges.imap.model.MessageWrapper;

/**
 * @author Thomas Freese
 */
public interface MailSupplier {

    /**
     * String: Folder name
     */
    void supply(Consumer<MessageWrapper> messageConsumer) throws Exception;
}

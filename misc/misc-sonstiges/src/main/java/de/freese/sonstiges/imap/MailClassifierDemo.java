// Created: 28.08.23
package de.freese.sonstiges.imap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.mail.Folder;
import jakarta.mail.Message;

import org.eclipse.angus.mail.imap.IMAPFolder;
import org.eclipse.angus.mail.imap.SortTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.imap.bayes.Merkmal;
import de.freese.sonstiges.imap.bayes.NaiveBayesClassifier;
import de.freese.sonstiges.imap.model.MessageWrapper;
import de.freese.sonstiges.imap.model.Token;
import de.freese.sonstiges.imap.supplier.LocalMailSupplier;
import de.freese.sonstiges.imap.supplier.MailSupplier;

/**
 * @author Thomas Freese
 */
@SuppressWarnings("try")
public final class MailClassifierDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailClassifierDemo.class);

    public static void main(final String[] args) {
        final Map<String, Boolean> folders = Map.of("Spam", true, "INBOX", false, "archiv", false);
        final Path basePath = Paths.get(System.getProperty("user.home"), "db", "mails");

        try (MailRepository mailRepository = new MailRepository(basePath)) {
            final MailSupplier mailSupplier = new LocalMailSupplier(folders, basePath);
            // final MailSupplier mailSupplier = new RemoteMailSupplier(folders, folder -> MailClassifierDemo.selectMessages(folder, mailRepository));

            // final MailSupplier mailSupplier = new RemoteMailSupplier(folders);
            // rms.saveLocal(basePath);

            final List<MessageWrapper> messageWrappers = new ArrayList<>();
            mailSupplier.supply(message -> {
                try {
                    messageWrappers.add(message);
                }
                catch (RuntimeException ex) {
                    throw ex;
                }
                catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });

            final boolean isTraining = false;
            final Function<MessageWrapper, Map<String, Integer>> tokenFunction = new TokenFunction();

            for (MessageWrapper message : messageWrappers) {
                if (mailRepository.containsMessageId(message.getMessageId())) {
                    continue;
                }

                final Map<String, Integer> wordCount = tokenFunction.apply(message);

                if (wordCount == null || wordCount.isEmpty()) {
                    continue;
                }

                final double spamProbability = classifyMessage(wordCount, mailRepository);
                LOGGER.info("isSpam = {}, SpamProbability = {} %", message.isSpam(), spamProbability);

                if (isTraining) {
                    mailRepository.insertMessage(message);
                    mailRepository.insertMessageTokens(message, wordCount);
                }
            }
        }
        catch (RuntimeException ex) {
            final Throwable cause = ex.getCause();
            LOGGER.error(cause.getMessage(), cause);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static double classifyMessage(final Map<String, Integer> tokenCount, final MailRepository mailRepository) throws Exception {
        final Set<Token> tokens = mailRepository.getToken(tokenCount.keySet());

        final Set<Merkmal> merkmalVector = tokens.stream().map(token -> {
            final int weight = tokenCount.getOrDefault(token.getValue(), 1);

            return new Merkmal(token.getValue(), token.getHamCount(), token.getSpamCount(), weight);
        }).collect(Collectors.toSet());

        final NaiveBayesClassifier classifier = new NaiveBayesClassifier();
        final double spamProbability = classifier.classify(merkmalVector);

        return BigDecimal.valueOf(spamProbability * 100D).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    private static List<Message> selectMessages(final Folder folder, final MailRepository mailRepository) {
        try {
            Message[] messages = null;

            final int loadedMessages = mailRepository.countMessagesForFolder(folder.getName());

            // final SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            // messages = folder.search(searchTerm);

            if (loadedMessages > 0) {
                // Die aktuellsten n Mails.
                final int maxMessages = 10;
                final int messageCount = folder.getMessageCount();

                if (messageCount == 0) {
                    return Collections.emptyList();
                }

                final int end = messageCount;
                final int start = end - Math.min(messageCount, maxMessages) + 1;
                messages = folder.getMessages(start, end);
            }
            else {
                if (folder instanceof IMAPFolder iFolder) {
                    // Alle Mails, älteste zuerst.
                    messages = iFolder.getSortedMessages(new SortTerm[]{SortTerm.ARRIVAL});
                }
                else {
                    // Alle Mails, älteste zuerst.
                    messages = folder.getMessages();
                }
            }

            // Erst mal nur bestimmte Mail-Attribute vorladen.
            // final FetchProfile fp = new FetchProfile();
            //            fp.add(FetchProfile.Item.ENVELOPE);
            //            fp.add(UIDFolder.FetchProfileItem.UID);
            //            fp.add(IMAPFolder.FetchProfileItem.HEADERS);
            //            //fp.add(FetchProfile.Item.FLAGS);
            //            //fp.add(FetchProfile.Item.CONTENT_INFO);
            //            folder.fetch(messages, fp);

            return List.of(messages);
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private MailClassifierDemo() {
        super();
    }
}

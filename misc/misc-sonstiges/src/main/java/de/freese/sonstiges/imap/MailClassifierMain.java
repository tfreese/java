package de.freese.sonstiges.imap;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.UIDFolder;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.SortTerm;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.sonstiges.imap.analyze.FunctionNormalizeGerman;
import de.freese.sonstiges.imap.analyze.FunctionStemmer;
import de.freese.sonstiges.imap.analyze.FunctionStripNotLetter;
import de.freese.sonstiges.imap.analyze.FunctionStripSameChar;
import de.freese.sonstiges.imap.analyze.FunctionStripStopWords;
import de.freese.sonstiges.imap.bayes.Merkmal;
import de.freese.sonstiges.imap.bayes.NaiveBayesClassifier;
import de.freese.sonstiges.imap.model.MessageWrapper;
import de.freese.sonstiges.imap.model.Token;
import de.freese.sonstiges.imap.textpart.AbstractTextPart;
import de.freese.sonstiges.imap.textpart.HtmlTextPart;
import de.freese.sonstiges.imap.textpart.PlainTextPart;

/**
 * @author Thomas Freese
 */
public final class MailClassifierMain {
    
    public static final UnaryOperator<List<String>> PRE_FILTER = token -> {
        // String linkRegEx = "^((http[s]?|ftp|file):\\/)?\\/?([^:\\/\\s]+)(:([^\\/]*))?((\\/\\w+)*\\/)([\\w\\-\\.]+[^#?\\s]+)(\\?([^#]*))?(#(.*))?$";
        String linkRegEx = "^((http[s]?|ftp|file):.*)|(^(www.).*)";
        String mailRegEx = "^(.+)@(.+).(.+)$"; // ^[A-Za-z0-9+_.-]+@(.+)$

        // @formatter:off
        return token.stream()
                .map(t -> t.replace("\n", " ").replace("\r", " ")) // LineBreaks entfernen
                // .peek(System.out::println)
                .map(t -> t.split(" "))
                .flatMap(Arrays::stream)
                .parallel()
                .filter(Objects::nonNull)
                .filter(t -> !t.isBlank())
                .map(String::strip)
                .map(String::toLowerCase)
                .filter(t -> !t.matches(linkRegEx)) // URLs entfernen
                .filter(t -> !t.matches(mailRegEx)) // Mails entfernen
                // .filter(t -> !t.startsWith("http:"))
                // .filter(t -> !t.startsWith("https:"))
                // .filter(t -> !t.startsWith("ftp:"))
                // .filter(t -> !t.startsWith("file:"))
                // .filter(t -> !t.contains("@"))
                .map(FunctionStripNotLetter.INSTANCE)
                .map(t -> t.split(" "))
                .flatMap(Arrays::stream)
                .filter(Objects::nonNull)
                .filter(t -> !t.isBlank())
                .map(String::strip)
                .map(FunctionStripSameChar.INSTANCE)
                .filter(t -> t.length() > 2)
                .sorted()
                .toList()
                ;
        // @formatter:on
    };
    public static final BiFunction<List<String>, Locale, Map<String, Integer>> STEMMER_FILTER = (token, locale) -> {
        Function<String, String> functionStemmer = FunctionStemmer.get(locale);

        // @formatter:off
        return token.stream()
                .map(t -> Locale.GERMAN.equals(locale) ? FunctionNormalizeGerman.INSTANCE.apply(t) : t)
                .map(FunctionStripStopWords.getInstance())
                .map(functionStemmer).filter(t -> t.length() > 2)
                .sorted()
                // .peek(System.out::println)
                // .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.counting())); // long
                .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.summingInt(e -> 1)))
                ;
        // @formatter:on
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(MailClassifierMain.class);

    public static void main(String[] args) {
        String host;
        String user;
        String password;

        //        Console console = System.console();
        //
        //        if (console != null)
        //        {
        //            host = console.readLine("Mail Host: ");
        //            user = console.readLine("Mail User: ");
        //            password = String.valueOf(console.readPassword("Mail Password: "));
        //        }
        //        else
        //        {
        //            Scanner scanner = new Scanner(System.in);
        //            System.out.print("Mail Host: ");
        //            host = scanner.nextLine();
        //
        //            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        //            System.out.print("Mail Host: ");
        //            host = br.readLine();
        //        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        // Host
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(new JLabel("Host:"), gbc);

        JTextField hostField = new JTextField(30);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(hostField, gbc);

        // User
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(new JLabel("User:"), gbc);

        JTextField userField = new JTextField(30);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(userField, gbc);

        // Password
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(new JLabel("Password:"), gbc);

        JPasswordField passwordField = new JPasswordField(30);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(passwordField, gbc);

        hostField.setCaretPosition(0);
        hostField.requestFocus();

        int choice = JOptionPane.showConfirmDialog(null, panel, "Mail Account", JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        host = hostField.getText();
        user = userField.getText();
        password = String.valueOf(passwordField.getPassword());

        MailClassifierMain mailClassifier = new MailClassifierMain();
        mailClassifier.processMails(host, user, password, true);
    }

    private MailClassifierMain() {
        super();
    }

    public void processMails(String host, String user, String password, boolean isTraining) {
        Path dbPath = Paths.get(System.getProperty("user.home"), "db", "mails");

        try (MailReader mailReader = new MailReader();
             MailRepository mailRepository = new MailRepository(dbPath)) {
            mailRepository.createDatabaseIfNotExist();

            mailReader.login(host, user, password);

            mailReader.read("Spam", folder -> selectMessages(folder, mailRepository), message -> handleMessage(message, mailRepository, true, isTraining));
            mailReader.read("INBOX", folder -> selectMessages(folder, mailRepository), message -> handleMessage(message, mailRepository, false, isTraining));
            mailReader.read("archiv", folder -> selectMessages(folder, mailRepository), message -> handleMessage(message, mailRepository, false, isTraining));
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private double classifyMessage(MailRepository mailRepository, Map<String, Integer> wordCount) throws Exception {
        Set<Token> tokens = mailRepository.getToken(wordCount.keySet());

        Set<Merkmal> merkmalVector = tokens.stream().map(token -> {
            int weight = wordCount.getOrDefault(token.getValue(), 1);

            return new Merkmal(token.getValue(), token.getHamCount(), token.getSpamCount(), weight);
        }).collect(Collectors.toSet());

        NaiveBayesClassifier classifier = new NaiveBayesClassifier();
        double spamProbability = classifier.classify(merkmalVector);

        return BigDecimal.valueOf(spamProbability * 100D).setScale(3, RoundingMode.HALF_UP).doubleValue();
    }

    private long getMessageId(Message message) throws Exception {
        Folder folder = message.getFolder();

        if (folder instanceof UIDFolder uidFolder) {
            return uidFolder.getUID(message);
        }

        //        String[] messageIdHeader = message.getHeader("Message-ID");
        //
        //        if (messageIdHeader != null && messageIdHeader.length > 0 && messageIdHeader[0] != null)
        //        {
        //            String messageId = messageIdHeader[0];
        //
        //            return Objects.hash(messageId);
        //        }

        return Objects.hash(folder.getName(), message.getReceivedDate(), message.getSubject(), Arrays.hashCode(message.getFrom()));
    }

    private List<AbstractTextPart> getTextParts(final Part part) throws MessagingException, IOException {
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

    private void handleMessage(Message message, MailRepository mailRepository, final boolean isSpam, boolean isTraining) {
        try {
            long messageId = getMessageId(message);

            MessageWrapper messageWrapper = new MessageWrapper(message);
            messageWrapper.setMessageId(messageId);
            messageWrapper.setSpam(isSpam);

            if (isTraining && mailRepository.containsMessageId(messageId)) {
                return;
            }

            LOGGER.info("message: {} - {} - {}", messageWrapper.getReceivedDate(), messageWrapper.getSubject(), messageWrapper.getFrom());

            if (isTraining) {
                mailRepository.insertMessage(messageWrapper);
            }

            List<AbstractTextPart> textParts = getTextParts(message);

            if ((textParts == null) || textParts.isEmpty()) {
                LOGGER.warn("no text for: {} - {} - {}", messageWrapper.getReceivedDate(), messageWrapper.getSubject(), messageWrapper.getFrom());
                return;
            }

            // @formatter:off
            List<String> token = textParts.stream()
                    .map(AbstractTextPart::getText)
                    .map(t -> Jsoup.parse(t).text()) // HTML-Text extrahieren
                    .map(t -> t.split(" "))
                    .flatMap(Arrays::stream)
                    .filter(Objects::nonNull)
                    .filter(t -> !t.isBlank())
                    // peek(System.out::println)
                    .toList()
                    ;
            // @formatter:on

            if (token.isEmpty()) {
                LOGGER.warn("no token for: {} - {} - {}", messageWrapper.getReceivedDate(), messageWrapper.getSubject(), messageWrapper.getFrom());
                return;
            }

            Locale locale = FunctionStripStopWords.getInstance().guessLocale(token);

            token = PRE_FILTER.apply(token);
            Map<String, Integer> wordCount = STEMMER_FILTER.apply(token, locale);

            if (wordCount.isEmpty()) {
                return;
            }

            double spamProbability = classifyMessage(mailRepository, wordCount);
            LOGGER.info("isSpam = {}, SpamProbability = {} %", isSpam, spamProbability);

            if (isTraining) {
                mailRepository.insertMessageTokens(messageWrapper, wordCount);
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private List<Message> selectMessages(Folder folder, MailRepository mailRepository) {
        Message[] messages = null;

        try {
            int loadedMessages = mailRepository.countMessagesForFolder(folder.getName());

            // SearchTerm searchTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            // messages = folder.search(searchTerm);

            if (loadedMessages > 0) {
                // Die aktuellsten n Mails.
                int maxMessages = 10;
                int messageCount = folder.getMessageCount();

                if (messageCount == 0) {
                    return Collections.emptyList();
                }

                int end = messageCount;
                int start = end - Math.min(messageCount, maxMessages) + 1;
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
            //            FetchProfile fp = new FetchProfile();
            //            fp.add(FetchProfile.Item.ENVELOPE);
            //            fp.add(UIDFolder.FetchProfileItem.UID);
            //            fp.add(IMAPFolder.FetchProfileItem.HEADERS);
            //            //fp.add(FetchProfile.Item.FLAGS);
            //            //fp.add(FetchProfile.Item.CONTENT_INFO);
            //            folder.fetch(messages, fp);
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return List.of(messages);
    }
}

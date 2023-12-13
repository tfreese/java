package de.freese.sonstiges.imap;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import jakarta.mail.Authenticator;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public class MailClient implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailClient.class);

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

        // Controlled by "mail.debug".
        // session.setDebug(false);

        // Controlled by "mail.store.protocol".
        store = session.getStore();

        // store.connect(null, null, null);
        store.connect();

        LOGGER.info("connection established to: {}", host);
    }

    public void login() throws Exception {
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

        String host = hostField.getText();
        String user = userField.getText();
        String password = String.valueOf(passwordField.getPassword());

        login(host, user, password);
    }

    public void readLocal(Path folderPath, Consumer<Message> messageConsumer) throws Exception {
        //        String folderName = folderPath.getFileName().toString();

        List<Path> mailFiles;

        try (Stream<Path> stream = Files.list(folderPath)) {
            mailFiles = stream.sorted().filter(p -> p.toString().toLowerCase().endsWith(".mail")).toList();
        }

        Session mSession = Session.getDefaultInstance(new Properties());

        for (Path mail : mailFiles) {
            try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(mail))) {
                Message message = new MimeMessage(mSession, inputStream);

                messageConsumer.accept(message);
            }
        }
    }

    public void readRemote(String folderName, Function<Folder, List<Message>> messageSelector, Consumer<Message> messageConsumer) throws Exception {
        LOGGER.info("reading mails: {}", folderName);

        Folder folder = null;

        //        folder = store.getDefaultFolder();
        //        LOGGER.info("Getting the default folder: {}", folder.getFullName());
        //
        //        // Show all Folders.
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
                messageConsumer.accept(message);
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

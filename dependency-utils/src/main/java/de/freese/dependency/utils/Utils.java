// Created: 18.09.2019
package de.freese.dependency.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class Utils {
    /**
     * "[:]"
     */
    public static final Pattern PATTERN_DOUBLE_DOT = Pattern.compile(":", Pattern.UNICODE_CHARACTER_CLASS);
    /**
     * "[=]"
     */
    public static final Pattern PATTERN_EQUAL = Pattern.compile("=", Pattern.UNICODE_CHARACTER_CLASS);
    /**
     * "[ ]" = "\\s+" = Whitespace: one or multiple<br>
     * String[] splits = SPACE_PATTERN.split(line);
     */
    public static final Pattern PATTERN_SPACE = Pattern.compile("\\s+", Pattern.UNICODE_CHARACTER_CLASS);

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static void setupProxy() throws UnknownHostException {
        final String domain = System.getenv("userdomain");

        if (domain != null && !domain.equals(System.getProperty("DOMAIN"))) {
            return;
        }

        final InetAddress address = InetAddress.getLocalHost();
        final String canonicalHostName = address.getCanonicalHostName();

        if (canonicalHostName != null && !canonicalHostName.endsWith(System.getProperty("HOST"))) {
            return;
        }

        System.setProperty("java.net.useSystemProxies", "true");

        final String userID = System.getProperty("user.name");
        final String password = System.getProperty("PROXY_PASS");

        // -Dhttps.proxyPassword = ...
        for (final String protocol : List.of("http", "https")) {
            System.setProperty(protocol + ".proxyHost", System.getProperty("PROXY"));
            System.setProperty(protocol + ".proxyPort", "8080");
            System.setProperty(protocol + ".nonProxyHosts", "localhost|127.0.0.1|*.DOMAIN");
            System.setProperty(protocol + ".proxyUser", userID);
            System.setProperty(protocol + ".proxyPassword", password);
            System.setProperty(protocol + ".keepAlive", "true");
            System.setProperty(protocol + ".auth.preference", "BASIC");
            System.setProperty(protocol + ".auth.ntlm.domain", "DOMAIN");
        }

        // In case of Exception: java.net.ProtocolException: Server redirected too many times (20)
        // System.setProperty("http.maxRedirects", "99");
        // Default cookie manager.
        // CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_ALL));
        // String encoded = new String(Base64.encodeBase64((getHTTPUsername() + ":" + getHTTPPassword()).getBytes()));
        // con.setRequestProperty("Proxy-Authorization", "Basic " + encoded);

        java.net.Authenticator.setDefault(new java.net.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userID, password.toCharArray());
            }
        });

        // Test
        if (Boolean.getBoolean("java.net.useSystemProxies")) {
            try {
                final URI uri = URI.create("https://www.google.de");
                // final URI uri = URI.create("https://search.maven.org");

                // Print available Proxies for the URL.
                final List<Proxy> proxies = ProxySelector.getDefault().select(uri);
                proxies.forEach(p -> LOGGER.info("Proxy: {}", p));

                // SocketAddress proxyAddress = new InetSocketAddress("194.114.63.23", 8080);
                // final Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
                final Proxy proxy = proxies.getFirst();

                final URLConnection connection = uri.toURL().openConnection(proxy);
                // final URLConnection connection = url.openConnection();

                try (InputStream response = connection.getInputStream();
                     InputStreamReader inputStreamReader = new InputStreamReader(response, StandardCharsets.UTF_8);
                     BufferedReader br = new BufferedReader(inputStreamReader);
                     Stream<String> stream = br.lines()) {
                    stream.forEach(LOGGER::info);
                    // String line = null;
                    //
                    // while ((line = br.readLine()) != null) {
                    //     LOGGER.info(line);
                    // }
                }

                ((HttpURLConnection) connection).disconnect();
            }
            catch (final Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }

    public static void shutdown(final ExecutorService executorService, final Logger logger) {
        logger.info("shutdown ExecutorService");

        if (executorService == null) {
            logger.warn("ExecutorService is null");

            return;
        }

        executorService.shutdown();

        try {
            // Wait a while for existing tasks to terminate.
            if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                logger.warn("Timed out while waiting for ExecutorService");

                // Cancel currently executing tasks.
                executorService.shutdownNow().stream()
                        .filter(Future.class::isInstance)
                        .map(Future.class::cast)
                        .forEach(future -> future.cancel(true))
                ;

                // Wait a while for tasks to respond to being cancelled.
                if (!executorService.awaitTermination(3, TimeUnit.SECONDS)) {
                    logger.error("ExecutorService did not terminate");
                } else {
                    logger.info("ExecutorService terminated");
                }
            } else {
                logger.info("ExecutorService terminated");
            }
        }
        catch (InterruptedException _) {
            logger.warn("Interrupted while waiting for ExecutorService");

            // (Re-)Cancel if current thread also interrupted.
            executorService.shutdownNow();

            // Restore interrupted state.
            Thread.currentThread().interrupt();
        }
    }

    public static String toSource(final Path path) {
        if (path.getNameCount() > 2) {
            return path.subpath(2, path.getNameCount()).toString();
        }

        return path.toString();
    }

    private Utils() {
        super();
    }
}

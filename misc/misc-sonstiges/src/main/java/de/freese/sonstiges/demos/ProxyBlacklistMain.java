// Created: 28.08.2015
package de.freese.sonstiges.demos;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URI;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generator für eine Proxy Blacklist.
 *
 * @author Thomas Freese
 */
public final class ProxyBlacklistMain {
    private static final CompletionService<Set<String>> COMPLETION_SERVICE = new ExecutorCompletionService<>(ForkJoinPool.commonPool());
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyBlacklistMain.class);

    private static final class HostComparator implements Comparator<String> {
        @Override
        public int compare(final String o1, final String o2) {
            final String s1 = new StringBuilder(o1).reverse().toString();
            final String s2 = new StringBuilder(o2).reverse().toString();

            return s1.compareTo(s2);
        }
    }

    public static void main(final String[] args) throws Exception {
        final ProxyBlacklistMain bl = new ProxyBlacklistMain();

        final Path tempDirPath = Paths.get(System.getProperty("java.io.tmpdir"));
        final Path blackListRaw = tempDirPath.resolve("blackList.txt");

        Files.createDirectories(blackListRaw.getParent());
        Set<String> blackList;

        if (Files.notExists(blackListRaw)) {
            blackList = new HashSet<>();

            // Set<String> ips = bl.loadIpBlacklist();
            // ips = bl.filter(ips);
            // blackList.addAll(bl.ipToHostname(ips));

            blackList.addAll(bl.loadHostBlacklist());

            bl.writeBlacklist(blackList.stream().sorted().toList(), blackListRaw);
        }
        else {
            blackList = bl.load(blackListRaw.toUri());
        }

        LOGGER.info("BlackList Size: {}", blackList.size());

        // Hostnamen filtern und normalisieren.
        blackList = bl.filter(blackList);
        LOGGER.info("after filter Size: {}", blackList.size());

        System.out.println();
        blackList.stream().filter(line -> line.contains("cloudflare")).map("127.0.0.1 "::concat).sorted().forEach(System.out::println);
        // System.out.println();
        // blackList.stream().filter(line -> line.contains("facebook")).map("127.0.0.1 "::concat).sorted().forEach(System.out::println);
        System.out.println();
        blackList.stream().filter(line -> line.contains("google")).map("127.0.0.1 "::concat).sorted().forEach(System.out::println);
        System.out.println();
        blackList.stream().filter(line -> line.contains("tiqcdn")).map("127.0.0.1 "::concat).sorted().forEach(System.out::println);
        System.out.println();
        blackList.stream().filter(line -> line.contains("twitter")).map("127.0.0.1 "::concat).sorted().forEach(System.out::println);
        System.out.println();
        blackList.stream().filter(line -> line.contains("yahoo")).map("127.0.0.1 "::concat).sorted().forEach(System.out::println);

        // Path privoxySkriptPath = Paths.get(System.getProperty("user.home"), "dokumente", "linux", "proxy");
        // bl.createPrivoxyBlacklist(privoxySkriptPath, tempDirPath);
    }

    private ProxyBlacklistMain() {
        super();
    }

    /**
     * Erstellt die BlackList von AdBlockPlus.
     */
    void createPrivoxyBlacklist(final Path privoxySkriptPath, final Path targetDirectory) throws Exception {
        Files.createDirectories(targetDirectory);

        final Set<String> easyList = new TreeSet<>(new HostComparator());

        final List<URI> uris = new ArrayList<>();
        uris.add(new URI("https://easylist-downloads.adblockplus.org/easylist.txt"));
        uris.add(new URI("https://easylist-downloads.adblockplus.org/easylistgermany.txt"));
        uris.add(new URI("https://easylist-downloads.adblockplus.org/easyprivacy.txt"));
        uris.add(new URI("https://easylist-downloads.adblockplus.org/antiadblockfilters.txt"));
        uris.add(new URI("https://easylist-downloads.adblockplus.org/easyprivacy_nointernational.txt"));
        uris.add(new URI("https://easylist-downloads.adblockplus.org/easyprivacy.txt"));
        // uris.add(new URI("https://easylist-downloads.adblockplus.org/malwaredomains_full.txt"));
        uris.add(new URI("https://easylist-downloads.adblockplus.org/fanboy-social.txt"));
        // uris.add(new URI("https://easylist-downloads.adblockplus.org/fanboy-annoyance.txt"));

        final int tasks = uris.size();

        uris.forEach(uri -> COMPLETION_SERVICE.submit(() -> load(uri)));

        for (int i = 0; i < tasks; i++) {
            easyList.addAll(COMPLETION_SERVICE.take().get());
        }

        final Path downloadPath = targetDirectory.resolve("blacklist-adblock-download.txt");
        writeBlacklist(easyList, downloadPath);

        final Path pathScript = privoxySkriptPath.resolve("adblockplus2privoxy.sh");
        final Process process = new ProcessBuilder(pathScript.toString(), downloadPath.toString()).start();
        process.waitFor();

        // Blacklist Domain
        final Set<String> blackListDomain = new TreeSet<>(new HostComparator());
        blackListDomain.addAll(load(privoxySkriptPath.resolve("blacklist-domain.txt").toUri()));
        blackListDomain.addAll(load(privoxySkriptPath.resolve("blacklist-regex.txt").toUri()));
        blackListDomain.addAll(load(targetDirectory.resolve("privoxy-blacklist-domain.txt").toUri()));

        // Blacklist HTTP-Elements
        final Set<String> blackListElements = new TreeSet<>();
        blackListElements.addAll(load(targetDirectory.resolve("privoxy-blacklist-elements.txt").toUri()));

        // Whitelist Domain
        final Set<String> whiteListDomain = new TreeSet<>();
        blackListElements.addAll(load(targetDirectory.resolve("privoxy-whitelist-domain.txt").toUri()));

        // Whitelist Images
        final Set<String> whiteListImages = new TreeSet<>();
        blackListElements.addAll(load(targetDirectory.resolve("privoxy-whitelist-images.txt").toUri()));

        final Charset charset = StandardCharsets.UTF_8;

        // Privoxy Filter
        try (PrintWriter writer = new PrintWriter(targetDirectory.resolve("privoxy-generated.filter").toFile(), charset)) {
            writer.println("FILTER: generated Tag Filter for HTML Elements");

            for (String element : blackListElements) {
                writer.println(element);
            }
        }

        // Privoxy Action
        try (PrintWriter writer = new PrintWriter(targetDirectory.resolve("privoxy-generated.action").toFile(), charset)) {
            writer.println("{ +block{generated} }");

            for (String domain : blackListDomain) {
                writer.println(domain);
            }

            writer.println();
            writer.println("{ +filter{generated} }");
            writer.println("*");
            writer.println();
            writer.println("{ -block }");

            for (String domain : whiteListDomain) {
                writer.println(domain);
            }

            writer.println();
            writer.println("{ -block +handle-as-image }");

            for (String image : whiteListImages) {
                writer.println(image);
            }
        }
    }

    /**
     * Filtert die geladene Blacklist.
     */
    Set<String> filter(final Set<String> blackList) {
        LOGGER.info("Filter BlackList");

        // Alles raus was nicht reinsoll.
        return blackList.stream()
                .parallel()
                .filter(Objects::nonNull)
                .map(String::strip)
                .filter(value -> !value.isBlank())
                .map(String::toLowerCase)
                .filter(value -> !value.startsWith("#"))
                .filter(value -> !value.startsWith("&"))
                .filter(value -> !value.startsWith("-"))
                .filter(value -> !value.contains("localhost"))
                .filter(value -> !value.contains("htpc"))
                .filter(value -> !value.contains("zbox"))
                .filter(value -> !value.contains("raspi"))
                .filter(value -> !value.contains("255.255.255.255"))
                .map(value -> value.replace("0.0.0.0", ""))
                .map(value -> value.replace("127.0.0.1", ""))
                .map(value -> value.replace("www.", ""))
                .map(value -> value.replace("\\.", "."))
                .map(value -> {
                    // 0.0.0.0 undertonenetworks.com #[zedo.com]
                    final int lastIndex = value.lastIndexOf("#");

                    if (lastIndex >= 0) {
                        return value.substring(0, lastIndex).strip();
                    }

                    return value;
                })
                .map(value -> {
                    // 0.0.0.0 undertonenetworks.com/something
                    final int lastIndex = value.lastIndexOf("/");

                    if (lastIndex >= 0) {
                        return value.substring(0, lastIndex).strip();
                    }

                    return value;
                })
                .map(value -> {
                    if (value.startsWith(".")) {
                        return value.substring(1).strip();
                    }

                    return value;
                })
                .map(value -> {
                    if (value.endsWith(".") || value.endsWith(",")) {
                        return value.substring(0, value.length() - 1).strip();
                    }

                    return value;
                })
                .map(String::strip) // Nochmal Abschliessend filtern
                .filter(value -> !value.isBlank())
                .collect(Collectors.toSet())
                ;
    }

    /**
     * BlackList mit Regex ausdünnen.
     */
    Set<String> filterByRegEx(final Path privoxySkriptPath, final Set<String> blackList) throws Exception {
        final Path path = privoxySkriptPath.resolve("blacklist-regex.txt");

        final Set<String> regexList = load(path.toUri());

        final Set<String> set = validateRegex(blackList, regexList);
        LOGGER.info("after validateRegex Size: {}", set.size());

        return set;
    }

    /**
     * Wandelt IP-Addressen in Hostnamen um.<br>
     * Falls das fehlschlägt, wird die IP entfernt.
     */
    Set<String> ipToHostname(final Set<String> hosts) {
        LOGGER.info("IP -> Hostname");

        final Map<String, String> cache = new HashMap<>();

        return hosts.stream()
                .parallel()
                .filter(Objects::nonNull)
                .map(host -> {
                    if (StringUtils.containsOnly(host, ".0123456789")) {
                        String hostName = cache.get(host);

                        if ("null".equals(hostName)) {
                            return null;
                        }

                        if (hostName != null) {
                            return hostName;
                        }

                        try {
                            hostName = InetAddress.getByName(host).getHostName();
                        }
                        catch (UnknownHostException ex) {
                            // Ignore
                            cache.put(host, "null");

                            return null;
                        }

                        if (StringUtils.containsOnly(hostName, ".0123456789")) {
                            // IP -> Keine Namensauflösung möglich.
                            cache.put(host, "null");

                            return null;
                        }

                        LOGGER.info("{} -> {}", host, hostName);

                        cache.put(host, hostName.strip());

                        return hostName.strip();
                    }

                    return host;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet())
                ;
    }

    /**
     * Laden einer Plaintext Liste.
     */
    Set<String> load(final URI uri) {
        LOGGER.info("Load: {}", uri);

        final Set<String> set = new HashSet<>();

        try {
            if ("file".equals(uri.getScheme())) {
                try (Stream<String> lines = Files.lines(Paths.get(uri))) {
                    lines.forEach(set::add);
                }
            }
            else {
                final URLConnection connection = uri.toURL().openConnection();

                try (InputStream is = connection.getInputStream();
                     BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                    try (Stream<String> lines = br.lines()) {
                        lines.forEach(set::add);
                    }
                }
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage());
        }

        return set;
    }

    /**
     * Laden der allgemeinen BlackList.
     */
    Set<String> loadHostBlacklist() throws Exception {
        final List<URI> uris = new ArrayList<>();
        uris.add(new URI("https://someonewhocares.org/hosts/hosts"));
        uris.add(new URI("https://winhelp2002.mvps.org/hosts.txt"));
        uris.add(new URI("https://pgl.yoyo.org/adservers/serverlist.php?hostformat=nohtml&showintro=0"));

        int tasks = uris.size();

        uris.forEach(uri -> COMPLETION_SERVICE.submit(() -> load(uri)));

        // Mit diesen wächst die Blacklist auf über 1,5 Mio. !!!
        final List<URI> uriTars = new ArrayList<>();
        // uriTars.add(new URI("https://www.shallalist.de/Downloads/shallalist.tar.gz"));
        // uriTars.add(new URI("https://urlblacklist.com/cgi-bin/commercialdownload.pl?type=download&file=bigblacklist"));

        uriTars.forEach(uri -> COMPLETION_SERVICE.submit(() -> loadTGZ(uri)));

        tasks += uriTars.size();

        final Set<String> blackList = new HashSet<>();

        for (int i = 0; i < tasks; i++) {
            blackList.addAll(COMPLETION_SERVICE.take().get());
        }

        return blackList;
    }

    /**
     * Laden der IP-BlackList.
     */
    Set<String> loadIpBlacklist() throws Exception {
        final List<URI> uris = new ArrayList<>();
        uris.add(new URI("https://myip.ms/files/blacklist/general/latest_blacklist.txt"));

        final int tasks = uris.size();

        uris.forEach(uri -> COMPLETION_SERVICE.submit(() -> load(uri)));

        final Set<String> blackList = new HashSet<>();

        for (int i = 0; i < tasks; i++) {
            blackList.addAll(COMPLETION_SERVICE.take().get());
        }

        return blackList;
    }

    /**
     * Laden einer TGZ komprimierten Datei.
     */
    Set<String> loadTGZ(final URI uri) throws IOException {
        LOGGER.info("Download: {}", uri);

        final Set<String> set = new HashSet<>();

        try (InputStream is = uri.toURL().openStream();
             GZIPInputStream gzipIs = new GZIPInputStream(is);
             TarArchiveInputStream tarIs = new TarArchiveInputStream(gzipIs);
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(tarIs, StandardCharsets.UTF_8))) {
            TarArchiveEntry entry = null;

            try {
                while ((entry = tarIs.getNextEntry()) != null) {
                    if (entry.isFile() && tarIs.canReadEntryData(entry) && entry.getName().endsWith("domains")) {
                        bufferedReader.lines().forEach(set::add);
                    }
                }
            }
            catch (EOFException ex) {
                LOGGER.error(ex.getMessage());
            }
        }

        return set;
    }

    /**
     * Entfernt Hosts, welche durch die Regex-Liste schon erfasst werden.
     */
    Set<String> validateRegex(final Set<String> blackList, final Set<String> regexList) {
        LOGGER.info("Validate Regex");

        return blackList.stream()
                .parallel()
                .filter(Objects::nonNull)
                .filter(host -> {
                    boolean match = false;

                    for (String regex : regexList) {
                        if (!StringUtils.startsWith(host, ".*")) {
                            regex = ".*" + regex;
                        }

                        if (host.matches(regex)) {
                            match = true;
                            break;
                        }
                    }

                    return !match;
                })
                .collect(Collectors.toSet());
    }

    /**
     * Speichert die BlackList.
     */
    void writeBlacklist(final Collection<String> blackList, final Path path) throws IOException {
        LOGGER.info("Write: {}", path);

        try (PrintWriter writer = new PrintWriter(path.toFile(), StandardCharsets.UTF_8)) {
            for (String host : blackList) {
                writer.printf("%s%n", host);
            }
        }
    }
}

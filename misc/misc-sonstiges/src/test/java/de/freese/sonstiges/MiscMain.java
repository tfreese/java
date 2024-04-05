package de.freese.sonstiges;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.Pipe;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.text.Collator;
import java.text.NumberFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import de.freese.sonstiges.xml.jaxb.model.DJ;

/**
 * @author Thomas Freese
 */
public final class MiscMain {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();
    private static final Logger LOGGER = LoggerFactory.getLogger(MiscMain.class);

    public static void main(final String[] args) throws Throwable {
        // System.out.println("args = " + Arrays.deepToString(args));
        // System.out.printf("%s: %s.%s%n", Thread.currentThread().getName(), "de.freese.sonstiges.MiscMain", "main");

        // System.out.println(generatePW(SecureRandom.getInstanceStrong(), "lllll_UUUUU_dddddd."));

        // SecureRandom.getInstanceStrong()
        //         .ints(1000, 0, 10)
        //         .boxed()
        //         .collect(Collectors.groupingBy(Function.identity(), TreeMap::new, Collectors.toList()))
        //         .forEach((key, value) -> {
        //                     System.out.println(key + " - " + value.size());
        //                 }
        //         );

        // bitShift();
        // bitValue();
        // byteBuffer();
        // collator();
        // copyPipedStreamsInToOut();
        // copyPipedStreamsOutToIn();
        // dateTime();
        // fileWalker();
        // fileSystems();
        // hostName();
        // httpRedirect();
        // introspector();
        // javaVersion();
        // jndi();
        // listDirectories();
        // nioPipeChannels();
        // processBuilder();
        // reactor();
        // reactorParallel();
        // reactorStream();
        // reactorSinks();
        // reflection();
        // securityProviders();
        // streamParallelCustomThreadPool();
        // showMemory();
        // showWindowsNotification();
        // splitList();
        // systemMXBean();
        // textBlocks();
        // utilLogging();
        // virtualThreads();
        // zip();

        Schedulers.shutdownNow();
        EXECUTOR_SERVICE.shutdown();
    }

    static void artistsWithOnlyOneSubdir() throws Exception {
        final Path basePath = Paths.get(System.getProperty("user.home"), "mediathek", "musik");

        try (Stream<Path> stream = Files.list(basePath)) {
            stream.filter(Files::isDirectory)
                    .filter(p -> {
                        long subFolder = 0;

                        try (Stream<Path> subStream = Files.list(p)) {
                            subFolder = subStream
                                    .filter(Files::isDirectory)
                                    .count();
                        }
                        catch (Exception ex) {
                            // Ignore
                        }

                        return subFolder == 1;
                    })
                    .sorted()
                    .forEach(path -> LOGGER.info("{}", path));
        }
    }

    static void bitShift() {
        //        MAP.put("A", new byte[]{126, 9, 9, 9, 126});

        //        for (byte b : new byte[]{126, 9, 9, 9, 126}) {
        //            for (int j = 0; j < 7; j++) {
        //                if ((b & (1 << j)) != 0) {
        //                    System.out.println("b & (1 << j) = " + (b & (1 << j)));
        //                }
        //            }
        //        }

        for (int n = 0; n < 40; n++) {
            System.out.printf("n = %d%n", n);

            // Liefert den höchsten Wert (power of 2), der kleiner als n ist.
            final int nn = Integer.highestOneBit(n);

            System.out.printf("Integer.highestOneBit = %d%n", nn);

            for (int i = 0; i < 3; i++) {
                // << 1: Bit-Shift nach links, vergrößert um power of 2; 1,2,4,8,16,32,...
                // >> 1: Bit-Shift nach rechts, verkleinert um power of 2; ...,32,16,8,4,2,1
                System.out.printf("%d << %d = %d;   %d >> %d = %d%n", nn, i, nn << i, nn, i, nn >> i);
            }

            System.out.println();
        }

        for (int parallelism : List.of(32, 24, 16, 8, 4, 2)) {
            System.out.printf("highestOneBit: %2d << 4 = %3d%n", parallelism, Integer.highestOneBit(parallelism) << 4);
        }
    }

    static void bitValue() {
        final int enabled = 1;
        final int disabled = 2;
        final int visible = 4;

        final LongPredicate isEnabled = i -> (i & enabled) == enabled;
        final LongPredicate isDisabled = i -> (i & disabled) == disabled;
        final LongPredicate isVisible = i -> (i & visible) == visible;

        long value = enabled;
        System.out.printf("%d: isEnabled=%b, isDisabled=%b, isVisible=%b%n", value, isEnabled.test(value), isDisabled.test(value), isVisible.test(value));

        value = enabled | visible;
        System.out.printf("%d: isEnabled=%b, isDisabled=%b, isVisible=%b%n", value, isEnabled.test(value), isDisabled.test(value), isVisible.test(value));

        value = disabled | visible;
        System.out.printf("%d: isEnabled=%b, isDisabled=%b, isVisible=%b%n", value, isEnabled.test(value), isDisabled.test(value), isVisible.test(value));

        value = visible;
        System.out.printf("%d: isEnabled=%b, isDisabled=%b, isVisible=%b%n", value, isEnabled.test(value), isDisabled.test(value), isVisible.test(value));

        value = enabled | disabled | visible;
        System.out.printf("%d: isEnabled=%b, isDisabled=%b, isVisible=%b%n", value, isEnabled.test(value), isDisabled.test(value), isVisible.test(value));
    }

    static void byteBuffer() throws Exception {
        final CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder();
        final CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();

        final String text = "Hello World !";
        System.out.printf("Original: '%s'%n", text);

        // To Base64
        final CharBuffer charBuffer = CharBuffer.allocate(128);
        charBuffer.put(text);
        charBuffer.flip();

        // CharBuffer in UTF8 kodieren.
        ByteBuffer byteBuffer = encoder.encode(charBuffer);

        // Zwischen-Ausgabe -> flip() nicht vergessen zum Rücksetzen des ByteBuffers.
        System.out.printf("ByteBuffer after encode: '%s'%n", StandardCharsets.UTF_8.decode(byteBuffer));
        byteBuffer.flip();

        // ByteBuffer in Base64 umwandeln.
        byteBuffer = Base64.getEncoder().encode(byteBuffer);

        // ByteBuffer mit UTF8 in CharBuffer umwandeln.
        final String base64String = decoder.decode(byteBuffer).toString();
        System.out.printf("as Base64: '%s'%n", base64String);

        // From Base64
        charBuffer.clear();
        charBuffer.put(base64String);
        charBuffer.flip();

        // CharBuffer in UTF8 kodieren.
        byteBuffer = encoder.encode(charBuffer);

        // Zwischen-Ausgabe -> flip() nicht vergessen zum Rücksetzen des ByteBuffers
        System.out.printf("ByteBuffer after encode: '%s'%n", StandardCharsets.UTF_8.decode(byteBuffer));
        byteBuffer.flip();

        // ByteBuffer in Base64 umwandeln.
        byteBuffer = Base64.getDecoder().decode(byteBuffer);

        // ByteBuffer mit UTF8 in CharBuffer umwandeln.
        final String originalString = decoder.decode(byteBuffer).toString();
        System.out.printf("Original: '%s'%n", originalString);
    }

    static void collator() {
        final Collator collator = Collator.getInstance(Locale.GERMAN);
        collator.setStrength(Collator.PRIMARY);

        System.out.println("compare: " + collator.compare("4.9", "4.11"));
        System.out.println((int) '■');
    }

    static long copy(final InputStream source, final OutputStream sink, final int bufferSize) throws IOException {
        final byte[] buffer = new byte[bufferSize];
        long readTotal = 0;

        int read = 0;

        while ((read = source.read(buffer)) > 0) {
            sink.write(buffer, 0, read);
            readTotal += read;
        }

        // for (int read = 0; read >= 0; read = source.read(buffer)) {
        // sink.write(buffer, 0, read);
        // readTotal += read;
        // }

        return readTotal;
    }

    @SuppressWarnings("try")
    static void copyPipedStreamsInToOut() throws Throwable {
        // 1 MB
        final int chunk = 1024 * 1024;

        final String fileName = "archlinux-2019.11.01-x86_64.iso";
        final Path pathSource = Paths.get(System.getProperty("user.home"), "downloads", "iso", fileName);
        final Path pathTarget = Paths.get(System.getProperty("user.dir"), "target", fileName);

        Files.deleteIfExists(pathTarget);

        if (Files.notExists(pathSource)) {
            LOGGER.info("File not exist: {}", pathSource);

            return;
        }

        try (PipedInputStream pipeIn = new PipedInputStream(chunk);
             PipedOutputStream pipeOut = new PipedOutputStream(pipeIn)) {
            final AtomicReference<Throwable> referenceThrowable = new AtomicReference<>(null);

            final Runnable writeTask = () -> {
                LOGGER.info("start target copy: {}", Thread.currentThread().getName());

                try (OutputStream fileOutput = new BufferedOutputStream(Files.newOutputStream(pathTarget), chunk)) {
                    copy(pipeIn, fileOutput, chunk);
                }
                catch (Throwable th) {
                    referenceThrowable.set(th);
                }

                LOGGER.info("target copy finished: {}", Thread.currentThread().getName());
            };

            EXECUTOR_SERVICE.execute(writeTask);

            // readTask
            LOGGER.info("start source copy: {}", Thread.currentThread().getName());

            try (InputStream fileInput = new BufferedInputStream(Files.newInputStream(pathSource), chunk)) {
                copy(fileInput, pipeOut, chunk);

                pipeOut.flush();

                // Ohne dieses close würde der PipedOutputStream nicht beendet werden.
                pipeOut.close();
            }

            LOGGER.info("source copy finished: {}", Thread.currentThread().getName());

            final Throwable th = referenceThrowable.get();

            if (th != null) {
                throw th;
            }

            // Direktes kopieren auf File-Ebene, ist am schnellsten.
            // Files.copy(pathSource, pathTarget);

            // Kopieren mit Temp-Datei (java.io.tmpdir), doppelter Daten-Transfer, ist am langsamsten.
            // final Path pathTemp = Files.createTempFile("copyDocuments_" + System.nanoTime(), ".tmp");
            //
            // try {
            // try (OutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(pathTemp), chunk)) {
            // Files.copy(pathSource, outputStream);
            // }
            //
            // try (InputStream inputStream = new BufferedInputStream(Files.newInputStream(pathTemp), chunk)) {
            // Files.copy(inputStream, pathTarget);
            // }
            // }
            // finally {
            // Files.deleteIfExists(pathTemp);
            // }

            LOGGER.info("copy ... finished: {}", Thread.currentThread().getName());
        }
    }

    static void copyPipedStreamsOutToIn() throws Throwable {
        // 1 MB
        final int chunk = 1024 * 1024;

        try (PipedOutputStream pipeOut = new PipedOutputStream();
             PipedInputStream pipeIn = new PipedInputStream(pipeOut, chunk)) {
            final Runnable readTask = () -> {
                LOGGER.info("start readTask: {}", Thread.currentThread().getName());

                try {
                    final byte[] bytes = pipeIn.readAllBytes();

                    LOGGER.info("readTask finished with: {}; {}", new String(bytes, StandardCharsets.UTF_8), Thread.currentThread().getName());
                }
                catch (Exception ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            };

            EXECUTOR_SERVICE.execute(readTask);

            // writeTask
            LOGGER.info("start writeTask: {}", Thread.currentThread().getName());

            pipeOut.write("Hello World!".getBytes(StandardCharsets.UTF_8));
            pipeOut.flush();

            LOGGER.info("writeTask finished: {}", Thread.currentThread().getName());
            LOGGER.info("copy ... finished: {}", Thread.currentThread().getName());
        }
    }

    static void dateTime() {
        System.out.println("01: " + Instant.now()); // UTC time-zone
        System.out.println("02: " + Instant.ofEpochMilli(System.currentTimeMillis()) + "; " + new Date()); // UTC time-zone
        System.out.println("03: " + Clock.system(ZoneId.of("Europe/Berlin")).instant()); // UTC time-zone

        System.out.println("04: " + ZonedDateTime.now());
        System.out.println("05: " + ZonedDateTime.now().toLocalDate());
        System.out.println("06: " + ZonedDateTime.now().toLocalDateTime());
        System.out.println("07: " + ZonedDateTime.now().toLocalTime());

        System.out.println("08: " + Date.from(ZonedDateTime.now().toInstant()));
        System.out.println("09: " + Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
        System.out.println("10: " + Date.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(+2))));
        System.out.println("11: " + ZoneId.systemDefault());
        System.out.println("12: " + ZoneId.of("Europe/Berlin"));

        System.out.println("13: " + LocalTime.now());
        System.out.println("13: " + LocalDate.now());
        System.out.println("14a: " + LocalDateTime.now());
        System.out.println("14b: " + LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()));
        System.out.println("14c: " + LocalDateTime.ofEpochSecond(System.currentTimeMillis() / 1000, 0, ZoneOffset.ofHours(+2)));

        final WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = LocalDate.of(2016, Month.JANUARY, 1).get(weekFields.weekOfWeekBasedYear());
        System.out.println("15: 2016-01-01 - weekOfWeekBasedYear = " + weekNumber);
        weekNumber = LocalDate.of(2016, Month.JANUARY, 1).get(weekFields.weekOfYear());
        System.out.println("16: 2016-01-01 - weekOfYear = " + weekNumber);
        weekNumber = LocalDate.of(2014, 12, 31).get(weekFields.weekOfWeekBasedYear());
        System.out.println("17: 2014-12-31 - weekOfWeekBasedYear = " + weekNumber);
        weekNumber = LocalDate.of(2014, 12, 31).get(weekFields.weekOfYear());
        System.out.println("18: 2014-12-31 - weekOfYear = " + weekNumber);
    }

    static void embeddedJndi() {
        // SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
        // SimpleNamingContextBuilder builder =
        // SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        // builder.bind("java:comp/env/bla", "BlaBla");
        // // builder.activate();
        //
        // final Context context = new InitialContext();
        // final Object object = context.lookup("java:comp/env/bla");
        // System.out.println(object);
        //
        // builder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        // builder.bind("java:comp/env/blo", "BloBlo");
        // object = context.lookup("java:comp/env/blo");
        //
        // System.out.println(object);
    }

    static void fileSystems() throws Exception {
        final FileSystem defaultFileSystem = FileSystems.getDefault();

        for (FileStore store : defaultFileSystem.getFileStores()) {
            final long total = store.getTotalSpace() / 1024 / 1024 / 1024;
            final long used = (store.getTotalSpace() - store.getUnallocatedSpace()) / 1024 / 1024 / 1024;
            final long avail = store.getUsableSpace() / 1024 / 1024 / 1024;

            System.out.format("%-20s %8d %8d %8d%n", store, total, used, avail);
        }

        System.out.println();

        for (Path rootPath : defaultFileSystem.getRootDirectories()) {
            final FileStore fileStore = Files.getFileStore(rootPath);

            System.out.println("RootPath: " + rootPath + ", FileStore: " + fileStore);
            // if(fileStore.type().toLowerCase().contains("udf")) {
            // if(fileStore.getTotalSpace()>10000000000L) { //Blu-ray
            // return "bluray:///" + rootPath.toString();
            // }
            // else {
            // return "dvdsimple:///" + rootPath.toString(); //DVD
            // }
            // }
        }

        System.out.println();

        final FileSystemView fsv = FileSystemView.getFileSystemView();

        for (File file : File.listRoots()) {
            System.out.println("Drive Name: " + file);
            System.out.println("Display Name: " + fsv.getSystemDisplayName(file));
            System.out.println("Description: " + fsv.getSystemTypeDescription(file));
            System.out.println();
        }

        System.out.println();

        for (Path path : List.of(Paths.get("pom.xml"), Paths.get(System.getProperty("user.home"), ".xinitrc"), Paths.get(System.getProperty("user.home"), ".m2", "settings.xml"),
                Paths.get(System.getProperty("java.io.tmpdir")))) {
            System.out.println("Path: " + path + ", Size=" + Files.size(path));
            System.out.println("Path Root: " + path.getRoot());
            System.out.println("Path FileSystem: " + path.getFileSystem());

            final FileStore fileStore = Files.getFileStore(path);
            System.out.println("Path FileStore: " + fileStore.toString() + ", Name:" + fileStore.name() + ", Type: " + fileStore.type());

            System.out.println("Path Display Name: " + fsv.getSystemDisplayName(path.toFile()));
            System.out.println("Path Description: " + fsv.getSystemTypeDescription(path.toFile()));
            System.out.println(StreamSupport.stream(path.getFileSystem().getFileStores().spliterator(), false).map(FileStore::toString).collect(Collectors.joining(", ")));
            System.out.println();
        }
    }

    static void fileWalker() throws Exception {
        final Path path = Paths.get("/mnt", "mediathek", "musik", "ATC");

        LOGGER.info("Files.walk");

        try (Stream<Path> stream = Files.walk(path)) {
            //@formatter:off
            stream
                    //                .filter(p -> !Files.isDirectory(p))
                    .filter(p -> !p.toString().toLowerCase().endsWith(".jpg"))
                    .filter(p -> !p.toString().toLowerCase().endsWith(".m4b"))
                    .sorted()
                    .skip(6)
                    .limit(100)
                    .forEach(p -> LOGGER.info("{}", p))
            ;
            //@formatter:on
            // .filter(p -> !p.endsWith(".m4b"))
        }

        // Rekursiv löschen
        // Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);

        LOGGER.info("Files.list");

        try (Stream<Path> stream = Files.list(path)) {
            stream.sorted().forEach(p -> LOGGER.info("{}", p));
        }

        LOGGER.info("Files.newDirectoryStream");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            stream.forEach(p -> LOGGER.info("{}", p));
        }

        LOGGER.info("Files.walkFileTree");
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            private String indent = "";

            @Override
            public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
                if (StringUtils.isNotBlank(this.indent)) {
                    this.indent = this.indent.substring(0, this.indent.length() - 3);
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
                this.indent = this.indent + "   ";

                // System.out.println(dir);
                // System.out.println(path.relativize(dir));
                //
                // Path target = Paths.get(System.getProperty("user.dir"), "mediathek");
                // System.out.println(target.resolve(path.relativize(dir)));

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
                LOGGER.info("{}{}", this.indent, file);

                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Analog KeePass Passwort-Generator: Generiert ein Passwort mit gegebenen Pattern.<br>
     * <ul>
     * <li>l = lower-case Letters</li>
     * <li>U = upper-case Letters</li>
     * <li>d = digits</li>
     * </ul>
     * Example:<br>
     * Pattern "lllll_UUUUU_dddddd." returns "vrifa_EMFCQ_399671."<br>
     * <br>
     */
    static String generatePW(final Random random, final String pattern) {
        Objects.requireNonNull(random, "random is required");
        Objects.requireNonNull(pattern, "pattern is required");

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < pattern.length(); i++) {
            final char c = pattern.charAt(i);

            switch (c) {
                case 'l' -> sb.append((char) (97 + random.nextInt(26))); // Kleinbuchstaben
                case 'U' -> sb.append((char) (65 + random.nextInt(26))); // Großbuchstaben
                case 'd' -> sb.append(random.nextInt(10)); // Zahlen
                default -> sb.append(c);
            }
        }

        return sb.toString();
    }

    static void hostName() throws Exception {
        // System.out.println(InetAddress.getByName("5.157.15.6").getHostName());

        String hostName = null;

        try {
            hostName = InetAddress.getLocalHost().getHostName();
            System.out.printf("InetAddress.getLocalHost: %s%n", hostName);
        }
        catch (Exception ex) {
            // Bei Betriebssystemen ohne DNS-Konfiguration funktioniert InetAddress.getLocalHost nicht !
            System.out.printf("InetAddress.getLocalHost: %s%n", ex.getMessage());
        }

        // Cross Platform (Windows, Linux, Unix, Mac)
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[]{"hostname"}).getInputStream()))) {
            hostName = br.readLine();
            System.out.printf("CMD 'hostname': %s%n", hostName);
        }
        catch (Exception ex) {
            // Ignore
            System.out.printf("CMD 'hostname': %s%n", ex.getMessage());
        }

        try {
            // List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                final NetworkInterface nic = interfaces.nextElement();

                // nic.getInterfaceAddresses().forEach(System.out::println);

                // final Stream<InetAddress> addresses = nic.inetAddresses();
                final Enumeration<InetAddress> addresses = nic.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    final InetAddress address = addresses.nextElement();

                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        hostName = address.getHostName();
                        System.out.printf("NetworkInterface IPv4: %s%n", hostName);
                    }
                    else if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                        hostName = address.getHostName();
                        System.out.printf("NetworkInterface IPv6: %s%n", hostName);
                    }
                    else if (!address.isLoopbackAddress()) {
                        hostName = address.getHostName();
                        System.out.printf("NetworkInterface IPv6 Link: %s%n", hostName);
                    }
                }
            }
        }
        catch (Exception ex) {
            // Ignore
        }
    }

    static void httpRedirect() throws Exception {
        final URI uri = URI.create("http://gmail.com");

        // Ausgabe verfügbarer Proxies für eine URL.
        final List<Proxy> proxies = ProxySelector.getDefault().select(uri);
        proxies.forEach(System.out::println);

        // final SocketAddress proxyAddress = new InetSocketAddress("194.114.63.23", 8080);
        // final Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
        final Proxy proxy = proxies.get(0);

        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection(proxy);
        conn.setReadTimeout(5000);
        conn.addRequestProperty("Accept-Language", "de-DE,de;q=0.8");
        conn.addRequestProperty("User-Agent", "Mozilla");
        conn.addRequestProperty("Referer", "google.com");

        System.out.println("Request URI ... " + uri);

        boolean redirect = false;

        final int status = conn.getResponseCode();

        if (status == HttpURLConnection.HTTP_MOVED_TEMP
                || status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_SEE_OTHER) {
            redirect = true;
        }

        System.out.println("Response Code: " + status);

        if (redirect) {
            // get redirect url from "location" header field
            final String newUrl = conn.getHeaderField("Location");

            // get the cookie if we need, for login
            final String cookies = conn.getHeaderField("Set-Cookie");

            // open the new connection again
            conn = (HttpURLConnection) URI.create(newUrl).toURL().openConnection(proxy);
            conn.setRequestProperty("Cookie", cookies);
            conn.addRequestProperty("Accept-Language", "de-DE,de;q=0.8");
            conn.addRequestProperty("User-Agent", "Mozilla");
            conn.addRequestProperty("Referer", "google.com");

            System.out.println("Redirect to URL : " + newUrl);
        }

        final StringBuilder html = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            br.lines().forEach(line -> html.append(line).append(System.lineSeparator()));
        }

        System.out.println("URL Content... \n" + html);
        System.out.println("Done");
    }

    static void introspector() throws IntrospectionException {
        for (PropertyDescriptor propertyDescriptor : Introspector.getBeanInfo(DJ.class).getPropertyDescriptors()) {
            System.out.printf("%s: %s, %s%n", propertyDescriptor.getName(), propertyDescriptor.getReadMethod(), propertyDescriptor.getWriteMethod());
        }
    }

    static void javaVersion() {
        //        Runtime.version()
        // String javaVersion = SystemUtils.JAVA_VERSION;
        final String javaVersion = System.getProperty("java.version");
        final String javaVersionDate = System.getProperty("java.version.date");
        final String vmVersion = System.getProperty("java.vm.version");
        final String[] splits = javaVersion.toLowerCase().split("[._]");

        // Major
        String versionString = String.format("%03d", Integer.parseInt(splits[0]));

        // Minor
        versionString += "." + String.format("%03d", Integer.parseInt(splits[1]));

        if (splits.length > 2) {
            // Micro
            versionString += "." + String.format("%03d", Integer.parseInt(splits[2]));
        }

        if (splits.length > 3 && !splits[3].startsWith("ea")) {
            // Update
            try {
                versionString += "." + String.format("%03d", Integer.parseInt(splits[3]));
            }
            catch (Exception ex) {
                System.err.println(ex.getMessage());
            }
        }

        final int version = Integer.parseInt(versionString.replace(".", ""));

        System.out.printf("javaVersionDate = %s%n", javaVersionDate);
        System.out.printf("vmVersion = %s%n", vmVersion);
        System.out.printf("JavaVersion = %s = %s = %d%n", javaVersion, versionString, version);
    }

    static void jndi() throws Exception {
        // Tomcat-JNDI Service
        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");

        final InitialContext initialContext = new InitialContext();
        initialContext.createSubcontext("java:");
        initialContext.createSubcontext("java:comp");
        initialContext.createSubcontext("java:comp/env");
        initialContext.createSubcontext("java:comp/env/jdbc");

        final Context context = (Context) new InitialContext().lookup("java:comp/env");
        context.bind("test", "dummy");
        LOGGER.info(new InitialContext().lookup("java:comp/env/test").toString());

        new InitialContext().bind("java:comp/env/jdbc/datasource", "myDataSource");
        LOGGER.info(new InitialContext().lookup("java:comp/env/jdbc/datasource").toString());
    }

    static void listDirectories() throws Exception {
        final Path base = Paths.get(System.getProperty("user.dir"));

        // Liefert alles im Verzeichnis, nicht rekursiv.
        System.out.println();
        final DirectoryStream.Filter<Path> filter = path -> Files.isDirectory(path) && !path.getFileName().toString().startsWith(".");

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(base, filter)) {
            for (Path path : stream) {
                System.out.println(path);
            }
        }

        // Liefert alles rekursiv, wenn definiert, auch den Root Path.
        System.out.println();

        try (Stream<Path> stream = Files.walk(base, 1)) {
            stream.filter(Files::isDirectory).forEach(System.out::println);
        }

        // Liefert alles im Verzeichnis, nicht rekursiv.
        System.out.println();
        final Predicate<Path> isDirectory = Files::isDirectory;
        final Predicate<Path> isHidden = p -> p.getFileName().toString().startsWith(".");

        try (Stream<Path> children = Files.list(base).filter(isDirectory.and(isHidden.negate()))) {
            children.forEach(System.out::println);
        }

        // Rekursiv löschen
        // Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
    }

    static void nioPipeChannels() throws Exception {
        final Pipe pipe = Pipe.open();

        final Callable<Void> writeCallable = () -> {
            // Schreiben
            final Pipe.SinkChannel sinkChannel = pipe.sink();

            final ByteBuffer buf = ByteBuffer.allocateDirect(24);

            for (int i = 1; i <= 3; i++) {
                System.out.printf("%s-%s: write %d%n", Thread.currentThread().getName(), "MiscMain.nioPipe", i);
                buf.putInt(i);
            }

            buf.flip();

            while (buf.hasRemaining()) {
                sinkChannel.write(buf);
            }

            return null;
        };

        final Callable<Void> readCallable = () -> {
            // Lesen
            final Pipe.SourceChannel sourceChannel = pipe.source();

            final ByteBuffer buf = ByteBuffer.allocate(24);

            final int bytesRead = sourceChannel.read(buf);
            buf.flip();

            System.out.printf("%s-%s: bytesRead=%d%n", Thread.currentThread().getName(), "MiscMain.nioPipe", bytesRead);

            while (buf.hasRemaining()) {
                System.out.printf("%s-%s: read %d%n", Thread.currentThread().getName(), "MiscMain.nioPipe", buf.getInt());
            }

            return null;
        };

        writeCallable.call();
        readCallable.call();

        System.out.println();

        // Reihenfolge absichtlich vertauscht,
        final Future<Void> readFuture = ForkJoinPool.commonPool().submit(readCallable);
        ForkJoinPool.commonPool().submit(writeCallable);

        readFuture.get();
    }

    static void printCharsets() {
        System.out.printf("Charsets: Default=%s", Charset.defaultCharset());
        final Set<String> sets = Charset.availableCharsets().keySet();
        // Arrays.sort(ids);

        for (String set : sets) {
            System.out.println(set);
        }
    }

    static void printTimeZones() {
        System.out.printf("TimeZones: Default=%s", TimeZone.getDefault());
        final String[] ids = TimeZone.getAvailableIDs();
        Arrays.sort(ids);

        for (String id : ids) {
            System.out.println(id);
        }
    }

    static void processBuilder() {
        try {
            // run the Unix "ps -ef" command
            // using the Runtime exec method:
            // final Process process = Runtime.getRuntime().exec("ps -ef");
            // final Process process = Runtime.getRuntime().exec("ping -c5 weg.de");
            // final Process process = new ProcessBuilder().command("df -hT").start();
            final ProcessBuilder processBuilder = new ProcessBuilder().command("/bin/sh", "-c", "df | grep vgdesktop-root | awk '{print $4}'");
            // .directory(directory);
            // .redirectErrorStream(true); // Gibt Fehler auf dem InputStream aus.

            for (int i = 0; i < 10; i++) {
                final Process process = processBuilder.start();

                try (BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                     BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                    // read the output from the command
                    System.out.println("Here is the standard output of the command:");
                    stdInput.lines().forEach(System.out::println);

                    // read any errors from the attempted command
                    System.out.println("Here is the standard error of the command (if any):");
                    stdError.lines().forEach(System.out::println);
                }

                System.out.println();
                // process.waitFor();
                process.destroy();
            }

            System.exit(0);
        }
        catch (final IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
            System.exit(-1);
        }
    }

    static void reactor() throws Exception {
        // Debug einschalten.
        // Hooks.onOperatorDebug();

        Mono.just("Test").map(s -> s + s).subscribe(System.out::println);
        Mono.just("").map(v -> null).onErrorReturn("null value").subscribe(System.out::println);

        System.out.println();

        final Scheduler scheduler = Schedulers.fromExecutor(EXECUTOR_SERVICE);
        // subscribeOn(Scheduler scheduler)

        // @formatter:off
        Flux.just("Test1", "Test2", "Test3", "Test4")
            .parallel() // In wie viele Zweige soll der Stream gesplittet werden: Default Schedulers.DEFAULT_POOL_SIZE
            .runOn(scheduler) // ThreadPool für die parallele Verarbeitung definieren.
            .map(s -> s + s)
            .subscribe(v -> System.out.println(Thread.currentThread().getName() + ": " + v))
            ;
        // @formatter:on

        System.out.println();

        // @formatter:off
        Flux.just("Test1", "Test2", "Test3")
            .parallel(2)
            .runOn(scheduler)
            .map(v -> v.endsWith("1") ? null : v)
            .map(s -> s + s)
            .subscribe(v -> System.out.println(Thread.currentThread().getName() + ": " + v), th -> System.out.println("Exception: " + th))
            ;
        // @formatter:on

        System.out.println();

        // Hooks.onOperatorDebug();
        // @formatter:off
        Flux.just("Test1", "Test2", "Test3", null)
            .parallel()
            .runOn(scheduler)
            .filter(StringUtils::isNotBlank)
            .map(s -> s + s)
            .doOnError(th -> System.out.println("Exception: " + th))
            .subscribe(v -> System.out.println(Thread.currentThread().getName() + ": " + v))
            ;
        // @formatter:on

        System.out.println();

        // Test mit StepVerifier (io.projectreactor:reactor-test)
        Flux<String> source = Flux.just("foo", "bar");
        source = source.concatWith(Mono.error(new IllegalArgumentException("boom")));

        // @formatter:off
        StepVerifier.create(source)
            .expectNext("foo")
            .expectNext("bar")
            .expectErrorMessage("boom")
            .verify()
            ;
        // @formatter:on

        // Irgendein Thread hängt hier noch ...
        System.exit(0);
    }

    static void reactorParallel() throws Exception {
        System.setProperty("reactor.schedulers.defaultBoundedElasticSize", Integer.toString(2 * Runtime.getRuntime().availableProcessors()));

        // Statischer Thread-Pool -> analog Executors.newFixedThreadPool(X)
        // Optimized for fast Runnable non-blocking executions.
        Flux.just(1, 2, 3).parallel(2).runOn(Schedulers.parallel()).subscribe(i -> LOGGER.info(Integer.toString(i)));

        // Threads sterben nach 60s inaktivität -> analog Executors.newCachedThreadPool()
        // Optimized for longer executions, an alternative for blocking tasks where the number of active tasks (and threads) is capped.
        Flux.just(1, 2, 3).parallel(2).runOn(Schedulers.boundedElastic()).subscribe(i -> LOGGER.info(Integer.toString(i)));

        Flux.just(1, 2, 3).publishOn(Schedulers.parallel()).subscribe(i -> LOGGER.info(Integer.toString(i)));
        Flux.just(1, 2, 3).subscribeOn(Schedulers.boundedElastic()).subscribe(i -> LOGGER.info(Integer.toString(i)));

        Schedulers.parallel().schedule(() -> System.out.println(Thread.currentThread().getName()));
        Schedulers.boundedElastic().schedule(() -> System.out.println(Thread.currentThread().getName()));

        Schedulers.shutdownNow();
    }

    static void reactorSinks() throws Exception {
        // Debug einschalten.
        Hooks.onOperatorDebug();

        LOGGER.info("Start");

        final Sinks.Many<String> latestChange = Sinks.many().replay().latest();

        // Analog AccumulativeRunnable
        latestChange.asFlux().buffer(Duration.ofMillis(250), Schedulers.boundedElastic()).subscribe(list -> LOGGER.info(list.toString()));

        // Nur das letzte Element innerhalb des Zeitraums.
        latestChange.asFlux().sample(Duration.ofMillis(250)).subscribe(LOGGER::info);

        for (int i = 0; i < 100; i++) {
            // latestChange.emitNext(Integer.toString(i), EmitFailureHandler.FAIL_FAST);
            latestChange.tryEmitNext(Integer.toString(i));
            TimeUnit.MILLISECONDS.sleep(25);
        }

        TimeUnit.MILLISECONDS.sleep(25);

        LOGGER.info("Stop");
    }

    static void reactorStream() throws Exception {
        Flux.just(0).doFinally(state -> System.out.println("flux finally 1")).doFinally(state -> System.out.println("flux finally 2")).subscribe();
        Stream.of(0).onClose(() -> System.out.println("stream close 1")).onClose(() -> System.out.println("stream close 2")).close();

        Flux.fromStream(Stream.of(0).onClose(() -> System.out.println("stream close 3")).onClose(() -> System.out.println("stream close 4")))
                .doFinally(state -> System.out.println("flux finally 5")).doFinally(state -> System.out.println("flux finally 6")).subscribe();
    }

    /**
     * Runs only with JVM-Options: --add-opens java.base/java.lang=ALL-UNNAMED
     */
    static void reflection() throws Exception {
        final String string = "test";

        try {
            final Field field = String.class.getDeclaredField("value");
            field.setAccessible(true);
            System.out.printf("Old Relection-Api: %s%n", Arrays.toString((byte[]) field.get(string)));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            final MethodHandles.Lookup methodLookup = MethodHandles.privateLookupIn(String.class, MethodHandles.lookup());
            // final MethodHandles.Lookup methodLookup = MethodHandles.lookup();
            final VarHandle handle = methodLookup.findVarHandle(String.class, "value", byte[].class);
            System.out.printf("New MethodHandles: %s%n", Arrays.toString((byte[]) handle.get(string)));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void regEx() {
        System.out.printf("102.112.207.net: %s%n", "102.112.207.net".matches(".*2[0oO]7\\.net"));
        System.out.printf("102.112.2o7.net: %s%n", "102.112.2o7.net".matches(".*2(0|o|O)7\\.net"));
        System.out.printf("102.122.2O7.net: %s%n", "102.122.2O7.net".matches(".*2(0|o|O)7\\.net"));
    }

    static void replace() {
        String text = "ab\"cd'ef \\ ";

        text = text.replaceAll("\"", "\\\""); // " -> \"
        System.out.println(text);

        text = text.replace("\"", "\\\""); // " -> \"
        System.out.println(text);

        text = text.replace("'", "\\'"); // ' -> \'
        System.out.println(text);

        text = text.replace(" \\ ", ""); // ' \ ' -> ''
        System.out.println(text);
    }

    static void rrd() throws Exception {
        final Path path = Paths.get(System.getProperty("user.dir"), "target", "mapped.dat");

        // try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "rw")) {
        // // Erstellt leere Datei fester Größe.
        // raf.setLength(8 * 1024);
        // }

        // final FileChannel fileChannel = raf.getChannel())

        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            final long fileSize = 8 * 1024; // 8 kB

            // Bereich der Datei im Buffer mappen.
            final MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, fileSize);

            buffer.putInt(1305); // Erster Eintrag
            buffer.putInt(8, 1305); // Dritter Eintrag, absolute Position

            buffer.position(0); // An den Anfang setzen

            // while (buffer.hasRemaining())
            // {
            // // Würde den kompletten Buffer (8 kB) auslesen.
            // System.out.println(buffer.getInt());
            // }
            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());

            buffer.force();
            fileChannel.force(true);
        }

        System.out.println();

        // Einzel int-Read mit DataInputStream.
        try (DataInputStream dis = new DataInputStream(Files.newInputStream(path))) {
            System.out.println(dis.readInt());
            dis.skip(4);
            System.out.println(dis.readInt());
        }

        System.out.println();

        // Multi int-Read mit MappedByteBuffer.
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            // Bereich der Datei im Buffer mappen, nur jeweils 12 Bytes = 3 Integers.
            final MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, 12);

            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());
        }

        System.out.println();

        // Einzel int-Read mit ByteBuffer (allocate).
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            // Nur jeweils 4 Bytes = 1 Integer.
            final ByteBuffer buffer = ByteBuffer.allocate(4);

            fileChannel.read(buffer);
            buffer.flip();
            System.out.println(buffer.getInt());

            buffer.clear();
            fileChannel.read(buffer);
            buffer.flip();
            System.out.println(buffer.getInt());

            buffer.clear();
            fileChannel.read(buffer);
            buffer.flip();
            System.out.println(buffer.getInt());
        }

        System.out.println();

        // Multi int-Read mit ByteBuffer (allocate).
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ)) {
            // Nur jeweils 12 Bytes = 3 Integers.
            final ByteBuffer buffer = ByteBuffer.allocate(12);

            fileChannel.read(buffer);
            buffer.flip();

            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());
            System.out.println(buffer.getInt());
        }
    }

    static void securityProviders() {
        for (Provider provider : Security.getProviders()) {
            System.out.printf(" --- Provider %s, version %s --- %n", provider.getName(), provider.getVersionStr());

            final Set<Service> services = provider.getServices();

            for (Service service : services) {
                if (service.getType().equalsIgnoreCase(MessageDigest.class.getSimpleName())) {
                    System.out.printf("Algorithm name: \"%s\"%n", service.getAlgorithm());
                }
            }

            System.out.println();
        }
    }

    static void showMemory() {
        final Runtime runtime = Runtime.getRuntime();
        final long maxMemory = runtime.maxMemory();
        final long allocatedMemory = runtime.totalMemory();
        final long freeMemory = runtime.freeMemory();

        final long divider = 1024 * 1024;
        final String unit = "MB";

        final NumberFormat format = NumberFormat.getInstance();

        System.out.printf("Free memory: %s%n", format.format(freeMemory / divider) + unit);
        System.out.printf("Allocated memory: %s%n", format.format(allocatedMemory / divider) + unit);
        System.out.printf("Max memory: %s%n", format.format(maxMemory / divider) + unit);
        System.out.printf("Total free memory: %s%n", format.format((freeMemory + (maxMemory - allocatedMemory)) / divider) + unit);
    }

    static void showWindowsNotification() throws Exception {
        // javafx
        //        Notifications notificationTest=Notifications.create();
        //        notificationTest.position(Pos.BASELINE_RIGHT);
        //        notificationTest.title(title);
        //        notificationTest.text(text);
        //        notificationTest.show();//for error noti notificationTest.showError();

        if (SystemTray.isSupported()) {
            final SystemTray systemTray = SystemTray.getSystemTray();

            Image image = null;
            //            image = Toolkit.getDefaultToolkit().createImage("images/duke.png");
            //            image = Toolkit.getDefaultToolkit().getImage("images/duke.png");

            //            URL url = URI.create("https://cr.openjdk.java.net/~jeff/Duke/png/Hips.png").toURL();
            //            image = ImageIO.read(url);

            try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("images/duke.png")) {
                image = ImageIO.read(inputStream);
            }

            final PopupMenu popupMenu = new PopupMenu();
            final TrayIcon trayIcon = new TrayIcon(image, "Tray Demo", popupMenu);

            // create a action listener to listen for default action executed on the tray icon
            final ActionListener listener = event -> {
                System.out.println(event);

                // Exit the JVM.
                systemTray.remove(trayIcon);
            };

            final MenuItem defaultItem = new MenuItem("Test-Menu");
            defaultItem.addActionListener(listener);
            popupMenu.add(defaultItem);

            trayIcon.addActionListener(listener);
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("TrayIcon-Tooltip");

            systemTray.add(trayIcon);

            trayIcon.displayMessage("Hello, World", "notification demo", TrayIcon.MessageType.INFO);
        }
        else {
            System.err.println("SystemTray is not supported !");
        }
    }

    static void splitList() {
        final List<Integer> intList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        final Map<Integer, List<Integer>> groups = intList.stream().collect(Collectors.groupingBy(s -> (s - 1) / 3));
        final List<List<Integer>> subSets = new ArrayList<>(groups.values());

        subSets.forEach(list -> {
            System.out.println("\nSub-List:");
            list.forEach(System.out::println);
        });
    }

    static void streamParallelCustomThreadPool() throws ExecutionException, InterruptedException {
        //        int availableCpus = Runtime.getRuntime().availableProcessors();

        final List<Long> list = LongStream.rangeClosed(1, 10).boxed().toList();

        // Alle Threads des ForkJoinPool.commonPool() werden verwendet.
        list.stream().parallel().forEach(value -> LOGGER.info("{}", value));

        System.out.println();

        // Hier sollen nur n Threads verwendet werden.
        //
        // Grund für das Verhalten ist folgende Methode: java.util.concurrent.ForkJoinTask.fork
        // "Arranges to asynchronously execute this task in the pool the current task is running in,
        // if applicable, or using the ForkJoinPool.commonPool() if not in ForkJoinPool."
        final ExecutorService customThreadPool = new ForkJoinPool(2);
        // Analog
        // ExecutorService customThreadPool = Executors.newWorkStealingPool(2);

        try {
            final Runnable runnable = () -> list.stream().parallel().forEach(value -> LOGGER.info("{}", value));
            customThreadPool.submit(runnable).get();
        }
        finally {
            // Memory-Leak verhindern.
            customThreadPool.shutdown();
        }
    }

    static void systemMXBean() throws Exception {
        System.out.println("\nOperatingSystemMXBean");

        final OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        final com.sun.management.OperatingSystemMXBean os = (com.sun.management.OperatingSystemMXBean) operatingSystemMXBean;

        // Das funktioniert nur, wenn es mehrmals aufgerufen wird.
        os.getCpuLoad();
        os.getCpuLoad();

        System.out.println("\tArch: " + os.getArch());
        System.out.println("\tName: " + os.getName());
        System.out.println("\tVersion: " + os.getVersion());
        System.out.println("\tCpuLoad: " + os.getCpuLoad());
        System.out.println("\tCpuLoad: " + os.getCpuLoad());
        System.out.println("\tAvailableProcessors: " + os.getAvailableProcessors());
        System.out.println("\tCommittedVirtualMemorySize: " + os.getCommittedVirtualMemorySize());
        System.out.println("\tFreePhysicalMemorySize(: " + os.getFreeMemorySize());
        System.out.println("\tFreeSwapSpaceSize: " + os.getFreeSwapSpaceSize());
        System.out.println("\tProcessCpuLoad: " + os.getProcessCpuLoad());
        System.out.println("\tProcessCpuTime: " + os.getProcessCpuTime());
        System.out.println("\tSystemLoadAverage: " + os.getSystemLoadAverage());
        System.out.println("\tTotalPhysicalMemorySize: " + os.getTotalMemorySize());
        System.out.println("\tTotalSwapSpaceSize: " + os.getTotalSwapSpaceSize());

        long lastSystemTime = 0;
        long lastProcessCpuTime = 0;

        long systemTime = System.nanoTime();
        long processCpuTime = os.getProcessCpuTime();
        double cpuUsage = ((double) (processCpuTime - lastProcessCpuTime)) / ((double) (systemTime - lastSystemTime));
        System.out.println("\tcpuUsage: " + cpuUsage);

        lastSystemTime = systemTime;
        lastProcessCpuTime = processCpuTime;

        TimeUnit.MILLISECONDS.sleep(3000);

        systemTime = System.nanoTime();
        processCpuTime = os.getProcessCpuTime();
        cpuUsage = ((double) (processCpuTime - lastProcessCpuTime)) / ((double) (systemTime - lastSystemTime));
        System.out.println("\tcpuUsage: " + cpuUsage);
    }

    static void textBlocks() throws Exception {
        // '\' Zeilenumbruch für zu lange Zeilen
        // '\n' Manueller Zeilenumbruch mit leerer Zeile
        // '\t' Tabulator
        // '%s' String.format Platzhalter

        final String sql = """
                select
                *
                from \
                "table"

                where

                \t1 = 1
                    order by %s asc;
                """.formatted("column");

        System.out.println(sql);
    }

    static void utilLogging() {
        // java.util.logging.Logger.GLOBAL_LOGGER_NAME
        final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MiscMain.class.getName());
        // logger.setLevel(Level.ALL);

        logger.severe("Schwerwiegender Fehler");
        logger.warning("Warnung");
        logger.info("Information");
        logger.config("Konfigurationshinweis");
        logger.fine("Fein");
        logger.finer("Feiner");
        logger.finest("Am feinsten");
    }

    static void virtualThreads() throws Exception {
        final Consumer<Thread> printThreadInfos = thread -> {
            final String message = "isVirtual = %b, ID = %s".formatted(thread.isVirtual(), thread);
            LOGGER.info(message);
        };

        // Executors.newVirtualThreadPerTaskExecutor(): Virtual Threads do not have Names.
        ThreadFactory threadFactory = Thread.ofVirtual().name("virtual-", 1).factory();

        try (ExecutorService executorService = Executors.newThreadPerTaskExecutor(threadFactory)) {
            IntStream.range(0, 20).forEach(i -> {
                executorService.submit(() -> {
                    printThreadInfos.accept(Thread.currentThread());
                    TimeUnit.MILLISECONDS.sleep(500L);
                    return i;
                });
            });
        }

        System.out.println();

        threadFactory = Thread.ofVirtual().name("virtual-", 1).factory();

        try (ExecutorService executorService = Executors.newFixedThreadPool(3, threadFactory)) {
            IntStream.range(0, 20).forEach(i -> {
                executorService.submit(() -> {
                    printThreadInfos.accept(Thread.currentThread());
                    TimeUnit.MILLISECONDS.sleep(500L);
                    return i;
                });
            });
        }

        Thread.ofVirtual().name("virtual").start(() -> printThreadInfos.accept(Thread.currentThread()));

        Thread.startVirtualThread(() -> printThreadInfos.accept(Thread.currentThread()));
    }

    /**
     * <a href="https://www.baeldung.com/java-compress-and-uncompress">java-compress-and-uncompress</a>
     */
    static void zip() throws Exception {
        // De-/Compress a Stream with GZIPOutputStream, GZIPInputStream.

        final Path source = Paths.get(System.getProperty("user.dir"), "pom.xml");
        final Path target = Paths.get(System.getProperty("java.io.tmpdir"), "pom.zip");

        // Create Zip Archive.
        try (InputStream inputStream = Files.newInputStream(source);
             OutputStream outputStream = Files.newOutputStream(target, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream, StandardCharsets.UTF_8)) {

            final ZipEntry zipEntry = new ZipEntry("folder/" + source.getFileName().toString());
            zipOutputStream.putNextEntry(zipEntry);

            inputStream.transferTo(zipOutputStream);

            zipOutputStream.closeEntry();

            zipOutputStream.finish();
            zipOutputStream.flush();
        }

        // Append new File.
        final URI uriTarget = URI.create("jar:" + target.toUri());

        try (FileSystem fs = FileSystems.newFileSystem(uriTarget, Map.of("create", true))) {
            final Path nf = fs.getPath("newFile.xml");
            Files.write(nf, Files.readAllBytes(source), StandardOpenOption.CREATE);
        }

        // Read Zip Archive.
        try (InputStream inputStream = Files.newInputStream(target);
             ZipInputStream zipInputStream = new ZipInputStream(inputStream, StandardCharsets.UTF_8)) {

            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                System.out.println("zipEntry = " + zipEntry);

                if ("folder/pom.xml".equals(zipEntry.getName())) {
                    try (OutputStream outputStream = Files.newOutputStream(target.getParent().resolve("my-pom-1.xml"), StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING)) {
                        zipInputStream.transferTo(outputStream);
                    }
                }

                zipInputStream.closeEntry();
            }
        }

        // Alternative
        try (ZipFile zipFile = new ZipFile(target.toFile(), StandardCharsets.UTF_8)) {

            //            ZipEntry zipEntry = zipFile.getEntry("folder/pom.xml");
            //            Stream<? extends ZipEntry> entries = zipFile.stream();

            final Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                final ZipEntry zipEntry = entries.nextElement();
                System.out.println("zipEntry = " + zipEntry);

                if ("folder/pom.xml".equals(zipEntry.getName())) {
                    try (OutputStream outputStream = Files.newOutputStream(target.getParent().resolve("my-pom-2.xml"), StandardOpenOption.CREATE,
                            StandardOpenOption.TRUNCATE_EXISTING);
                         InputStream inputStream = zipFile.getInputStream(zipEntry)) {
                        inputStream.transferTo(outputStream);
                    }
                }
            }
        }
    }

    private MiscMain() {
        super();
    }
}

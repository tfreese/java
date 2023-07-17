// Created: 28.12.2011
package de.freese.maven.proxy;

import java.io.File;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.maven.proxy.config.FileCache;
import de.freese.maven.proxy.config.ProxyConfig;
import de.freese.maven.proxy.jreserver.MavenProxyJreServer;
import de.freese.maven.proxy.repository.CompositeRepository;
import de.freese.maven.proxy.repository.Repository;
import de.freese.maven.proxy.repository.file.FileCacheRepository;
import de.freese.maven.proxy.repository.file.FileRepository;
import de.freese.maven.proxy.repository.http.JreHttpClientRepository;
import de.freese.maven.proxy.utils.MavenProxyThreadFactory;
import de.freese.maven.proxy.utils.ProxyUtils;

/**
 * Maven Configuration:
 *
 * <pre>
 * &lt;mirror&gt;
 *   &lt;id&gt;local-proxy&lt;/id>&gt;
 *   &lt;name&gt;local-proxy&lt;/name&gt;
 *   &lt;url&gt;http://localhost:7999&lt;/url&gt;
 *   &lt;mirrorOf&gt;*&lt;/mirrorOf&gt;
 * &lt;/mirror&gt;
 * </pre>
 *
 * <a href="https://github.com/netty/netty/tree/4.1/example">netty example</a><br>
 * <br>
 * curl -v localhost:8085/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.pom<br>
 * curl -v -X GET localhost:8085/ch/qos/logback/logback-classic/1.2.3/logback-classic-1.2.3.pom<br>
 * curl -v -X PUT localhost:8085 -d "..."<br>
 * <br>
 * mvn -f pom_proxy.xml -s settings_proxy.xml install<br>
 * mvn -f pom_proxy.xml -s settings_proxy.xml dependency:resolve<br>
 *
 * @author Thomas Freese
 */
public final class MavenProxyLauncher {
    private static final Logger LOGGER = LoggerFactory.getLogger(MavenProxyLauncher.class);

    public static void main(final String[] args) throws Exception {
        Path configPath = findConfigFile(args);

        if (!Files.exists(configPath)) {
            LOGGER.error("maven-proxy config file not exist: {}", configPath);
            return;
        }

        LOGGER.info("Process User: {}", System.getProperty("user.name"));

        URL url = ClassLoader.getSystemResource("proxy-config.xsd");
        Source schemaFile = new StreamSource(new File(url.toURI()));

        Source xmlFile = new StreamSource(configPath.toFile());

        // Validate Schema.
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");

        Schema schema = schemaFactory.newSchema(schemaFile);
        //        Validator validator = schema.newValidator();
        //        validator.validate(xmlFile);

        JAXBContext jaxbContext = JAXBContext.newInstance(ProxyConfig.class.getPackageName());
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(schema);
        ProxyConfig proxyConfig = (ProxyConfig) unmarshaller.unmarshal(xmlFile);

        validateConfig(proxyConfig);

        // ProxyUtils.setupProxy();

        //        Logger root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        //        root.setLevel(Level.INFO);

        int poolSize = Math.max(2, Runtime.getRuntime().availableProcessors() / 4);
        ExecutorService executorServiceHttpClient = new ThreadPoolExecutor(1, poolSize, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new MavenProxyThreadFactory("http-client-%d"));
        ExecutorService executorServiceHttpServer = new ThreadPoolExecutor(1, poolSize, 60L, TimeUnit.SECONDS, new SynchronousQueue<>(), new MavenProxyThreadFactory("http-server-%d"));

        // @formatter:off
        HttpClient.Builder builder = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .followRedirects(Redirect.NEVER)
                .proxy(ProxySelector.getDefault())
                .connectTimeout(Duration.ofSeconds(3))
                .executor(executorServiceHttpClient)
                ;
        // @formatter:on
        // .authenticator(Authenticator.getDefault())
        // .cookieHandler(CookieHandler.getDefault())
        // .sslContext(SSLContext.getDefault())
        // .sslParameters(new SSLParameters())

        HttpClient httpClient = builder.build();

        CompositeRepository compositeRepository = new CompositeRepository();

        proxyConfig.getRepositories().getUrls().stream().map(URI::create).forEach(uri -> {
            if ("http".equals(uri.getScheme()) || "https".equals(uri.getScheme())) {
                compositeRepository.addRepository(new JreHttpClientRepository(httpClient, uri));
            }
            else if ("file".equals(uri.getScheme())) {
                compositeRepository.addRepository(new FileRepository(Paths.get(uri)));
            }
            else {
                throw new IllegalArgumentException("only http, https, file are supported: " + uri.getScheme());
            }
        });

        Repository repository = compositeRepository;

        if (proxyConfig.getFileCache() != null || proxyConfig.getFileCache().isEnabled()) {
            URI fileCacheUri = URI.create(proxyConfig.getFileCache().getLocalUrl());
            repository = new FileCacheRepository(repository, Paths.get(fileCacheUri));
        }

        // MavenProxy proxy = new MavenProxyNetty();
        MavenProxy proxy = new MavenProxyJreServer();

        proxy.setPort(proxyConfig.getPort());
        proxy.setExecutor(executorServiceHttpServer);
        proxy.setRepository(repository);

        new Thread(proxy::start, "Maven-Proxy").start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            proxy.stop();

            ProxyUtils.shutdown(executorServiceHttpServer, LOGGER);
            ProxyUtils.shutdown(executorServiceHttpClient, LOGGER);
        }, "Shutdown"));
    }

    private static Path findConfigFile(final String[] args) {
        if (args != null && args.length == 2) {
            String parameter = args[0];

            if ("-maven-proxy.config".equals(parameter)) {
                return Paths.get(args[1]);
            }
        }
        else if (System.getProperty("maven-proxy.config") != null) {
            return Paths.get(System.getProperty("maven-proxy.config"));
        }
        else if (System.getenv("maven-proxy.config") != null) {
            return Paths.get(System.getenv("maven-proxy.config"));
        }

        LOGGER.error("no maven-proxy config file found");
        LOGGER.error("define it as programm argument: -maven-proxy.config <ABSOLUTE_PATH>/proxy-config.xml");
        LOGGER.error("or as system property: -Dmaven-proxy.config=<ABSOLUTE_PATH>/proxy-config.xml");
        LOGGER.error("or as environment variable: set/export maven-proxy.config=<ABSOLUTE_PATH>/proxy-config.xml");

        throw new IllegalStateException("no maven-proxy config file found");
    }

    private static void validateConfig(final ProxyConfig config) throws Exception {

        if (config.getPort() == null || config.getPort() < 1024) {
            LOGGER.error("port must be in range 1025-65534: {}", config.getPort());
            throw new IllegalStateException("port must be in range 1025-65534: " + config.getPort());
        }

        if (config.getRepositories().getUrls().isEmpty()) {
            LOGGER.error("no repositories configured");
            throw new IllegalStateException("no repositories configured");
        }

        if (config.getFileCache() != null && config.getFileCache().isEnabled()) {
            FileCache fileCache = config.getFileCache();

            String localUrl = fileCache.getLocalUrl();

            if (localUrl == null || localUrl.isBlank()) {
                LOGGER.error("local-url for file-cache not configured");
                throw new IllegalStateException("local-url for file-cache not configured");
            }

            if (!localUrl.startsWith("file:")) {
                LOGGER.error("local-url for file-cache must bei a file URL: {}", localUrl);
                throw new IllegalStateException("local-url for file-cache must bei a file URL: " + localUrl);
            }

            if (fileCache.isCreate()) {
                URI fileCacheUri = URI.create(localUrl);
                Path fileCachePath = Paths.get(fileCacheUri);

                if (!Files.exists(fileCachePath)) {
                    Files.createDirectories(fileCachePath);
                }
            }
        }
    }

    private MavenProxyLauncher() {
        super();
    }
}

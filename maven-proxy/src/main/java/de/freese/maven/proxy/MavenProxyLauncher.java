// Created: 28.12.2011
package de.freese.maven.proxy;

import java.net.ProxySelector;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.freese.maven.proxy.jreserver.MavenProxyJreServer;
import de.freese.maven.proxy.repository.CompositeRepository;
import de.freese.maven.proxy.repository.Repository;
import de.freese.maven.proxy.repository.file.FileRepository;
import de.freese.maven.proxy.repository.http.JreHttpClientRepository;
import de.freese.maven.proxy.util.MavenProxyThreadFactory;
import de.freese.maven.proxy.util.ProxyUtils;

/**
 * Startet den Maven Proxy.<br>
 * Maven Konfiguration:
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
 * <a href="https://github.com/netty/netty/tree/4.1/example">netty</a><br>
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
        LOGGER.info("Process User: {}", System.getProperty("user.name"));

        String fileCacheDirectory = System.getProperty("mavenproxy.fileCache");

        Path fileCachePath = null;

        if ((fileCacheDirectory != null) && !fileCacheDirectory.isBlank()) {
            fileCachePath = Paths.get(fileCacheDirectory);

            if (!Files.exists(fileCachePath)) {
                Files.createDirectories(fileCachePath);
            }

            LOGGER.info("Using FileCachePath: {}", fileCachePath);
        }

        Integer port = Integer.getInteger("mavenproxy.port");

        if (port == null || port <= 0) {
            LOGGER.error("A Port must be set by '-Dmavenproxy.port=...'");
            return;
        }

        // ProxyUtils.setupProxy();

        int poolSize = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);

        // ExecutorService executorService = Executors.newFixedThreadPool(poolSize, new MavenProxyThreadFactory("maven-proxy-"));
        // ExecutorService executorService = Executors.newCachedThreadPool(new MavenProxyThreadFactory("maven-proxy-"));
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
        compositeRepository.addRepository(new JreHttpClientRepository(httpClient, "https://repo1.maven.org/maven2"));
        compositeRepository.addRepository(new JreHttpClientRepository(httpClient, "https://repo.spring.io/release"));
        compositeRepository.addRepository(new JreHttpClientRepository(httpClient, "https://repository.primefaces.org"));
        compositeRepository.addRepository(new JreHttpClientRepository(httpClient, "https://plugins.gradle.org/m2"));
        compositeRepository.addRepository(new JreHttpClientRepository(httpClient, "https://repo.spring.io/milestone"));

        Repository repository = compositeRepository;

        if (fileCachePath != null) {
            repository = new FileRepository(fileCachePath, compositeRepository);
        }

        // MavenProxy proxy = new MavenProxyNetty();
        MavenProxy proxy = new MavenProxyJreServer();

        proxy.setPort(port);
        proxy.setExecutor(executorServiceHttpServer);
        proxy.setRepository(repository);

        new Thread(proxy::start, "Maven-Proxy").start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            proxy.stop();

            ProxyUtils.shutdown(executorServiceHttpServer, LOGGER);
            ProxyUtils.shutdown(executorServiceHttpClient, LOGGER);
        }, "Shutdown"));
    }

    private MavenProxyLauncher() {
        super();
    }
}

package de.freese.dependency.update.client;

import java.net.Authenticator;
import java.time.Duration;

import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.slf4j.LoggerFactory;

import de.freese.dependency.update.client.apache.ApacheHttpRepositoryClientBuilder;
import de.freese.dependency.update.client.apache.PreemptiveAuthenticationRequestInterceptor;
import de.freese.dependency.update.client.jakarta.JakartaRepositoryClientBuilder;
import de.freese.dependency.update.client.jnh.JreHttpRepositoryClientBuilder;
import de.freese.dependency.update.client.url.UrlConnectionRepositoryClientBuilder;

/**
 * @author Thomas Freese
 */
public final class RepositoryClientFactory {
    public static RepositoryClient createRepositoryClient(final int maxRetries, final Duration retryInterval) throws Exception {
        return createApacheHttpClient(maxRetries, retryInterval);
        // return createJakartaWsClient(maxRetries, retryInterval);
        // return createJreHttpClient(maxRetries, retryInterval);
        // return createUrlConnectionClient(maxRetries, retryInterval);
    }

    private static RepositoryClient createApacheHttpClient(final int maxRetries, final Duration retryInterval) throws Exception {
        final CredentialsProvider credentialsProvider = createCredentialsProvider();

        return new ApacheHttpRepositoryClientBuilder()
                .maxRetries(maxRetries)
                .retryInterval(retryInterval)
                .builderConfigurer(httpClientBuilder -> httpClientBuilder
                        // .setDefaultCredentialsProvider(credentialsProvider)
                        .addRequestInterceptorFirst(new PreemptiveAuthenticationRequestInterceptor(credentialsProvider))
                )
                .build();
    }

    private static Authenticator createAuthenticator() {
        // return new HostsAwareAuthenticator("host", new PasswordAuthentication("user", "password".toCharArray()));
        return null;
    }

    private static CredentialsProvider createCredentialsProvider() {
        // final CredentialsProvider credentialsProvider = CredentialsProviderBuilder.create()
        //         .add(
        //                 new HttpHost("https", "host", 443),
        //                 new UsernamePasswordCredentials("user", "token".toCharArray())
        //         )
        //         .add(
        //                 new HttpHost("https", "host", 443),
        //                 new BearerToken("token")
        //         )
        //         .build();
        //
        // return credentialsProvider;

        return null;
    }

    private static RepositoryClient createJakartaWsClient(final int maxRetries, final Duration retryInterval) throws Exception {
        final CredentialsProvider credentialsProvider = createCredentialsProvider();

        return new JakartaRepositoryClientBuilder()
                .maxRetries(maxRetries)
                .retryInterval(retryInterval)
                .builderConfigurer(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider)
                )
                .build();
    }

    private static RepositoryClient createJreHttpClient(final int maxRetries, final Duration retryInterval) throws Exception {
        if (LoggerFactory.getLogger("jdk.httpclient.HttpClient").isDebugEnabled()) {
            // Properties: https://docs.oracle.com/en/java/javase/25/docs/api/java.net.http/module-summary.html
            System.setProperty("jdk.httpclient.HttpClient.log", "requests");
        }

        final Authenticator authenticator = createAuthenticator();

        return new JreHttpRepositoryClientBuilder()
                .maxRetries(maxRetries)
                .retryInterval(retryInterval)
                .authenticator(authenticator)
                .build();
    }

    private static RepositoryClient createUrlConnectionClient(final int maxRetries, final Duration retryInterval) throws Exception {
        final Authenticator authenticator = createAuthenticator();

        return new UrlConnectionRepositoryClientBuilder()
                .maxRetries(maxRetries)
                .retryInterval(retryInterval)
                .authenticator(authenticator)
                .build();
    }

    private RepositoryClientFactory() {
        super();
    }
}

package de.freese.dependency.update.client;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.time.Duration;

import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.impl.auth.CredentialsProviderBuilder;
import org.slf4j.LoggerFactory;

import de.freese.dependency.update.client.apache.ApacheHttpRepositoryClientBuilder;
import de.freese.dependency.update.client.apache.PreemptiveAuthenticationRequestInterceptor;
import de.freese.dependency.update.client.jakarta.JakartaRepositoryClientBuilder;
import de.freese.dependency.update.client.jnh.JreHttpRepositoryClientBuilder;
import de.freese.dependency.update.client.url.UrlConnectionRepositoryClientBuilder;

/**
 * @author Thomas Freese
 * @since 21.07.26
 */
@SuppressWarnings({"java:S112", "java:S125"})
public enum RepositoryClientType {
    APACHE {
        @Override
        public RepositoryClient create(final int maxRetries, final Duration retryInterval) throws Exception {
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
    },
    JAKARTA {
        @Override
        public RepositoryClient create(final int maxRetries, final Duration retryInterval) throws Exception {
            final CredentialsProvider credentialsProvider = createCredentialsProvider();

            return new JakartaRepositoryClientBuilder()
                    .maxRetries(maxRetries)
                    .retryInterval(retryInterval)
                    .builderConfigurer(httpClientBuilder -> httpClientBuilder
                            .setDefaultCredentialsProvider(credentialsProvider)
                    )
                    .build();
        }
    },
    JRE_HTTPCLIENT {
        @Override
        public RepositoryClient create(final int maxRetries, final Duration retryInterval) throws Exception {
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
    },
    URL_CONNECTION {
        @Override
        public RepositoryClient create(final int maxRetries, final Duration retryInterval) throws Exception {
            final Authenticator authenticator = createAuthenticator();

            return new UrlConnectionRepositoryClientBuilder()
                    .maxRetries(maxRetries)
                    .retryInterval(retryInterval)
                    .authenticator(authenticator)
                    .build();
        }
    };

    private static Authenticator createAuthenticator() {
        // return new HostsAwareAuthenticator("host", new PasswordAuthentication("user", "password".toCharArray()));
        return new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(null, "".toCharArray());
            }
        };
    }

    private static CredentialsProvider createCredentialsProvider() {
        // final CredentialsProvider credentialsProvider = CredentialsProviderBuilder.create()
        //         //         .add(;
        //         //                 new HttpHost("https", "HOST", 443),
        //         //                 new UsernamePasswordCredentials("user", "token".toCharArray())
        //         //         )
        //         //         .add(
        //         //                 new HttpHost("https", "HOST", 443),
        //         //                 new BearerToken("token")
        //         //         )
        //         .build();

        return CredentialsProviderBuilder.create().build();
    }

    public abstract RepositoryClient create(int maxRetries, Duration retryInterval) throws Exception;
}

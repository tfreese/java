// Created: 16 Apr. 2025
package de.freese.dependency.update.client.apache;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Set;

import javax.net.ssl.SSLException;

import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
final class MyHttpRequestRetryStrategy extends DefaultHttpRequestRetryStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(MyHttpRequestRetryStrategy.class);

    private static final Set<Class<? extends IOException>> NON_RETRIABLE_EXCEPTIONS = Set.of(
            // InterruptedIOException.class, (SocketTimeoutException)
            UnknownHostException.class,
            ConnectException.class,
            ConnectionClosedException.class,
            NoRouteToHostException.class,
            SSLException.class);

    private static final Set<Integer> RETRIABLE_CODES = Set.of(
            HttpStatus.SC_TOO_MANY_REQUESTS,
            HttpStatus.SC_SERVICE_UNAVAILABLE,
            HttpStatus.SC_INTERNAL_SERVER_ERROR
    );

    MyHttpRequestRetryStrategy(final int maxRetries, final TimeValue defaultRetryInterval) {
        super(maxRetries, defaultRetryInterval, NON_RETRIABLE_EXCEPTIONS, RETRIABLE_CODES);
    }

    @Override
    public TimeValue getRetryInterval(final HttpRequest request, final IOException exception, final int execCount, final HttpContext context) {
        LOGGER.info("{}: {}", request, exception.getMessage());

        return super.getRetryInterval(request, exception, execCount, context);
    }

    @Override
    public TimeValue getRetryInterval(final HttpResponse response, final int execCount, final HttpContext context) {
        if (context instanceof final HttpClientContext hcc) {
            LOGGER.info("{}: {}", hcc.getRequest(), response);
        } else {
            LOGGER.info("{}", response);
        }

        return super.getRetryInterval(response, execCount, context);
    }

    @Override
    protected boolean handleAsIdempotent(final HttpRequest request) {
        // Retry if the request is considered idempotent.
        //
        // Before our retry customization, we need to elaborate a bit on the idempotency of requests.
        // It is important since the Apache HTTP client considers all HttpEntityEnclosingRequest implementations non-idempotent.
        // Common implementations of this interface are HttpPost, HttpPut, and HttpPatch classes.
        // So, our PATCH and PUT requests will not be, by default, retried!
        return super.handleAsIdempotent(request);
    }
}

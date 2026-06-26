package de.freese.dependency.update.client.apache;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

import javax.net.ssl.SSLException;

import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.client5.http.utils.DateUtils;
import org.apache.hc.core5.http.ConnectionClosedException;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHeaders;
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
public final class ExponentialBackoffRetryStrategy extends DefaultHttpRequestRetryStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExponentialBackoffRetryStrategy.class);
    private static final Set<Class<? extends IOException>> NON_RETRIABLE_EXCEPTIONS = Set.of(
            InterruptedIOException.class,
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
    private final long defaultRetryIntervalMillis;

    public ExponentialBackoffRetryStrategy(final int maxRetries, final TimeValue defaultRetryInterval) {
        super(maxRetries, defaultRetryInterval, NON_RETRIABLE_EXCEPTIONS, RETRIABLE_CODES);

        this.defaultRetryIntervalMillis = defaultRetryInterval.toMilliseconds();
    }

    @Override
    public TimeValue getRetryInterval(final HttpResponse response, final int execCount, final HttpContext context) {
        if (context instanceof final HttpClientContext hcc) {
            LOGGER.info("{}: {}", hcc.getRequest(), response);
        } else {
            LOGGER.info("{}", response);
        }

        // If exist use Retry-After Header.
        Objects.requireNonNull(response, "response required");

        final Header retryAfterHeader = response.getFirstHeader(HttpHeaders.RETRY_AFTER);

        if (retryAfterHeader != null) {
            final String value = retryAfterHeader.getValue();
            TimeValue retryAfter = null;

            try {
                retryAfter = TimeValue.ofSeconds(Long.parseLong(value));
            }
            catch (final NumberFormatException _) {
                final Instant retryAfterDate = DateUtils.parseStandardDate(value);

                if (retryAfterDate != null) {
                    retryAfter = TimeValue.ofMilliseconds(retryAfterDate.toEpochMilli() - System.currentTimeMillis());
                }
            }

            if (TimeValue.isPositive(retryAfter)) {
                return retryAfter;
            }
        }

        return backoff(execCount);
    }

    @Override
    public TimeValue getRetryInterval(final HttpRequest request, final IOException exception, final int execCount, final HttpContext context) {
        LOGGER.info("{}: {}", request, exception.getMessage());

        return super.getRetryInterval(request, exception, execCount, context);
    }

    // @Override
    // protected boolean handleAsIdempotent(final HttpRequest request) {
    //     // Retry if the request is considered idempotent.
    //     //
    //     // Before our retry customization, we need to elaborate a bit on the idempotency of requests.
    //     // It is important since the HTTP client considers all HttpEntityEnclosingRequest implementations non-idempotent.
    //     // Common implementations of this interface are HttpPost, HttpPut, and HttpPatch classes.
    //     // So, our PATCH and PUT requests will not be, by default, retried!
    //     return super.handleAsIdempotent(request);
    // }

    private TimeValue backoff(final int execCount) {
        // 500ms, 1s, 2s, 4s
        final long delay = defaultRetryIntervalMillis * (1L << Math.min(execCount, 6));

        return TimeValue.ofMilliseconds(delay);
    }
}

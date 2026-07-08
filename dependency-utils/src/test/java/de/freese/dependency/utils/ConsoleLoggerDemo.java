package de.freese.dependency.utils;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;

/**
 * @author Thomas Freese
 * @since 08.07.26
 */
public final class ConsoleLoggerDemo {
    static void main() {
        final Logger logger = ConsoleLogger.of(ConsoleLoggerDemo.class);

        logger.debug("debug message");
        logger.error("error message");
        logger.info("info message");
        logger.trace("trace message");
        logger.warn("warn message");

        final Duration duration = Duration.between(Instant.now().minusMillis(123_456_789L), Instant.now());
        logger.info("Duration: {}.{} s", duration.toSeconds(), duration.toMillisPart());
    }

    private ConsoleLoggerDemo() {
        super();
    }
}

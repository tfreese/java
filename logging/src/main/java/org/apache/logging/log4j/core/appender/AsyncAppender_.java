// Created: 14.06.2018
package org.apache.logging.log4j.core.appender;

import java.util.concurrent.Executor;

import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;

import de.freese.logging.log4j.MyAsyncAppender;

/**
 * Path for AsyncAppender.<br>
 * Simplification of {@link AsyncAppender} with an {@link Executor}.<br>
 * Only for testing (change name) !
 *
 * @author Thomas Freese
 */
//@Plugin(name = "Async", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
@SuppressWarnings({"checkstyle:TypeName", "java:S1144"})
public final class AsyncAppender_ extends MyAsyncAppender {
    @PluginBuilderFactory
    public static Builder newBuilder() {
        return new MyAsyncAppender.Builder();
    }

    private AsyncAppender_(final String name, final AppenderRef[] appenderRefs, final String errorRef, final Configuration config) {
        super(name, appenderRefs, errorRef, config, null);
    }
}

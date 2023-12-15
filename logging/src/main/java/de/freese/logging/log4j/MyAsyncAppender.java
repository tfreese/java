// Created: 14.06.2018
package de.freese.logging.log4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AsyncAppender;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;

/**
 * Simplification of {@link AsyncAppender} with an {@link Executor}.<br>
 * Own PlugIns must be defined in the log4j2.xml within 'Configuration packages="de.freese.logging.log4j"'.
 *
 * @author Thomas Freese
 */
@Plugin(name = "MyAsync", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class MyAsyncAppender extends AbstractAppender {
    /**
     * @author Thomas Freese
     */
    public static class Builder implements org.apache.logging.log4j.core.util.Builder<MyAsyncAppender> {
        @PluginElement("AppenderRef")
        @Required(message = "No appender references provided to MyAsyncAppender")
        private AppenderRef[] appenderRefs;
        @PluginConfiguration
        private Configuration configuration;
        @PluginBuilderAttribute
        @PluginAliases("error-ref")
        private String errorRef;
        @PluginBuilderAttribute
        private String executorJndiLocation;
        @PluginBuilderAttribute
        @Required(message = "No name provided for MyAsyncAppender")
        private String name;

        @Override
        public MyAsyncAppender build() {
            return new MyAsyncAppender(this.name, this.appenderRefs, this.errorRef, this.configuration, this.executorJndiLocation);
        }

        public Builder setAppenderRefs(final AppenderRef[] appenderRefs) {
            this.appenderRefs = appenderRefs;

            return this;
        }

        public Builder setConfiguration(final Configuration configuration) {
            this.configuration = configuration;

            return this;
        }

        public Builder setErrorRef(final String errorRef) {
            this.errorRef = errorRef;

            return this;
        }

        public Builder setExecutorJndiLocation(final String executorJndiLocation) {
            this.executorJndiLocation = executorJndiLocation;

            return this;
        }

        public Builder setName(final String name) {
            this.name = name;

            return this;
        }
    }

    @PluginBuilderFactory
    public static Builder newBuilder() {
        return new Builder();
    }

    private final AppenderRef[] appenderRefs;
    private final List<AppenderControl> appenders;
    private final Configuration configuration;
    private final String errorRef;
    private final String executorJndiLocation;

    private AppenderControl errorAppender;
    private Executor executor;

    protected MyAsyncAppender(final String name, final AppenderRef[] appenderRefs, final String errorRef, final Configuration configuration, final String executorJndiLocation) {
        super(name, null, null, false, Property.EMPTY_ARRAY);

        this.configuration = Objects.requireNonNull(configuration, "configuration required");
        this.appenderRefs = Objects.requireNonNull(appenderRefs, "appenderRefs required");
        this.errorRef = errorRef;
        this.appenders = new ArrayList<>();
        this.executorJndiLocation = executorJndiLocation;
    }

    @Override
    public void append(final LogEvent event) {
        if (!isStarted()) {
            throw new IllegalStateException("MyAsyncAppender " + getName() + " is not active");
        }

        final LogEvent memento = Log4jLogEvent.createMemento(event);
        //        InternalAsyncUtil.makeMessageImmutable(event.getMessage());

        final Runnable task = () -> appendEvent(memento);

        executor.execute(task);
    }

    @Override
    public void start() {
        final Map<String, Appender> map = this.configuration.getAppenders();
        this.appenders.clear();

        for (final AppenderRef appenderRef : this.appenderRefs) {
            final Appender appender = map.get(appenderRef.getRef());

            if (appender != null) {
                this.appenders.add(new AppenderControl(appender, appenderRef.getLevel(), appenderRef.getFilter()));
            }
            else {
                AbstractLifeCycle.LOGGER.error("No appender named {} was configured", appenderRef);
            }
        }

        if (this.appenders.isEmpty()) {
            throw new ConfigurationException("No appenders are available for AsyncAppender " + getName());
        }

        if (this.errorRef != null) {
            final Appender appender = map.get(this.errorRef);

            if (appender != null) {
                this.errorAppender = new AppenderControl(appender, null, null);
            }
            else {
                AbstractLifeCycle.LOGGER.error("Unable to set up error Appender. No appender named {} was configured", this.errorRef);
            }
        }

        if (executorJndiLocation != null && !executorJndiLocation.isBlank()) {
            try {
                final Context initialContext = new InitialContext();
                executor = (Executor) initialContext.lookup(this.executorJndiLocation);
            }
            catch (Exception ex) {
                AbstractLifeCycle.LOGGER.error(ex.getMessage(), ex);
                executor = Executors.newCachedThreadPool();
            }
        }
        else {
            executor = Executors.newCachedThreadPool();
        }

        super.start();
    }

    @Override
    public boolean stop(final long timeout, final TimeUnit timeUnit) {
        setStopping();

        super.stop(timeout, timeUnit, false);

        setStopped();

        return true;
    }

    private void appendEvent(final LogEvent logEvent) {
        boolean success = false;

        for (AppenderControl appender : this.appenders) {
            System.err.printf("%s - %s - %s - %s%n", getClass().getSimpleName(), Thread.currentThread().getName(), appender.getAppenderName(), logEvent.getMessage().getFormattedMessage());

            try {
                appender.callAppender(logEvent);
                success = true;
            }
            catch (final Exception ex) {
                // If no appender is successful the error appender will get it.
            }
        }

        if (!success && (this.errorAppender != null)) {
            try {
                this.errorAppender.callAppender(logEvent);
            }
            catch (final Exception ex) {
                // Silently accept the error.
            }
        }
    }
}

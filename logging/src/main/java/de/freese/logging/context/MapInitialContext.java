package de.freese.logging.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;

/**
 * <pre>{@code
 * MapInitialContext.init();
 *
 * Context initialContext = new InitialContext();
 * initialContext.bind("java:comp/env/...", ...);
 * }</pre>
 *
 * @author Thomas Freese
 */
public final class MapInitialContext extends InitialContext {
    public static void init() throws NamingException {
        final InitialContext initialContext = new MapInitialContext();
        final InitialContextFactory factory = environment -> initialContext;
        final InitialContextFactoryBuilder builder = environment -> factory;

        NamingManager.setInitialContextFactoryBuilder(builder);

        // NamingManager.setInitialContextFactoryBuilder(environment -> environment1 -> initialContext);
    }

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    private MapInitialContext() throws NamingException {
        super();
    }

    @Override
    public void bind(final String key, final Object value) {
        cache.put(key, value);
    }

    @Override
    public Object lookup(final String key) throws NamingException {
        return cache.get(key);
    }

    @Override
    public void rebind(final String name, final Object obj) throws NamingException {
        bind(name, obj);
    }

    @Override
    public void rename(final String oldName, final String newName) throws NamingException {
        final Object value = lookup(oldName);
        unbind(oldName);
        bind(newName, value);
    }

    @Override
    public void unbind(final String name) throws NamingException {
        cache.remove(name);
    }
}

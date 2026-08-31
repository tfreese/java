package de.addressbook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot entry point for the Addressbook application.
 * <p>
 * Layering follows {@code .specify/memory/architecture.md}: Presentation (JSF/PrimeFaces)
 * -&gt; API (REST controllers / backing beans) -&gt; Business (services) -&gt; Persistence (JDBC DAOs).
 */
@SpringBootApplication
public final class AddressbookApplication {
    private AddressbookApplication() {
        super();
    }

    static void main(final String[] args) {
        SpringApplication.run(AddressbookApplication.class, args);
    }
}

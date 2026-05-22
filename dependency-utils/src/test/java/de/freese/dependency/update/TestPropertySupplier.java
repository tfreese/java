// Created: 03.03.2019
package de.freese.dependency.update;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.freese.dependency.update.property.PropertySupplier;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@DisplayName("Test PropertySupplier")
class TestPropertySupplier {
    static Stream<Arguments> argumentsFactory() {
        return Stream.of(
                Arguments.of("ivy", PropertySupplier.ofIvySettings(Path.of("..", "..", "parents", "ivy-projects", "ivysettings.xml"))),
                Arguments.of("spring", PropertySupplier.ofSpringBootDependencies()),
                Arguments.of("maven", PropertySupplier.ofMavenPom(Path.of("..", "..", "parents", "maven-projects", "maven-parent", "pom.xml")))
        );
    }

    @ParameterizedTest(name = "{index} ({0}) => {1}")
    @MethodSource("argumentsFactory")
    @DisplayName("Properties")
    void testPropertySupplier(final String title, final PropertySupplier propertySupplier) {
        final Map<String, String> map = propertySupplier.get();

        assertNotNull(map);
        assertFalse(map.isEmpty());

        // coordinates.stream().sorted().forEach(System.out::println);
    }
}

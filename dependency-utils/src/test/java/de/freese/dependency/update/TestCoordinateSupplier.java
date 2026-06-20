// Created: 03.03.2019
package de.freese.dependency.update;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import de.freese.dependency.update.coordinate.Coordinate;
import de.freese.dependency.update.coordinate.CoordinateSupplier;

/**
 * @author Thomas Freese
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@DisplayName("Test CoordinateSupplier")
class TestCoordinateSupplier {
    private static final List<Arguments> ARGUMENTS = new ArrayList<>();

    static Stream<Arguments> argumentsFactory() {
        return ARGUMENTS.stream();
    }

    @BeforeAll
    static void beforeAll() {
        ARGUMENTS.add(Arguments.of("bom", CoordinateSupplier.ofMavenPom(Path.of("..", "..", "parents", "maven-projects", "maven-bom", "pom.xml"))));
        ARGUMENTS.add(Arguments.of("parent", CoordinateSupplier.ofMavenPom(Path.of("..", "..", "parents", "maven-projects", "maven-parent",
                "pom.xml"))));

        ARGUMENTS.add(Arguments.of("gradle.properties", CoordinateSupplier.ofGradleProperties(Path.of(System.getProperty("user.home"), ".gradle",
                "gradle.properties"))));
        ARGUMENTS.add(Arguments.of("gradle version catalog", CoordinateSupplier.ofGradleVersionCatalog(Path.of("..", "..", "parents",
                "gradle-projects", "libs.versions.toml"))));

        ARGUMENTS.add(Arguments.of("ivy-1", CoordinateSupplier.ofIvy(Path.of("..", "..", "parents",
                "ivy-projects", "multi-module", "project-api", "ivy.xml"))));
        ARGUMENTS.add(Arguments.of("ivy-2", CoordinateSupplier.ofIvy(Path.of("..", "..", "parents",
                "ivy-projects", "multi-module", "project-impl", "ivy.xml"))));
    }

    @ParameterizedTest(name = "{index} ({0}) => {1}")
    @MethodSource("argumentsFactory")
    @DisplayName("Coordinates")
    void testCoordinateSupplier(final String title, final CoordinateSupplier coordinateSupplier) {
        final List<Coordinate> coordinates = coordinateSupplier.get();

        assertNotNull(coordinates);
        assertFalse(coordinates.isEmpty());

        // coordinates.stream().sorted().forEach(System.out::println);
    }
}

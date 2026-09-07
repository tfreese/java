package de.freese.misc.junit5;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import java.awt.Point;
import java.io.Serial;
import java.util.Random;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The type Test misc.
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Test Junit5")
class TestDemo {
    static final MyObject[] MY_OBJECTS = {new MyObject(0, 0), new MyObject(0, 1), new MyObject(1, 0), new MyObject(1, 1)};

    static class MyObject extends Point {
        @Serial
        private static final long serialVersionUID = -2330553112363031008L;

        MyObject(final int x, final int y) {
            super(x, y);

            System.out.println(this);
        }

        @Override
        public String toString() {
            return "MyObject{" + "x=" + x + ", y=" + y + '}';
        }
    }

    static class MyParameterResolver implements ParameterResolver {
        static final Random RANDOM = new Random();

        @Override
        public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
            if (supportsParameter(parameterContext, extensionContext)) {
                return MY_OBJECTS[RANDOM.nextInt(MY_OBJECTS.length)];
            }

            return null;
        }

        @Override
        public boolean supportsParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
            return parameterContext.getParameter().getType() == MyObject.class;
        }
    }

    static Stream<MyObject> createObjects() {
        return Stream.of(MY_OBJECTS);
    }

    static Stream<Arguments> createObjectsArgumented() {
        return Stream.of(
                Arguments.of("Obj. 1", MY_OBJECTS[0]),
                Arguments.of("Obj. 2", MY_OBJECTS[1]),
                Arguments.of("Obj. 3", MY_OBJECTS[2]),
                Arguments.of("Obj. 4", MY_OBJECTS[3])
        );
    }

    @TestFactory
    Stream<DynamicTest> testDynamic() {
        return Stream.of(MY_OBJECTS)
                .map(obj -> dynamicTest("Test for: " + obj, () -> assertNotNull(obj))
                );
    }

    @TestFactory
    Stream<DynamicTest> testDynamic2() {
        return Stream.of(MY_OBJECTS)
                .flatMap(obj -> Stream.of(
                                dynamicTest("NotNull-Test for: " + obj, () -> assertNotNull(obj)),
                                dynamicTest("X-Test", () -> assertTrue(obj.getX() < 2)),
                                dynamicTest("Y-Test", () -> assertTrue(obj.getY() < 2))
                        )
                );
    }

    @TestFactory
    Stream<DynamicNode> testDynamic3() {
        return Stream.of(MY_OBJECTS)
                .map(obj -> dynamicContainer(obj.toString(),
                                Stream.of(dynamicTest("NotNull-Test", () -> assertNotNull(obj)),
                                        dynamicContainer("Coordinates",
                                                Stream.of(dynamicTest("X-Test", () -> assertTrue(obj.getX() < 2)),
                                                        dynamicTest("Y-Test", () -> assertTrue(obj.getY() < 2))
                                                )
                                        )
                                )
                        )
                );
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createObjects")
    @DisplayName("Test @MethodSource")
    @Tag("myTest")
    @EnabledOnOs({OS.WINDOWS, OS.LINUX})
    void testMethodSource(final MyObject obj) {
        // @EnabledOnJre(JRE.JAVA_11)
        // @DisabledOnJre(JRE.JAVA_8)
        // @EnabledIfSystemProperty(named = "os.arch", matches = ".*64.*")
        // @EnabledIfEnvironmentVariable(named = "ENV", matches = "staging-server")

        assertNotNull(obj);
        assertTrue(obj.getX() < 2);
        assertTrue(obj.getY() < 2);
    }

    @ParameterizedTest(name = "{index} -> {0}")
    @MethodSource("createObjectsArgumented")
    @DisplayName("Test @MethodSource Argumented")
    void testMethodSourceArgumented(final String name, final MyObject obj) {
        assertNotNull(obj);
        assertTrue(obj.getX() < 2);
        assertTrue(obj.getY() < 2);
    }

    @RepeatedTest(value = 4, name = "{displayName}: {currentRepetition}/{totalRepetitions}")
    @ExtendWith(TestDemo.MyParameterResolver.class)
    @DisplayName("Test @ExtendWith")
    void testParameterResolver(final MyObject obj) {
        assertNotNull(obj);
        assertTrue(obj.getX() < 2);
        assertTrue(obj.getY() < 2);
    }
}

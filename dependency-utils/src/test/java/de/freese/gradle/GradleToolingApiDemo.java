// Created: 10 Feb. 2025
package de.freese.gradle;

import java.io.File;

import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.idea.IdeaDependency;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;
import org.gradle.tooling.model.idea.IdeaSingleEntryLibraryDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class GradleToolingApiDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(GradleToolingApiDemo.class);

    static void main() {
        LOGGER.info("{}", System.getProperty("user.dir"));

        try (ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(new File("misc/misc-log4j3"))
                .useInstallation(new File("/usr/share/java/gradle"))
                // .useGradleVersion("8.12.1")
                // .useBuildDistribution()
                .connect()
        ) {
            // final GradleProject gradleProject = connection.getModel(GradleProject.class);
            // gradleProject.getTasks().forEach(task -> LOGGER.info(task.toString()));

            final IdeaProject ideaProject = connection.getModel(IdeaProject.class);

            for (final IdeaModule module : ideaProject.getModules()) {
                LOGGER.info("{}", module.getName());

                for (final IdeaDependency dependency : module.getDependencies()) {
                    // LOGGER.info("   {}", dependency);

                    final IdeaSingleEntryLibraryDependency ideaDependency = (IdeaSingleEntryLibraryDependency) dependency;

                    // true = transitive
                    // if (ideaDependency.getExported()) {
                    // LOGGER.info("   {}", ideaDependency);
                    LOGGER.info("   {} / {}", ideaDependency.getGradleModuleVersion(), ideaDependency.getFile());
                    // }
                }
            }

            // final EclipseProject eclipseProject = connection.getModel(EclipseProject.class);
            //
            // for (EclipseProject childProject : eclipseProject.getChildren()) {
            //     LOGGER.info(childProject.getName());
            //
            //     for (ExternalDependency externalDependency : childProject.getClasspath()) {
            //         LOGGER.info("   {} / {} / {}", externalDependency.isExported(), externalDependency.getGradleModuleVersion(), externalDependency.getFile());
            //     }
            // }
        }
    }

    private GradleToolingApiDemo() {
        super();
    }
}

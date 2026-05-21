// Created: 28.05.23
package de.freese.dependency.update.coordinate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Reporting;

import de.freese.dependency.utils.MavenModelCache;
import de.freese.dependency.utils.Utils;

/**
 * @author Thomas Freese
 */
final class CoordinateSupplierMavenPom implements CoordinateSupplier {
    private static List<Coordinate> parseDependencies(final Model model) {
        final String source = Utils.toSource(model.getPomFile().toPath());

        final List<Coordinate> coordinates = new ArrayList<>();

        // Dependencies without Version are defined in the BOM and will be ignored.
        final Consumer<Dependency> dependencyConsumer = dependency -> {
            if (dependency.getVersion() == null) {
                return;
            }

            final Coordinate coordinate;

            if ("${project.groupId}".equals(dependency.getGroupId()) && model.getGroupId() != null) {
                coordinate = new Coordinate(model.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), source);
            } else {
                coordinate = new Coordinate(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), source);
            }

            coordinates.add(coordinate);
        };

        // Plugins without Version are defined in the BOM and will be ignored.
        final Consumer<Plugin> pluginConsumer = plugin -> {
            if (plugin.getVersion() == null) {
                return;
            }

            final Coordinate coordinate;

            if ("${project.groupId}".equals(plugin.getGroupId()) && model.getGroupId() != null) {
                coordinate = new Coordinate(model.getGroupId(), plugin.getArtifactId(), plugin.getVersion(), source);
            } else {
                coordinate = new Coordinate(plugin.getGroupId(), plugin.getArtifactId(), plugin.getVersion(), source);
            }

            coordinates.add(coordinate);
        };

        // Parent
        Optional.ofNullable(model.getParent()).map(parent -> new Coordinate(parent.getGroupId(), parent.getArtifactId(), parent.getVersion(), source))
                .ifPresent(coordinates::add);

        // Dependencies
        model.getDependencies().forEach(dependencyConsumer);

        // Dependency-Management
        final DependencyManagement dependencyManagement = model.getDependencyManagement();

        if (dependencyManagement != null) {
            dependencyManagement.getDependencies().forEach(dependencyConsumer);
        }

        // Plugins
        final Build build = model.getBuild();

        if (build != null) {
            List<Plugin> plugins = build.getPlugins();

            plugins.forEach(pluginConsumer);
            plugins.stream().map(Plugin::getDependencies).flatMap(List::stream).forEach(dependencyConsumer);

            // Plugin-Management
            final PluginManagement pluginManagement = build.getPluginManagement();

            if (pluginManagement != null) {
                plugins = pluginManagement.getPlugins();

                plugins.forEach(pluginConsumer);
                plugins.stream().map(Plugin::getDependencies).flatMap(List::stream).forEach(dependencyConsumer);
            }

            // Extensions
            build.getExtensions().stream().map(ext -> {
                final Plugin plugin = new Plugin();
                plugin.setGroupId(ext.getGroupId());
                plugin.setArtifactId(ext.getArtifactId());
                plugin.setVersion(ext.getVersion());

                return plugin;
            }).forEach(pluginConsumer);
        }

        // Reporting
        final Reporting reporting = model.getReporting();

        if (reporting != null) {
            reporting.getPlugins().stream().map(rp -> {
                final Plugin plugin = new Plugin();
                plugin.setGroupId(rp.getGroupId());
                plugin.setArtifactId(rp.getArtifactId());
                plugin.setVersion(rp.getVersion());

                return plugin;
            }).forEach(pluginConsumer);
        }

        return coordinates;
    }

    private final Path path;

    CoordinateSupplierMavenPom(final Path path) {
        super();

        this.path = Objects.requireNonNull(path, "path required");
    }

    @Override
    public List<Coordinate> get() {
        final Model model = MavenModelCache.get(path);

        return parseDependencies(model);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CoordinateSupplierMavenPom.class.getSimpleName() + "[", "]")
                .add("path=" + path)
                .toString();
    }
}

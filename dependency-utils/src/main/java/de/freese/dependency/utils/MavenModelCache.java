// Created: 06.03.23
package de.freese.dependency.utils;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingRequest;

/**
 * @author Thomas Freese
 */
public final class MavenModelCache {
    private static final Map<Path, Model> CACHE = new ConcurrentHashMap<>();

    public static Model get(final Path path) {
        return CACHE.computeIfAbsent(path, p -> {
            final ModelBuilder modelBuilder = new DefaultModelBuilderFactory().newInstance();
            final Model model = modelBuilder.buildRawModel(p.toFile(), ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL, false).get();

            if (model == null) {
                throw new IllegalArgumentException("no model found for path: " + p);
            }

            return model;
        });

        // try {
        //     final ModelBuildingRequest req = new DefaultModelBuildingRequest();
        //     req.setProcessPlugins(false);
        //     req.setPomFile(path.toFile());
        //     req.setTwoPhaseBuilding(true);
        //     req.setValidationLevel(ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL);
        //     // req.setWorkspaceModelResolver(...);
        //     req.setModelResolver(new DefaultModelResolver);
        //
        //     final Model model2 = modelBuilder.build(req).getEffectiveModel();
        //     System.out.println(model2);
        // }
        // catch (ModelBuildingException ex) {
        //     throw new RuntimeException(ex);
        // }
    }

    private static void put(final Path path, final Model model) {
        CACHE.put(path, model);
    }

    private MavenModelCache() {
        super();
    }
}

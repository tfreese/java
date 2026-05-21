// Created: 06.03.23
package de.freese.dependency.utils;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingRequest;

/**
 * @author Thomas Freese
 */
public final class MavenModelCache {
    private static final Map<Path, Model> CACHE = new HashMap<>();

    public static synchronized Model get(final Path path) {
        Model model = CACHE.get(path);

        if (model == null) {
            final ModelBuilder modelBuilder = new DefaultModelBuilderFactory().newInstance();
            model = modelBuilder.buildRawModel(path.toFile(), ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL, false).get();

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

            if (model == null) {
                throw new IllegalArgumentException("no model found for path: " + path);
            }

            put(path, model);
        }

        return model;
    }

    private static void put(final Path path, final Model model) {
        CACHE.put(path, model);
    }

    private MavenModelCache() {
        super();
    }
}

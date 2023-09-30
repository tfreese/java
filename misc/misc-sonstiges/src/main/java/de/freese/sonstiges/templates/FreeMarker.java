package de.freese.sonstiges.templates;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class FreeMarker {
    private static final Logger LOGGER = LoggerFactory.getLogger(FreeMarker.class);

    public static void main(final String[] args) {
        String templateFile = "example.ftl";

        try {

            Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
            // cfg.setTemplateLoader(new FileTemplateLoader(new File(".")));
            // cfg.setTemplateLoader(new ClassTemplateLoader(FreeMarker.class, ""));
            cfg.setDirectoryForTemplateLoading(Paths.get("src", "main", "resources", "templates", "freemarker").toFile());
            cfg.setNumberFormat("0.#######");
            // cfg.setObjectWrapper(ObjectWrapper.BEANS_WRAPPER);
            // cfg.setObjectWrapper(BeansWrapper.getDefaultInstance());

            Template template = cfg.getTemplate(templateFile);

            Map<String, Object> model = new HashMap<>();
            // SimpleHash model = new SimpleHash();
            model.put("names", List.of("List element 1", "List element 2", "List element 3"));
            model.put("Math", Math.class);
            model.put("PI", Math.PI);

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
                template.process(model, writer);
                writer.flush();
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private FreeMarker() {
        super();
    }
}

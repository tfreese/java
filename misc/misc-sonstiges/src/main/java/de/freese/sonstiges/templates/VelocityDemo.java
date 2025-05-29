package de.freese.sonstiges.templates;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Freese
 */
public final class VelocityDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityDemo.class);

    public static void main(final String[] args) {

        try {
            final Properties properties = new Properties();
            properties.put("resource.loaders", "classpath");
            properties.put("resource.loader.classpath.description", "VelocityDemo Classpath Resource Loader");
            properties.put("resource.loader.classpath.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            properties.put("resource.loader.classpath.path", ".");
            properties.put("resource.loader.classpath.cache", "false");
            properties.put("resource.loader.classpath.modification_check_interval", "0");

            // final VelocityEngine ve = new VelocityEngine("velocity.properties");
            final VelocityEngine velocityEngine = new VelocityEngine(properties);
            velocityEngine.init();

            // Make a context object and populate with the data. This is where the VelocityDemo engine gets the data to resolve the references (ex. $list) in the template.

            final VelocityContext context = new VelocityContext();
            context.put("names", List.of("List element 1", "List element 2", "List element 3"));
            context.put("Math", Math.class);
            context.put("PI", Math.PI);

            // Direkter Aufruf, wenn Template als String bereits vorliegt.
            // VelocityDemo.evaluate(context, stringWriter, logTag, template);

            // get the Template object. This is the parsed version of your template input file. Note that getTemplate() can throw ResourceNotFoundException : if
            // it doesn't find the template ParseErrorException: if there is something wrong with the VTL Exception: if something else goes wrong (this is
            // generally indicative of as a serious problem.)
            final Template template = loadTemplate(velocityEngine);

            // Now have the template engine process your template using the data placed into the context.
            // Think of it as a 'merge' of the template and the data to produce the output stream.
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out))) {
                if (template != null) {
                    template.merge(context, writer);
                }

                writer.flush();
            }
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private static Template loadTemplate(final VelocityEngine velocityEngine) throws Exception {
        final String templateFile = Paths.get("templates", "velocity", "example.vtl").toString();

        return velocityEngine.getTemplate(templateFile);
    }

    private VelocityDemo() {
        super();
    }
}

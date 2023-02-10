// Created: 10.10.2012
package de.freese.sonstiges.javascript;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Demo für JavaScriptEngine.<br>
 * Benötigt org.openjdk.nashorn:nashorn-core Dependency.
 *
 * @author Thomas Freese
 */
public final class JavaScriptMain {
    public static void main(final String[] args) throws Exception {
        ScriptEngineManager manager = new ScriptEngineManager();

        manager.getEngineFactories().forEach(System.out::println);

        // Benötigt org.openjdk.nashorn:nashorn-core Dependency.
        ScriptEngine engine = manager.getEngineByExtension("js");

        if (engine == null) {
            engine = manager.getEngineByName("JavaScriptMain");
        }

        if (engine == null) {
            engine = manager.getEngineByName("nashorn");
        }

        if (engine == null) {
            engine = manager.getEngineByMimeType("text/javascript");
        }

        if (engine == null) {
            System.err.println("no engine found");
            return;
        }

        JavaScriptMain javaScript = new JavaScriptMain();
        javaScript.simpleScript(engine);
        javaScript.bindings(engine);
        javaScript.scriptFile(engine);
        javaScript.simpleFunction(engine);
        javaScript.withInterface(engine);
    }

    private JavaScriptMain() {
        super();
    }

    private void bindings(final ScriptEngine engine) throws ScriptException {
        StringBuilder script = new StringBuilder();
        script.append("a + b");

        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("a", 6);
        bindings.put("b", 7);

        Object result = engine.eval(script.toString(), bindings);
        Double summe = (Double) result;

        System.out.printf("%d + %d = %f%n", (int) bindings.get("a"), (int) bindings.get("a"), summe);
        System.out.println();
    }

    private void scriptFile(final ScriptEngine engine) throws ScriptException, IOException {
        Conf conf = new Conf();

        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("conf", conf);

        try (Reader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("configuration.js"), StandardCharsets.UTF_8)) {
            engine.eval(reader, bindings);
        }

        System.out.printf("Threads = %d%n", conf.getThreads());
        System.out.printf("BlockSize = %d%n", conf.getBlockSize());
        System.out.println();
    }

    private void simpleFunction(final ScriptEngine engine) throws ScriptException, NoSuchMethodException {
        StringBuilder script = new StringBuilder();
        script.append("function addiere(a,b)");
        script.append("{");
        script.append(" return a + b;");
        script.append("}");

        engine.eval(script.toString());

        Invocable invocable = (Invocable) engine;

        Object result = invocable.invokeFunction("addiere", 5, 6);
        Double summe = (Double) result;

        System.out.printf("Summe = %f%n", summe);

        System.out.println();
    }

    private void simpleScript(final ScriptEngine engine) throws ScriptException {
        StringBuilder script = new StringBuilder();
        script.append("var sum = 0;");
        script.append("for(var i = 0; i < 1000; i++)");
        script.append("{");
        script.append(" sum+=i;");
        script.append("}");

        Object result = engine.eval(script.toString());
        Double summe = (Double) result;

        System.out.printf("Summe = %f%n", summe);
        System.out.println();
    }

    private void withInterface(final ScriptEngine engine) throws ScriptException {
        engine.eval("function plus(a,b) { return a + b; }");
        engine.eval("function minus(a,b) { return a - b; }");

        Invocable invocable = (Invocable) engine;
        Calculator calculator = invocable.getInterface(Calculator.class);

        System.out.printf("Plus = %f%n", calculator.plus(5, 4));
        System.out.printf("Minus = %f%n", calculator.minus(5, 4));
        System.out.println();
    }
}

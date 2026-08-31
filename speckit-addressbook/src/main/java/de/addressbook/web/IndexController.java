package de.addressbook.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Leitet die Wurzel-URL ({@code /}) auf die Personenliste weiter, damit
 * {@code http://localhost:8080/} direkt die Hauptansicht zeigt (quickstart.md, Szenario 7).
 * Enthaelt keine Fachlogik (architecture.md).
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "redirect:/persons.xhtml";
    }
}

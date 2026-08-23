package de.freese.metamodel.metagen;

import java.util.List;

import javax.sql.DataSource;

import de.freese.metamodel.metagen.model.Schema;

/**
 * Interface für einen {@link MetaExporter}.<br>
 * Bildet die Struktur einer Datenbank ab.
 *
 * @author Thomas Freese
 * @since 08.07.2018
 */
@FunctionalInterface
public interface MetaExporter {
    /**
     * Liefert das Schema mit dem Meta-Modell.
     */
    List<Schema> export(DataSource dataSource, String schemaNamePattern, String tableNamePattern) throws Exception;
}

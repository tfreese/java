# Technology Stack

Konkrete Technologien, Versionen und Build-Konventionen fuer das Personen Addressable.
Fachliche und strukturelle Vorgaben (Schichten, Abhaengigkeitsregeln) siehe `architecture.md`.

## Sprache & Runtime

- Englisch als Projektsprache (Code, Kommentare, Dokumentation)
- Java 25
- Encoding durchgaengig `UTF-8` (Compiler, Gradle JVM, IntelliJ)
- Alle Konstruktoren sollen `super();` aufrufen, falls möglich
- Verwende ausschliesslich bei Datum- und Kalenderfunktionen die Java 8+ Time API (`java.time.*`), nicht `java.util.Date` oder `java.util.Calendar`
- Verwende statische Imports, falls möglich.

## Backend

- spring-boot `4.1.1` 
- Persistenz: JDBC, Zugriff ausschliesslich ueber DAOs
- Fachlogik in Services, keine Fachlogik in DAOs oder REST-Controllern

## Frontend

- JSF
- PrimeFaces
- spring-boot-web
- Dunkles Theme fuer UI (PrimeFaces Theme `arya-blue`)
- Backing Beans als API-Layer (Bindeglied zwischen View und Business Layer)

## Datenbank

- Embedded H2 mit SQL-Schema aus `inputs/schema.sql` (siehe `data-model.md`)
- Zugriff nur ueber JDBC-DAOs, kein direkter SQL-Zugriff aus Business- oder Presentation-Layer

## Build & Dependency Management

- Build-Tool: Gradle Kotlin 9.7.1 (Toolchain Java 25)
- Dependency-Policy ist hart: keine SNAPSHOTs, keine dynamischen Versionen (`failOnDynamicVersions`, `failOnChangingVersions`)
- Versionen zentral in `gradle.properties` (`version_*`), nicht in Subprojekten verteilen
- Gradle Configuration Cache ist standardmaessig aktiv

## Migration Constraints

Nicht zugelassene Technologien:
- Proprietaere Frameworks ohne Architekturfreigabe
- Neue Persistenz- oder ORM-Frameworks ausserhalb des bestehenden JPA/DAO-Ansatzes
- Alternative Web-Frameworks (z. B. React/Angular Vollintegration) ohne dokumentierte Architekturentscheidung

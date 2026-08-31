# Architecture

## Layer Model

| Layer | Verantwortung |
|---|---|---|
| Presentation | View-Rendering, Navigation, Client-seitige Validierung (XHTML) |
| API | Bindeglied View - Business, Datenbindung, DTO-Mapping |
| Business | Fachservices, fachliche Validierung |
| Persistence | Datenzugriff, Mapping auf Tabellen/Sequenzen (JPA/JDBC) |

## Dependency Rules

- Abhaengigkeiten laufen ausschliesslich top-down: Presentation -> API -> Business -> Persistence.
- Persistence und Integration duerfen keine Abhaengigkeit auf API oder Presentation haben.
- Business Layer darf Integration Layer aufrufen, niemals umgekehrt.

## Mandatory Rules

- Keine Fachlogik in Views (XHTML/Backing Beans duerfen nur delegieren, nicht entscheiden).
- Keine Persistenzzugriffe aus UI-Komponenten oder Business-Services.
- API Layer kommuniziert ausschliesslich mit Business Services, nie direkt mit der Persistenz oder
  externen Schnittstellen.

## Anti-Patterns (MUST NOT)

- Fachliche if/else-Entscheidungen in `.xhtml`-Dateien oder Backing-Bean-Gettern mit Seiteneffekten.
- Direkte JDBC/EntityManager-Nutzung ausserhalb von des Persistence-Layers.
- Aufrufe von Server Schnittstellen nur direkt aus der Presentation- oder API-Schicht.
- Zyklische Abhaengigkeiten zwischen Handlern verschiedener fachlicher Domaenen ohne definierte Schnittstelle.

## Cross-Cutting Concerns

- Transaktionsgrenzen liegen im Business Layer (Handler), nicht im DAO und nicht im Backing Bean.
- Fehlerbehandlung: fachliche Exceptions werden im Business Layer geworfen und in der API-Schicht
  in UI-taugliche Fehlermeldungen uebersetzt; technische Exceptions werden nicht ungefiltert an die
  View durchgereicht.
- Logging erfolgt schichtuebergreifend konsistent, jedoch ohne fachliche Entscheidungslogik im Logging-Code.

# Referenz: Klärungs-Taxonomie

> Angelehnt an die Kategorien, die spec-kit im Befehl
> `/speckit.clarify` verwendet, um Mehrdeutigkeiten in einer
> Spezifikation zu erkennen. Dient dem Skill
> `requirements-speckit-transformer` als feste Prüfliste in Schritt 3.

## Kategorien

### 1. Functional Scope & Behavior

- Sind alle Kernfunktionen mit einem eindeutigen MUST-Satz abgedeckt?
- Gibt es implizite Funktionen, die der Ausgangstext nur andeutet?
- Existieren widersprüchliche Aussagen zum Funktionsumfang?

### 2. Domain & Data Model

- Sind alle genannten Entitäten mit ihren wichtigsten Attributen benannt?
- Sind Beziehungen zwischen Entitäten eindeutig (1:1, 1:n, n:m)?
- Ist die Datenherkunft (Quellsystem) klar?

### 3. User Experience & Interaction

- Ist für jede User Story ein eindeutiger Auslöser ("When") benannt?
- Sind Rollen/Berechtigungen unterschiedlicher Nutzergruppen klar?

### 4. Non-Functional (Performance, Security, Compliance)

- Gibt es quantifizierte Antwortzeit-/Durchsatzanforderungen?
- Sind Datenschutz-, Aufbewahrungs- oder Protokollierungspflichten benannt?
- Sind Authentifizierungs-/Autorisierungsanforderungen konkret?

### 5. Integration & externe Abhängigkeiten

- Sind Schnittstellen zu Drittsystemen benannt (Name, Richtung, Daten)?
- Ist bekannt, ob Drittsysteme synchron oder asynchron angebunden
  werden sollen (fachlich, nicht technisch formuliert)?

### 6. Edge Cases & Fehlerverhalten

- Ist das Verhalten bei Ausfall/leeren Daten/Grenzwerten beschrieben?
- Sind Wiederholungs- oder Eskalationsregeln benannt?

### 7. Erfolgskriterien / Messbarkeit

- Existiert mindestens eine messbare, technologieneutrale Kennzahl?
- Ist der Zielwert (Schwelle, Prozentsatz, Zeitrahmen) konkret?

## Regeln für die Formulierung von Klärungsfragen

- Maximal 5 Fragen pro Zyklus, priorisiert nach Auswirkungsgrad.
- Jede Frage muss eine echte Interrogativform sein (Wer/Was/Wann/
  Wie viele/Welches ...), kein Themen-Label ohne Fragestellung.
- Wenn eine begrenzte Anzahl plausibler Antwortoptionen existiert,
  diese als Auswahl anbieten (A/B/C), um die Rückantwort eindeutig
  auswertbar zu machen.
- Keine Frage stellen, deren Antwort bereits eindeutig aus dem Ausgangstext hervorgeht.
- Keine rein technischen Fragen (Framework, Datenbank, Klassenname) -
  das gehört in die Planungsphase (`/speckit.plan`), nicht in `spec.md`.

---
name: requirements-speckit-transformer
description: >
  Transformiert unstrukturierte, schriftliche Anforderungen (Fließtext,
  Lastenhefte, Notizen) in eine für GitHub spec-kit optimierte
  Feature-Spezifikation (spec.md-Format:
  User Stories mit Priorität, Given/When/Then-Akzeptanzkriterien,
  Functional Requirements FR-XXX, Key Entities, Success Criteria SC-XXX).
  Führt eine strukturierte Lückenanalyse nach fester Taxonomie durch und
  stellt bis zu 5 gezielte Klärungsfragen zu fehlenden Anforderungen.
  Nutze diesen Skill immer, wenn Anforderungen analysiert, für spec-kit
  aufbereitet, auf Vollständigkeit geprüft oder in eine spec.md
  überführt werden sollen.
license: Proprietary
---

# Skill: Requirements > spec-kit Transformer

## Zweck

Dieser Skill kapselt die vollständige Methodik, um aus rohem,
unstrukturiertem Anforderungstext einen spec-kit-kompatiblen
Spezifikationsentwurf zu erzeugen - analog zur Kombination aus
`/speckit.specify` (Strukturierung) und `/speckit.clarify`
(Lückenanalyse), aber anwendbar auf beliebigen Eingabetext statt nur
auf eine Kurzbeschreibung.

## Referenzen (vor der Bearbeitung lesen)

- `.specify/templates/spec-template.md` - verbindliche Zielstruktur und offizielles spec-kit Template.
- `references/clarification-taxonomy.md` - Kategorien und Regeln für Klärungsfragen (angelehnt an `/speckit.clarify`).

Verwende ausschließlich diese lokalen Referenzen als Formatvorgabe.
Rufe keine externen URLs ab, sofern der Nutzer dies nicht ausdrücklich verlangt.

## Ablauf (verbindlich, in dieser Reihenfolge)

### Schritt 1 - Extraktion

Lies den rohen Anforderungstext vollständig. Extrahiere:

- Akteure/Rollen ("wer")
- Ziele/Motivation ("warum")
- Konkrete Handlungen/Funktionen ("was")
- Genannte Daten/Entitäten
- Genannte Randbedingungen (Mengen, Fristen, Systeme, Schnittstellen)
- Explizit oder implizit genannte Erfolgskriterien

Erzeuge daraus **keine** Annahmen über nicht genannte Informationen.
Fehlende Informationen werden in Schritt 3 als Klärungsfragen behandelt,
nicht in Schritt 1 erfunden.

### Schritt 2 - Mapping auf spec-kit-Struktur

Überführe die Extraktion 1:1 in die Struktur aus `.specify/templates/spec-template.md`:

1. **User Stories**, priorisiert nach P1/P2/P3
   - Vergib Prioritäten nach Geschäftswert und Unabhängigkeit, nicht
     nach Reihenfolge im Ausgangstext.
   - Jede Story muss unabhängig testbar sein ("Independent Test").
2. **Acceptance Scenarios** je Story im Format
   `Given [Zustand], When [Aktion], Then [Ergebnis]`.
3. **Edge Cases** - leite mindestens 2 Grenzfälle pro Story ab, sofern
   der Ausgangstext dafür Anhaltspunkte liefert; sonst als offene
   Klärungsfrage kennzeichnen (nicht raten).
4. **Functional Requirements (FR-XXX)** - atomare, prüfbare
   MUST-Aussagen, durchnummeriert.
5. **Key Entities** - nur aufnehmen, wenn das Feature Daten betrifft.
6. **Success Criteria (SC-XXX)** - messbare, technologieneutrale
   Kennzahlen (keine Implementierungsdetails wie Frameworks oder
   Klassennamen).

Jede Information, die nicht eindeutig aus dem Ausgangstext ableitbar
ist, wird **nicht** stillschweigend ergänzt, sondern wörtlich im
Entwurf als

```
[NEEDS CLARIFICATION: <konkrete offene Frage>]
```

markiert - exakt wie im spec-kit-Originalformat, damit der Entwurf
später unverändert in `/speckit.specify` bzw. direkt als `spec.md`
weiterverwendet werden kann.

### Schritt 3 - Strukturierte Lückenanalyse

Prüfe den Entwurf systematisch gegen **alle** Kategorien aus
`references/clarification-taxonomy.md`:

- Functional Scope & Behavior
- Domain & Data Model
- User Experience & Interaction
- Non-Functional (Performance, Security, Compliance)
- Integration & externe Abhängigkeiten
- Edge Cases & Fehlerverhalten
- Erfolgskriterien / Messbarkeit

Für jede Kategorie: Ist im Ausgangstext eine eindeutige, prüfbare
Aussage enthalten? Wenn nein > Kandidat für Klärungsfrage.

### Schritt 4 - Fragenauswahl (max. 5)

Wähle aus allen Kandidaten die **maximal 5** Fragen mit dem größten
Einfluss auf Architektur, Implementierbarkeit oder Testbarkeit aus
(Priorisierung: Sicherheit/Compliance > Datenmodell > funktionaler
Kern > UX-Detail > Nice-to-have).

Regeln für jede Frage (siehe `clarification-taxonomy.md` für Details):

- Echte Interrogativ-Frage, kein bloßes Themen-Label
  ("Welches Authentifizierungsverfahren?" statt "Authentifizierung").
- Wenn sinnvoll: Mehrfachauswahl mit konkreten Optionen anbieten,
  um die Rückantwort des Nutzers zu vereinfachen und eindeutig auswertbar zu machen.
- Genau eine offene Frage pro `[NEEDS CLARIFICATION]`-Marker.

### Schritt 5 - Antworten einarbeiten (Folgeaufrufe)

Wenn der Nutzer Antworten liefert:

1. Ersetze den passenden `[NEEDS CLARIFICATION: ...]`-Marker durch die konkrete, jetzt bekannte Anforderung.
2. Prüfe, ob durch die neue Information weitere FR-XXX, Edge Cases oder Key Entities entstehen - ergänze diese direkt.
3. Wiederhole Schritt 3 nur für die betroffenen Kategorien (kein vollständiger Neu-Durchlauf nötig).
4. Wenn keine offenen Marker mehr vorhanden sind: Status "Spezifikation vollständig" setzen.

### Schritt 6 - Ausgabe

Liefere immer:

- Vollständigen spec.md-Entwurf als Markdown-Codeblock.
- Liste der verbleibenden offenen Klärungsfragen (falls vorhanden).
- Status: `Entwurf unvollständig (n offene Klärungen)` oder `Spezifikation vollständig`.
- Speichere das Ergebnis in der Datei `specify-input-${TIMESTAMP}.md` im Arbeitsverzeichnis.

## Nicht-Ziele

- Dieser Skill erstellt **keinen** technischen Plan (`plan.md`) und
  **keine** Task-Liste (`tasks.md`) - das ist Aufgabe der
  nachgelagerten spec-kit-Befehle `/speckit.plan` und `/speckit.tasks`.
- Dieser Skill trifft **keine** Technologieentscheidungen (Framework,
  Datenbankschema, Klassenstruktur) - spec.md bleibt lösungsneutral.
- Dieser Skill rät **nicht** bei fehlenden Informationen, sondern markiert und fragt nach.

# Format-Spezifikation: Anforderungsdokument

Dieses Dokument definiert das exakte Zielschema, das der Skill `requirements-engineering` erzeugt und
beim Iterationsmodus wieder einliest. Änderungen an diesem Schema müssen abwärtskompatibel sein
(bestehende Dokumente müssen weiter parsebar bleiben).

## Frontmatter des Ausgabedokuments

Jedes erzeugte Dokument beginnt mit YAML-Frontmatter. Das Feld `skill` ist das Erkennungsmerkmal für
den Iterationsmodus (Abschnitt 1/5 in SKILL.md).

```yaml
---
skill: requirements-engineering
titel: <Projekt-/Themenname>
version: <fortlaufende Ganzzahl, beginnend bei 1>
datum: <ISO-Datum YYYY-MM-DD, Erstellungsdatum dieser Version>
basiert-auf: <Dateiname der Vorgängerversion oder "null" bei Erstversion>
autor: <"Copilot (requirements-engineering skill)" oder Nutzername, falls bekannt>
status-gesamt: <"In Klärung" | "Abgestimmt" | "Final">
---
```

## IDs

Muster: `REQ-<KAT>-<NNN>` mit dreistelliger, führender Null, je Kategorie separat fortlaufend:

| Kürzel | Kategorie |
|---|---|
| `F` | Funktionale Anforderung |
| `NF` | Nicht-funktionale Anforderung |
| `C` | Rahmenbedingung / Constraint |
| `BR` | Geschäftsregel (Business Rule) |

IDs werden **nie wiederverwendet**, auch nicht nach Verwerfen einer Anforderung. Bei Iteration wird die
höchste vergebene Nummer je Kategorie aus dem Eingabedokument fortgesetzt.

## Status-Werte (je Einzelanforderung)

| Status | Bedeutung |
|---|---|
| `Entwurf` | Erstformulierung, noch nicht mit Nutzer abgestimmt |
| `Offen` | Es existiert mindestens eine unbeantwortete Rückfrage |
| `Geklärt` | Alle Rückfragen beantwortet, Anforderung vollständig |
| `Verworfen` | Nicht mehr relevant, aber ID bleibt für Nachvollziehbarkeit erhalten |

## Priorität (MoSCoW)

`Must` | `Should` | `Could` | `Won't` | `TBD` (wenn vom Nutzer noch nicht festgelegt)

## Struktur je Einzelanforderung

Siehe `assets/requirement-template.md` für das ausfüllbare Muster. Pflichtfelder:

- `ID`, `Titel`, `Kategorie`, `Priorität`, `Status`
- `Beschreibung` (aktiv formuliert, ein Satz/Absatz, atomar)
- `Akzeptanzkriterien` (mind. 1 Eintrag bei Status `Geklärt`; Liste)
- `Quelle` (z. B. "Nutzerangabe 2026-09-01", "abgeleitet/Annahme")
- `Offene Punkte` (Liste offener Rückfragen zu genau dieser Anforderung; leer, wenn `Geklärt`)

Optionale Felder: `Abhängigkeiten` (Liste anderer IDs), `Akteure`, `Datenobjekte`, `Fehlerfälle`.

## Gesamtdokument-Gliederung

Siehe `assets/document-template.md`. Reihenfolge der Abschnitte:

1. Frontmatter
2. Kurzüberblick (2–5 Sätze Freitext, was das Dokument abdeckt)
3. Glossar (nur bei Bedarf – mehrdeutige Begriffe)
4. Funktionale Anforderungen (`REQ-F-*`)
5. Nicht-funktionale Anforderungen (`REQ-NF-*`)
6. Rahmenbedingungen (`REQ-C-*`)
7. Geschäftsregeln (`REQ-BR-*`)
8. Annahmen (getroffene Annahmen, die nicht explizit bestätigt wurden)
9. Offene Punkte / Rückfragen (dokumentweite Sammlung, aggregiert aus allen `Offen`-Anforderungen -
   dient als Einstiegspunkt für die nächste Iteration)
10. Änderungshistorie (Tabelle: Version, Datum, Änderung, Basis-Datei)

## Parsing-Hinweise für den Iterationsmodus

- Frontmatter per YAML parsen; falls `skill: requirements-engineering` fehlt, Dokument **nicht** als
  Iterationsbasis behandeln, sondern als unstrukturierten Freitext-Input (Fallback: as-is übernehmen
  und in Abschnitt 2/4 neu strukturieren).
- Anforderungen werden über die `### REQ-<KAT>-<NNN>: <Titel>`-Überschrift erkannt (siehe Template).
- Der Abschnitt "Offene Punkte / Rückfragen" auf Dokumentebene ist **abgeleitet** (kein eigenständiger
  Datenspeicher) – er wird bei jeder Iteration aus den Einzelanforderungen neu aggregiert.

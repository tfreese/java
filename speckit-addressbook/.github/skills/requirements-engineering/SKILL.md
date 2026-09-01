---
name: requirements-engineering
description: 'Requirements-Engineering-Experte: strukturiert unstrukturierte oder unvollständige Software-Anforderungen in ein einheitliches Markdown-Format, stellt gezielte Rückfragen bei Lücken/Widersprüchen und speichert das Ergebnis als durchnummeriertes .md-Dokument. Nutzen bei Anfragen zu Requirements Engineering, Anforderungsanalyse, Anforderungsdokument, Pflichtenheft, Lastenheft, User Stories, funktionalen/nicht-funktionalen Anforderungen, Spezifikation erstellen/verfeinern, oder wenn ein bereits mit diesem Skill erzeugtes Markdown als Grundlage für eine weitere Iteration dient.'
license: 'Interne Nutzung – siehe LICENSE.txt'
---

# Requirements Engineering Expert

## Metadaten

- **Autor:** Thomas Freese
- **Erstellt:** 2026-09-01

Du agierst in diesem Skill als **Senior Requirements Engineer** (IREB-CPRE-Niveau). Ziel ist es, aus
Freitext, Stichpunkten, E-Mails, Transkripten o. ä. ein belastbares, testbares Anforderungsdokument zu
erzeugen – iterativ, nachvollziehbar und versioniert.

## 1. Eingabe klassifizieren

Bevor du irgendetwas strukturierst, stelle fest, um welchen Fall es sich handelt:

| Fall | Erkennungsmerkmal | Vorgehen                                                                    |
|---|---|-----------------------------------------------------------------------------|
| **Neuerfassung** | Freitext/Stichpunkte ohne Bezug auf vorhandenes Dokument | Abschnitt 2 (Analyse) > 3 (Rückfragen) > 4 (Dokument erzeugen)              |
| **Iteration** | Nutzer verweist auf/liefert eine `.md`-Datei, die dem Schema in `references/format-spezifikation.md` entspricht (erkennbar am Frontmatter-Feld `skill: requirements-engineering`) | Abschnitt 5 (Iterationsmodus)                                               |
| **Gemischt** | Neuer Freitext *und* vorhandenes Dokument | Neue Inhalte wie Neuerfassung behandeln, anschließend in Abschnitt 5 mergen |

**Wichtig:** Lies bei Bedarf `references/format-spezifikation.md` (exaktes Zielschema, IDs, Status-Werte,
Priorisierung) und `assets/requirement-template.md` (Baustein je Einzelanforderung), bevor du das
Dokument schreibst. Lade `references/fragenkatalog.md` nur, wenn du konkrete Lückenfragen formulieren
musst – nicht pauschal.

## 2. Unstrukturierten Input analysieren

1. Extrahiere alle erkennbaren **Einzelanforderungen** (atomar – eine Anforderung = ein Satz/Kriterium).
   Ein Freitextabsatz mit "und außerdem"/Aufzählungen wird in mehrere Anforderungen zerlegt.
2. Klassifiziere jede Anforderung vorläufig:
   - **Funktional (F)** – was das System tun soll.
   - **Nicht-funktional (NF)** – Qualitätsmerkmale (Performance, Sicherheit, Verfügbarkeit,
     Wartbarkeit, Skalierbarkeit, Usability, Compliance).
   - **Rahmenbedingung/Constraint (C)** – technische, organisatorische oder rechtliche Vorgaben.
   - **Geschäftsregel (BR)** – fachliche Regel/Berechnung/Validierung.
3. Erkenne implizite Akteure, Datenobjekte und Schnittstellen – auch wenn sie nicht explizit genannt
   wurden; markiere Annahmen deutlich als **Annahme**, nicht als Fakt.
4. Identifiziere **Lücken** je Anforderung: fehlende Akzeptanzkriterien, fehlende Priorität, unklare
   Trigger/Auslöser, fehlende Fehlerbehandlung, unklare Mengen-/Lastangaben, widersprüchliche Aussagen
   zu anderen bereits erfassten Anforderungen.

## 3. Rückfragen-Strategie

Stelle **nur** Rückfragen, die die Anforderung tatsächlich unklar/unvollständig lassen.
Keine Pauschalfragen, kein Abarbeiten des kompletten Fragenkatalogs.

- Jede Information, die nicht eindeutig aus dem Ausgangstext ableitbar ist, wird **nicht** stillschweigend ergänzt,
  sondern nachgefragt.
- **Bündeln statt einzeln**: Alle offenen Fragen zu einer Anforderung (oder zu einem thematischen
  Block) in einer Rückmeldung sammeln, nummeriert, mit Bezug auf die Anforderungs-ID.
- **Geschlossene Fragen bevorzugen**, wo möglich (Ja/Nein, Auswahl, Zahl/Einheit) – das reduziert den
  Klärungsaufwand für den Nutzer spürbar.
- **Sinnvolle Annahme vorschlagen**, wenn plausibel ("Ich nehme an, X. Korrekt?") statt offener
  W-Fragen – der Nutzer kann bestätigen statt frei antworten zu müssen.
- **Priorisiere Blocker**: Fragen, die die Kernfunktion betreffen, zuerst; kosmetische/Detailfragen
  können auch als offener Punkt im Dokument vermerkt werden (siehe unten), statt den Dialog zu
  blockieren.
- **Nutzer darf Fragen offenlassen.** Wenn der Nutzer eine Rückfrage nicht beantwortet oder explizit
  "später"/"weiß ich noch nicht" sagt: Anforderung trotzdem speichern, Status `Offen` setzen und die
  Frage **wörtlich** im Abschnitt "Offene Punkte" der Anforderung festhalten (siehe Template). Blockiere
  niemals die Erzeugung des Dokuments wegen unbeantworteter Fragen – Ziel ist ein **iteratives**
  Dokument, keine Vollständigkeitspflicht in einem Durchgang.
- Orientierung an typischen Fragen nach Kategorie: `references/fragenkatalog.md`.

## 4. Dokument erzeugen

1. Fülle je Anforderung den Baustein aus `assets/requirement-template.md`.
2. Baue das Gesamtdokument nach `assets/document-template.md` (Kopfmetadaten, Inhaltsverzeichnis nach
   Kategorie, Abschnitt "Offene Punkte / Rückfragen", Abschnitt "Annahmen", Änderungshistorie).
3. Vergib IDs fortlaufend je Kategorie: `REQ-F-001`, `REQ-NF-001`, `REQ-C-001`, `REQ-BR-001` (nie
   wiederverwenden, auch nicht nach Löschung – Status `Verworfen` statt Löschen).
4. Ermittle den Zieldateinamen (Abschnitt 6) und schreibe die Datei.
5. Fasse am Ende der Antwort **kurz** zusammen: wie viele Anforderungen erfasst (F/NF/C/BR), wie viele
   offene Punkte, Dateiname. Kein erneutes Ausgeben des kompletten Dokumentinhalts im Chat, wenn es
   bereits als Datei gespeichert wurde – außer der Nutzer fragt danach.

## 5. Iterationsmodus (bestehendes Dokument als Eingabe)

Wenn die Eingabe eine bereits von diesem Skill erzeugte `.md`-Datei ist (oder der Nutzer sagt "nutze
`requirements-03.md` als Basis"):

1. Parse das Dokument vollständig, inkl. Frontmatter (`version`, `basiert-auf`, `datum`).
2. Gehe gezielt die Abschnitte **"Offene Punkte / Rückfragen"** und Anforderungen mit Status `Offen`
   durch – das ist der primäre Ansatzpunkt für den nächsten Dialog.
3. Verarbeite zusätzlich neuen Freitext-Input des Nutzers (falls vorhanden) wie in Abschnitt 2.
4. Aktualisiere:
   - Bestehende Anforderungen: Status ändern (`Offen` > `Geklärt`), Akzeptanzkriterien ergänzen,  **nie** die ID ändern.
   - Neue Anforderungen: nächste freie ID der jeweiligen Kategorie vergeben (nicht bei 001 neu beginnen).
   - Änderungshistorie: neuer Eintrag mit Datum, Kurzbeschreibung der Änderungen, Verweis auf Ursprungsdatei.
5. Frontmatter-Feld `basiert-auf: <ursprungsdateiname>` und `version: <n+1>` setzen.
6. Speichere als **neue** Datei (nächste freie Nummer, Abschnitt 6) – die Ursprungsdatei wird **nicht**
   überschrieben, damit die Historie nachvollziehbar bleibt.

## 6. Dateiname & Nummerierung

Zielverzeichnis: das vom Nutzer genannte Verzeichnis, sonst das aktuelle Arbeitsverzeichnis. Basisname:
vom Nutzer genannt, sonst Default `requirements`.

Algorithmus (Kollisionsvermeidung):

1. Prüfe, ob `<basisname>.md` existiert. Existiert sie **nicht** > das ist der Zieldateiname (erste
   Erfassung).
2. Existiert sie bereits > suche die höchste vorhandene Zahl `n` in Dateien des Musters
   `<basisname>-<n>.md` (zweistellig, z. B. `requirements-01.md`, `requirements-02.md`) im
   Zielverzeichnis und verwende `n+1`.
3. Führt der Iterationsmodus (Abschnitt 5) diesen Schritt aus, gilt derselbe Algorithmus – die als
   Eingabe genutzte Datei zählt als vorhandene Datei und wird nicht überschrieben.

**Bevorzugt:** führe `scripts/next_filename.py <verzeichnis> <basisname>` aus (funktioniert plattformunabhängig, keine Off-by-one-/Padding-Fehler).
Das Skript gibt den nächsten freien vollständigen Dateipfad auf stdout aus.

**Fallback ohne Skriptausführung:** Liste die Verzeichnisinhalte, wende den Algorithmus oben manuell an.

```bash
python3 scripts/next_filename.py ./docs requirements
# Beispielausgabe: ./docs/requirements-03.md
```

## 7. Qualitätskriterien (vor dem Speichern prüfen)

- Jede Anforderung ist **atomar**, aktiv formuliert, testbar (kein "sollte möglichst").
- Jede funktionale Anforderung hat mind. ein **Akzeptanzkriterium** (Gherkin-artig
  Given/When/Then erlaubt, aber nicht zwingend).
- Nicht-funktionale Anforderungen sind **quantifiziert**, wo möglich (z. B. "Antwortzeit < 200 ms bei
  95. Perzentil", nicht "schnell").
- Keine Widersprüche zwischen Anforderungen unentdeckt lassen – bei Konflikt: beide referenzieren,
  Konflikt im Abschnitt "Offene Punkte" benennen, nicht stillschweigend auflösen.
- Konsistente Terminologie – bei Mehrdeutigkeit (z. B. "Nutzer" vs. "Kunde" vs. "Anwender") im
  Abschnitt "Glossar" klären bzw. als Rückfrage stellen.
- Priorisierung nach MoSCoW (`Must`/`Should`/`Could`/`Won't`) je Anforderung, sonst `TBD`.

## 8. Ressourcen dieses Skills

Verwende ausschließlich diese lokalen Referenzen und keine weiteren SKILLS oder externe Dokumente.
- `assets/document-template.md` – Gesamtstruktur des Ausgabedokuments.
- `assets/requirement-template.md` – Baustein je Einzelanforderung.
- `references/format-spezifikation.md` – exaktes Schema (Felder, IDs, Status-/Prioritätswerte, Frontmatter).
- `references/fragenkatalog.md` – Fragenkatalog nach Kategorie, als Orientierung für Rückfragen.
- `scripts/next_filename.py` – ermittelt kollisionsfreien, nummerierten Zieldateinamen.

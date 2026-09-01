# Skill: requirements-engineering

Agent Skill für GitHub Copilot (Cloud-Agent, Code Review, Copilot CLI, Copilot App, Agent-Modus in
VS Code/JetBrains). Strukturiert unstrukturierte Anforderungen, stellt gezielte Rückfragen bei Lücken
und speichert das Ergebnis als versioniertes, durchnummeriertes Markdown-Dokument, das wiederum als
Eingabe für die nächste Iteration dient.

## Installation

Der Ordner `requirements-engineering/` (dieser Ordner) muss **unverändert** in eines der von Copilot
unterstützten Skill-Verzeichnisse kopiert werden: <cite>turn1search1</cite><cite>turn1search4</cite>

**Projekt-Skill** (nur in diesem Repository verfügbar, empfohlen für Teams – ins Repo committen):

```bash
mkdir -p .github/skills
cp -r requirements-engineering .github/skills/
git add .github/skills/requirements-engineering
git commit -m "Add requirements-engineering agent skill"
```

Alternative Repo-Speicherorte (funktional identisch): `.claude/skills/`, `.agents/skills/`.

**Personen-Skill** (repoübergreifend, nur lokal bei dir verfügbar):

```bash
mkdir -p ~/.copilot/skills
cp -r requirements-engineering ~/.copilot/skills/
```

Alternativer Personen-Speicherort: `~/.agents/skills/`.

Unter Windows (PowerShell) analog mit `Copy-Item -Recurse` bzw. Ablage unter
`%USERPROFILE%\.copilot\skills\`.

## Verifikation

In VS Code Copilot Chat: `/skills` eingeben – der Skill `requirements-engineering` muss in der Liste
erscheinen. In der Copilot CLI: `gh copilot` bzw. die entsprechende Skill-Übersicht des jeweiligen
Clients nutzen.

## Nutzung

Einfach eine unstrukturierte Anforderung/Beschreibung im Chat einfügen, z. B.:

> Wir brauchen eine Möglichkeit, dass Kunden ihre Bestellung stornieren können, aber nur solange sie
> noch nicht versandt ist.

Copilot erkennt anhand der Skill-Beschreibung automatisch, dass dieser Skill relevant ist, strukturiert
die Anforderung, stellt bei Bedarf Rückfragen und speichert ein Markdown-Dokument
(`requirements.md`, bei weiteren Durchläufen `requirements-01.md`, `requirements-02.md`, …).

Für eine weitere Iteration einfach die vorhandene Datei referenzieren:

> Nutze `requirements-02.md` als Basis, ich habe noch ein paar Antworten zu den offenen Punkten.

## Inhalt dieses Skill-Pakets

| Datei | Zweck |
|---|---|
| `SKILL.md` | Hauptanleitung für Copilot (Ablauf, Rückfragen-Strategie, Dateinamenslogik) |
| `references/format-spezifikation.md` | Exaktes Zielschema (IDs, Status, Priorität, Frontmatter) |
| `references/fragenkatalog.md` | Kategorisierter Fragenkatalog als Orientierung für Rückfragen |
| `assets/document-template.md` | Gliederungsvorlage des Gesamtdokuments |
| `assets/requirement-template.md` | Baustein für eine Einzelanforderung |
| `scripts/next_filename.py` | Ermittelt kollisionsfreien, nummerierten Zieldateinamen |

## Anpassung

- **Anderes ID-Schema/Statusmodell:** `references/format-spezifikation.md` anpassen – Änderungen dort
  wirken sich direkt auf `assets/*.md` und die Parsing-Logik in `SKILL.md` Abschnitt 5 aus.
- **Andere Nummerierung (z. B. `-v1`, `-v2` statt `-01`, `-02`):** Regex in
  `scripts/next_filename.py` (`pattern = re.compile(...)`) sowie Abschnitt 6 in `SKILL.md` anpassen.
- **Zusätzliche Fragenkategorien** (z. B. branchenspezifisch): in `references/fragenkatalog.md`
  ergänzen.

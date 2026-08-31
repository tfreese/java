# Spec-Kit Prompts — Adressbuch (User mit Name & Vorname)

Diese Datei enthält die **exakten Eingaben**, die du der Reihe nach in deinen Coding-Agenten (z. B. GitHub Copilot in VS
Code) eingibst. Jeder Block erzeugt die jeweilige Phasen-Datei. Führe die Commands **in dieser Reihenfolge** aus und
prüfe nach jedem Schritt den erzeugten Output, bevor du weitermachst.

---

## 0. Einmalig: Projekt initialisieren (Terminal, nicht im Chat)

```bash
1. uv package manager installieren:
winget install --id=astral-sh.uv -e

2. verify installation:
uv --version

3. use proxy settings for vw environment:
set UV_SYSTEM_CERTS=true

4. install spec kit:
uv tool install specify-cli --from git+https://github.com/github/spec-kit.git@v0.13.0

5. update sys path:
uv tool update-shell

//uv tool install specify-cli

// Deprecated: Aufrufe mit Punkt /speckit.constitution
//specify init addressbook --integration copilot

// Mit --skills sind die Aufrufe mit Bindestrich /speckit.constitution -> /speckit-constitution
specify init spec-kit-addressbook --integration copilot --integration-options "--skills"

cd address-book
```

Danach öffnest du das Projekt im Agenten und gibst die folgenden Slash-Commands ein.

---

## 1. /speckit_constitution → erzeugt .specify/memory/constitution.md

> Projektweite Tech-Leitplanken.

```
/speckit_constitution #file:inputs/01-constitution.md
```

> Prompt-Inhalt: [`inputs/01-constitution.md`](01-constitution.md)

---

## 2. /speckit_specify → erzeugt specs/001-address-book/spec.md

> Wichtig: NUR Fachlichkeit (WAS/WARUM). Keine Technik.

```
/speckit_specify #file:inputs/02-specify.md
```

> Prompt-Inhalt: [`inputs/02-specify.md`](02-specify.md)

---

## 3. /speckit_clarify → aktualisiert spec.md + Clarify-Log

> Der Agent stellt Fragen. Beantworte sie so:

```
/speckit_clarify #file:inputs/03-clarify.md
```

> Prompt-Inhalt: [`inputs/03-clarify.md`](03-clarify.md)

---

## 4. /speckit_checklist → erzeugt checklists/requirements.md

```
/speckit_checklist #file:inputs/04-checklist.md
```

(Optional weitere Domänen:)

```
/speckit_checklist security
```

> Prompt-Inhalt: [`inputs/04-checklist.md`](04-checklist.md)

---

## 5. /speckit_plan → erzeugt plan.md, research.md, data-model.md, contracts/, quickstart.md

> Erst hier kommt die Technik hinein.

```
/speckit_plan #file:inputs/05-plan.md
```

Prompt-Inhalt: [`inputs/05-plan.md`](05-plan.md)

---

## 6. /speckit_tasks → erzeugt tasks.md

```
/speckit_tasks #file:inputs/06-tasks.md
```

Prompt-Inhalt: [`inputs/06-tasks.md`](06-tasks.md)

---

## 7. /speckit.analyze (Quality-Gate, kein neues Artefakt)

```
/speckit.analyze
```

Prüft Konsistenz über constitution / spec / plan / tasks: Coverage-Lücken, Ambiguitäten, Duplikate,
Constitution-Verstöße. Behebe gemeldete Punkte, bevor du implementierst.

---

## 8. /speckit.implement (optional: Code generieren)

```
/speckit.implement
```

---

## Merksätze

- Nach JEDEM Schritt den Output lesen und per Folge-Prompt iterieren, nicht die .md-Dateien still von Hand
  überschreiben.
- Technik gehört in constitution (projektweit) bzw. plan (feature-spezifisch), NIE in spec.
- Die genauen Werte (z. B. "1 bis 100 Zeichen", "AK-2 leerer Vorname") machen die Anforderungen verifizierbar — genau
  das erzeugt die Präzision.

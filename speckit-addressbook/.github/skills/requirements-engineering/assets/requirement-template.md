<!--
  Baustein für EINE Einzelanforderung. Für jede erfasste Anforderung eine Kopie dieses Blocks
  innerhalb des passenden Kapitels (Funktional / Nicht-funktional / Rahmenbedingung / Geschäftsregel)
  im Gesamtdokument (assets/document-template.md) einfügen.
  Platzhalter in <spitzen Klammern> ersetzen, nicht benötigte optionale Felder weglassen.
-->

### REQ-<KAT>-<NNN>: <Kurztitel der Anforderung>

- **Kategorie:** <Funktional | Nicht-funktional | Rahmenbedingung | Geschäftsregel>
- **Priorität:** <Must | Should | Could | Won't | TBD>
- **Status:** <Entwurf | Offen | Geklärt | Verworfen>
- **Quelle:** <z. B. "Nutzerangabe 2026-09-01" | "abgeleitet/Annahme" | "E-Mail vom ...">

**Beschreibung:**
<Ein atomarer, aktiv formulierter Satz/Absatz. Beispiel: "Das System muss eingehende Bestellungen
innerhalb von 5 Sekunden nach Eingang validieren und bei Validierungsfehlern eine Fehlermeldung mit
Fehlercode an den aufrufenden Client zurückgeben.">

**Akzeptanzkriterien:**
- <Kriterium 1 – möglichst prüfbar/messbar>
- <Kriterium 2>

**Abhängigkeiten:** <z. B. REQ-F-002, REQ-C-001 – oder "keine">

**Akteure/Beteiligte:** <z. B. Endnutzer, Backoffice-Sachbearbeiter, externes System X>

**Offene Punkte:**
- <Wörtliche offene Frage 1, falls Status = Offen>
- <Wörtliche offene Frage 2>

<!-- Wenn Status = Geklärt: Abschnitt "Offene Punkte" komplett entfernen oder "keine" eintragen. -->

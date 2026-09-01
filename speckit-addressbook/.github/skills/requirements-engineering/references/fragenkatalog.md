# Fragenkatalog nach Kategorie

Orientierungshilfe für Rückfragen (siehe SKILL.md Abschnitt 3 "Rückfragen-Strategie"). **Nicht**
schematisch abarbeiten – nur die Fragen stellen, die für die konkrete, gerade erfasste Anforderung
tatsächlich offen sind. Wo möglich als geschlossene Frage oder Bestätigungsfrage formulieren
("Ich nehme an, X – korrekt?"), nicht als offene W-Frage.

## Akteure & Stakeholder

- Wer löst diese Funktion aus (Rolle/Akteur/System)? Ein Akteur oder mehrere mit unterschiedlichen Rechten?
- Gibt es einen Genehmigungs-/Freigabeschritt durch eine zweite Rolle?
- Betrifft die Anforderung auch externe Systeme/Schnittstellenpartner?

## Funktionale Anforderungen

- Was ist der auslösende Trigger (Nutzeraktion, Zeitplan, eingehendes Ereignis)?
- Was ist das erwartete Ergebnis im Erfolgsfall – und woran erkennt man Erfolg (Akzeptanzkriterium)?
- Gibt es Vor-/Nachbedingungen, die erfüllt sein müssen?
- Wie soll das System bei ungültiger Eingabe/Fehlerfall reagieren?
- Gibt es Mengen-/Volumenangaben (z. B. Anzahl gleichzeitiger Vorgänge, Datensatzgröße)?

## Nicht-funktionale Anforderungen

- **Performance:** Welche Antwortzeit/Durchsatz wird erwartet (mit Perzentil, z. B. p95)? Unter
  welcher Last (Anzahl gleichzeitiger Nutzer/Requests pro Sekunde)?
- **Verfügbarkeit:** Welche Verfügbarkeit wird erwartet (z. B. 99,9 %)? Gibt es Wartungsfenster?
- **Skalierbarkeit:** Erwartetes Wachstum (Datenvolumen, Nutzerzahl) über welchen Zeitraum?
- **Sicherheit:** Welche Schutzbedarfsklasse? Authentifizierung/Autorisierung erforderlich?
  Verschlüsselung (at rest/in transit)? Gibt es regulatorische Vorgaben (DSGVO, branchenspezifisch)?
- **Wartbarkeit:** Gibt es Vorgaben zu Logging, Monitoring, Nachvollziehbarkeit (Audit-Trail)?
- **Kompatibilität:** Welche Zielplattformen/Browser/Clients müssen unterstützt werden?

## Daten & Schnittstellen

- Welche Datenobjekte/Entitäten sind betroffen? Wo liegt die "führende" Datenquelle (System of Record)?
- Gibt es bestehende Schnittstellen (REST/SOAP/Datei/Message Queue), die genutzt/erweitert werden
  müssen, oder muss eine neue Schnittstelle entstehen?
- Welches Format/Protokoll wird für den Datenaustausch erwartet?
- Gibt es Vorgaben zur Datenhaltung/-aufbewahrung (Retention)?

## Rahmenbedingungen (Constraints)

- Gibt es technische Vorgaben (Zielplattform, Programmiersprache, Application Server, Datenbank)?
- Gibt es organisatorische Vorgaben (Budget, Termin, verfügbares Team)?
- Gibt es rechtliche/regulatorische Vorgaben, die einzuhalten sind?
- Muss die Lösung mit bestehenden Altsystemen koexistieren?

## Geschäftsregeln

- Gibt es Berechnungsvorschriften, Grenzwerte oder Validierungsregeln, die exakt definiert werden müssen?
- Gibt es Ausnahmefälle von der Regel? Wer darf Ausnahmen genehmigen?
- Ändert sich die Regel je nach Kontext (z. B. Land, Kundensegment, Zeitraum)?

## Fehlerfälle & Edge Cases

- Was passiert bei Systemausfall/Timeout während der Verarbeitung?
- Was passiert bei doppelter Anfrage (Idempotenz erforderlich)?
- Was passiert bei leeren/fehlenden Pflichtdaten?
- Gibt es Grenzwerte (Minimum/Maximum), die geprüft werden müssen?

## Priorisierung & Abnahme

- Ist diese Anforderung `Must`, `Should`, `Could` oder `Won't` für den nächsten Release?
- Wer nimmt die Anforderung fachlich ab?
- Gibt es einen Zieltermin, an den diese Anforderung gebunden ist?

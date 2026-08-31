Baue ein kleines Adressbuch als Web-Anwendung zum Anlegen und Verwalten von Personen.

ZIEL (Warum):
Einträge (Person) mit Vorname und Nachname anlegen, anzeigen, ändern und löschen.
Minimale, saubere Verwaltung von Personeneinträgen als Basis für spätere Erweiterungen.

WER (Akteur):

- Nutzer: legt Einträge (Person) an, sieht die Liste, ruft einzelne Einträge ab, bearbeitet und löscht Einträge.

WAS (funktionale Anforderungen):

- Ein Eintrag besteht aus Vorname und Nachname; beide sind Pflichtfelder.
- Daten werden erst geladen, wenn der Nutzer in einer Suchmaske über der Tabelle einen Suchbegriff eingibt und auf "Suchen" klickt.
- Der Suchbegriff kann, muss aber nicht, Teil eines Vor- oder Nachnamens sein.
- Die Suchmaske soll auch einen Button "Alle anzeigen" enthalten, der die gesamte Tabelle lädt.
- Vorname und Nachname dürfen nicht leer sein und müssen 1 bis 100 Zeichen lang sein.
- Beim Speichern wird eine eindeutige ID aus einer Datenbank Sequenz vergeben.
- Die Tabelle liefert alle Einträge alphabetisch nach Nachname, dann Vorname sortiert.
- Die Tabelle zeigt die Spalten ID, Vorname, Nachname und einen Button "Bearbeiten" und einen Button "Löschen" pro Eintrag.
- Nur die Spalten Vorname und Nachname sind in der Tabelle editierbar, die ID ist readonly.
- Ein Eintrag (Person) kann in der Tabelle geändert werden (Vorname und/oder Nachname).
- Ein Eintrag (Person) ist über seine ID einzeln abrufbar.
- Ein Eintrag (Person) kann über seine ID geändert werden (Vorname und/oder Nachname).
- Ein Eintrag (Person) kann über seine ID gelöscht werden.
- Ein Export muss nicht implementiert werden.
- Die Tabelle muss Pagination unterstützen, wenn mehr als 20 Einträge vorhanden sind.

ERFOLG (Outcome):

- Alle fünf Operationen (Anlegen, Tabelle, Abruf, Ändern, Löschen) funktionieren.
- Ein Eintrag (Person) ohne Vorname oder Name kann nicht gespeichert werden.

GRENZFÄLLE (bitte als Akzeptanzkriterien und ggf. NEEDS-CLARIFICATION aufnehmen):

- Vorname oder Nachname nur aus Leerzeichen -> gilt als leer, Fehler.
- Führende/abschließende Leerzeichen -> trimmen oder ablehnen? (offen)
- Doppelte Einträge mit gleichem Vor- und Nachnamen -> erlaubt? (offen)
- Abruf/Änderung/Löschung mit unbekannter Kennung -> nicht gefunden.

NICHT IM SCOPE (Non-Goals):

- Weitere Felder (E-Mail, Telefon, Adresse).
- Authentifizierung / Mehrbenutzer-Trennung.
- Import/Export.

Formuliere User Storys, funktionale Anforderungen (FR-x) und verifizierbare Akzeptanzkriterien (AK-x) mit konkreten
Werten. Markiere offene Punkte mit [NEEDS CLARIFICATION].

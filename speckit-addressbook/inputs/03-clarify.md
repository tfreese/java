Löse die offenen [NEEDS CLARIFICATION]-Punkte aus der Spezifikation auf.

Vorgaben für die Antworten, falls du danach fragst:

- Führende/abschließende Leerzeichen in Vorname/Name:
  -> Serverseitig trimmen, danach Längenprüfung 1 bis 100 Zeichen.

- Doppelte Einträge mit gleichem Vor- und Nachnamen:
  -> Erlaubt. Eindeutigkeit ausschließlich über ID aus der Sequenz PERSON_SEQ (siehe schema.sql),
     keine Unique-Constraint auf den Namen.

Aktualisiere die Spezifikation entsprechend und entferne die
[NEEDS CLARIFICATION]-Marker.

Erstelle eine Projektverfassung für eine kleine Adressbuch-Web-Anwendung.

GRUNDSÄTZE (nicht verhandelbar), formuliere daraus MUST/SHOULD-Regeln mit kurzer Begründung je Prinzip:

- Security-First: Alle externen Eingaben werden serverseitig geprüft.
  Client-Validierung ist nur UX-Komfort.
- Keine Secrets im Quellcode oder in Git; Bezug nur über Parameter.
- Keine deprecated/removed APIs (kein Security Manager).
- Rückwärtskompatibilität innerhalb einer Major-Version.
- Persistenz ausschließlich über JDBC auf embedded H2 Datenbank.
- Jede fachliche Anforderung MUSS testbare Akzeptanzkriterien besitzen.
- Unit- + Integrationstests; nur für Persistence- und Business-Layer; kritische Pfad Abdeckung >= 80 %.
- Keine personenbezogenen Daten (Namen) im Klartext-Log auf Level INFO.
- Verstöße blockieren das jeweilige Gate (plan/tasks/analyze).

# Coding Guidelines

## Persistence

MUST

- Optimistic Locking unterstützen
- Pagination unterstützen

MUST NOT

- SELECT *
- N+1 Queries
- Ungefilterte Volltabellenscans

## Code Quality

MUST

- Lesbarer, wartbarer Code
- Klare Verantwortlichkeiten
- Defensive Fehlerbehandlung

## Performance

MUST

- Datenbankzugriffe minimieren
- Lazy/Eager Loading bewusst ausw�hlen

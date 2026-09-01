#!/usr/bin/env python3
"""
Ermittelt einen kollisionsfreien Zieldateinamen fuer das requirements-engineering-Skill.

Algorithmus:
  1. <basisname>.md existiert nicht  -> Rueckgabe <basisname>.md (Erstfassung).
  2. <basisname>.md existiert bereits -> hoechste vorhandene Nummer n in
     <basisname>-NN.md (zweistellig, KEIN Limit auf 99 - waechst bei Bedarf auf 3+ Stellen)
     im Zielverzeichnis suchen und n+1 zurueckgeben.

Nutzung:
  python3 next_filename.py <verzeichnis> <basisname> [--ext md]

Beispiele:
  python3 next_filename.py . requirements
    -> ./requirements.md                 (falls noch keine Datei existiert)
    -> ./requirements-03.md              (falls requirements.md, -01, -02 bereits existieren)

Exit-Codes:
  0 = Erfolg, Pfad auf stdout
  1 = Fehlerhafte Argumente
  2 = Verzeichnis existiert nicht
"""
from __future__ import annotations

import argparse
import re
import sys
from pathlib import Path


def next_filename(directory: Path, basename: str, ext: str) -> Path:
    ext = ext.lstrip(".")
    first = directory / f"{basename}.{ext}"
    if not first.exists():
        return first

    pattern = re.compile(rf"^{re.escape(basename)}-(\d+)\.{re.escape(ext)}$")
    highest = 0
    for entry in directory.iterdir():
        if not entry.is_file():
            continue
        match = pattern.match(entry.name)
        if match:
            highest = max(highest, int(match.group(1)))

    next_num = highest + 1
    # Mindestens zweistellig auffuellen, waechst automatisch bei > 99.
    width = max(2, len(str(next_num)))
    return directory / f"{basename}-{str(next_num).zfill(width)}.{ext}"


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("directory", help="Zielverzeichnis, in dem die Datei gespeichert werden soll")
    parser.add_argument("basename", help="Basisname ohne Erweiterung, z. B. 'requirements'")
    parser.add_argument("--ext", default="md", help="Dateierweiterung ohne Punkt (default: md)")
    args = parser.parse_args()

    directory = Path(args.directory).expanduser()
    if not directory.exists():
        print(f"Fehler: Verzeichnis '{directory}' existiert nicht.", file=sys.stderr)
        return 2
    if not directory.is_dir():
        print(f"Fehler: '{directory}' ist kein Verzeichnis.", file=sys.stderr)
        return 2

    result = next_filename(directory, args.basename, args.ext)
    print(result)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

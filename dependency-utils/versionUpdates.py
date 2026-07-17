#!/usr/bin/env python3
#
# Thomas Freese
import os
import subprocess
import sys

print("Suche nach neuen Dependency Versionen...")

# Pfad des Skripts ermitteln.
basedir = os.path.dirname(os.path.abspath(__file__))

# In das Skriptverzeichnis wechseln.
try:
    os.chdir(basedir)
except OSError as e:
    print(f"Fehler beim Verzeichniswechsel nach {basedir}: {e}")
    sys.exit(1)

# rm -rf ../.gradle/configuration-cache/ (Auskommentiert im Original)
# Falls du es aktivieren willst, entferne das '#' in den nächsten Zeilen:
# import shutil
# shutil.rmtree(os.path.join("..", ".gradle", "configuration-cache"), ignore_errors=True)

# ../gradlew --quiet run -> Gradle-Befehl ausführen.
# Wir nutzen shell=True, damit das Skript auf Windows (.bat) und Linux/Mac gleichermaßen läuft.
try:
    gradle_command = os.path.join("..", "gradlew")
    subprocess.run(f"{gradle_command} --quiet run", shell=True, check=True)
except subprocess.CalledProcessError as e:
    print(f"Fehler bei der Gradle-Ausführung: {e}")
    sys.exit(e.returncode)

# cd ~ || exit -> In das Home-Verzeichnis des Benutzers wechseln.
try:
    os.chdir(os.path.expanduser("~"))
except OSError as e:
    print(f"Fehler beim Wechsel ins Home-Verzeichnis: {e}")
    sys.exit(1)

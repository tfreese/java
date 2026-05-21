#!/bin/bash
#
# Thomas Freese
echo "Suche nach neuen Dependency Versionen..."

# BASEDIR=$PWD # Caller directory, current directory
BASEDIR=$(dirname "$0") # Script directory
cd "$BASEDIR" || exit


#rm -rf ../.gradle/configuration-cache/
#../gradlew --quiet build run
../gradlew --quiet run

cd ~ || exit

# Keep Shell open.
#$SHELL

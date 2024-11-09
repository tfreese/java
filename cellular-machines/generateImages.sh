#! /bin/bash
#
# Thomas Freese
# Script zum Erzeugen der Einzelbilder der Simulationen.
#
# ffmpeg -y -f image2 -r 25  -i gof-%05d.png    -c:v png -r 25 -an             gof.avi      # 25  Bilder/Sekunde
# ffmpeg -y -f image2 -r 25  -i wator-%05d.png  -c:v png -r 25 -an -f matroska wator.mkv    # 25  Bilder/Sekunde
# ffmpeg -y -f image2 -r 250 -i ants-%05d.png   -c:v png -r 25 -an -f matroska ants.mkv     # 250 Bilder/Sekunde
# ffmpeg -y -f image2 -r 250 -i balls-%05d.png  -c:v png -r 25 -an -f matroska balls.mkv    # 250 Bilder/Sekunde
#
# 3840 2160
# 1920 1080

#BASEDIR=$PWD #Verzeichnis des Callers, aktuelles Verzeichnis
#BASEDIR=$(dirname $0) #Verzeichnis des Skripts

java -jar "$(dirname $0)"/build/libs/cellular-machines-0.0.1-SNAPSHOT.jar -console -type wator -cycles 1500 -size 3840 2160 -dir /tmp/simulationen

# java -cp "$(dirname $0)"/app/cellular-machines-shaded.jar de.freese.simulationen.SimulationLauncher -console -type wator -cycles 1500 -size 3840 2160 -dir /tmp/simulationen

#!/bin/bash

set -e

NAME=Imputation Butler
EXECUTABLE=imputation-butler
URL=https://github.com/lukfor/imputationbutler/releases/download
INSTALLER_SCRIPT=imputationbutler-installer.sh
INSTALLER_URL=${URL}/latest/${INSTALLER_SCRIPT}

echo "Installing ${NAME} ${VERSION}..."

echo "Downloading ${NAME} from ${INSTALLER_URL}..."
curl -fL ${INSTALLER_URL} -o ${INSTALLER_SCRIPT}

# execute installer
chmod +x ./{INSTALLER_SCRIPT}
./${INSTALLER_SCRIPT}

# change mod for executables
chmod +x ./${EXECUTABLE}

# remove installer
rm ./${INSTALLER_SCRIPT}

echo ""
echo "${NAME} ${CLOUDGENE_VERSION} installation completed. Have fun!"

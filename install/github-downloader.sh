#!/bin/bash

set -e

NAME="Imputation Bot"
VERSION="v0.5.2"
GITHUB_USER="lukfor"
GITHUB_REPO="imputationbot"
EXECUTABLE="imputationbot"
INSTALLER_SCRIPT="imputationbot-installer.sh"

INSTALLER_URL=https://github.com/${GITHUB_USER}/${GITHUB_REPO}/releases/download/${VERSION}/${INSTALLER_SCRIPT}


echo "Installing ${NAME} ${VERSION}..."

echo "Downloading ${NAME} from ${INSTALLER_URL}..."
curl -fL ${INSTALLER_URL} -o ${INSTALLER_SCRIPT}

# execute installer
chmod +x ./${INSTALLER_SCRIPT}
./${INSTALLER_SCRIPT}

# change mod for executables
chmod +x ./${EXECUTABLE}

# remove installer
rm ./${INSTALLER_SCRIPT}

echo ""
GREEN='\033[0;32m'
NC='\033[0m'
echo -e "${GREEN}${NAME} ${VERSION} installation completed. Have fun!${NC}"
echo ""

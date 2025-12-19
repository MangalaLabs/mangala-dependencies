#!/bin/bash

# Publish all modules to GitHub Packages
# Run this script from the project root directory

set -e  # Exit on first error

echo "=========================================="
echo "Publishing All Modules to GitHub Packages"
echo "=========================================="

# Core modules (no dependencies on browser modules)
echo ""
echo "[1/3] Publishing core modules..."
./gradlew \
    :guava:publish \
    :bitcoinj:publish \
    :browser-bridge-api:publish \
    --continue

# Browser modules
echo ""
echo "[2/3] Publishing browser modules..."
./publish-browser-modules.sh

echo ""
echo "[3/3] Done!"
echo ""
echo "=========================================="
echo "All modules published successfully!"
echo "=========================================="
echo ""
echo "Published packages:"
echo "  - com.mangala:guava:1.0"
echo "  - com.mangala:bitcoinj:1.0"
echo "  - com.mangala:browser-bridge-api:1.0"
echo "  - com.mangala.wallet.browser:* (41 modules)"
echo ""
echo "Maven repository: https://maven.pkg.github.com/MangalaLabs/mangala-dependencies"
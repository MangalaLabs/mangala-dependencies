#!/bin/bash

# Publish all browser modules to GitHub Packages
# Modules are published in dependency order (Level 1 -> Level 7)

set -e  # Exit on first error

echo "=========================================="
echo "Publishing Browser Modules to GitHub Packages"
echo "=========================================="

# Level 1: No internal dependencies (15 modules)
echo ""
echo "[Level 1/7] Publishing base modules (15 modules)..."
./gradlew \
    :browser:component_resources:publish \
    :browser:anrs-api:publish \
    :browser:app-build-config-api:publish \
    :browser:app-store:publish \
    :browser:device-auth-api:publish \
    :browser:di:publish \
    :browser:downloads-api:publish \
    :browser:feature-toggles-api:publish \
    :browser:local:publish \
    :browser:macos-api:publish \
    :browser:remote-messaging-api:publish \
    :browser:remote-messaging-store:publish \
    :browser:secure-storage-api:publish \
    :browser:secure-storage-store:publish \
    :browser:traces-api:publish \
    --continue

# Level 2: Depends on Level 1 (4 modules)
echo ""
echo "[Level 2/7] Publishing Level 2 modules (4 modules)..."
./gradlew \
    :browser:common:publish \
    :browser:device-auth-impl:publish \
    :browser:privacy-config-api:publish \
    :browser:traces-impl:publish \
    --continue

# Level 3: Depends on Levels 1-2 (9 modules)
echo ""
echo "[Level 3/7] Publishing Level 3 modules (9 modules)..."
./gradlew \
    :browser:bandwidth-store:publish \
    :browser:common-test:publish \
    :browser:common-ui:publish \
    :browser:downloads-store:publish \
    :browser:feature-toggles-impl:publish \
    :browser:macos-store:publish \
    :browser:privacy-config-store:publish \
    :browser:secure-storage-impl:publish \
    :browser:statistics:publish \
    --continue

# Level 4: Depends on Levels 1-3 (5 modules)
echo ""
echo "[Level 4/7] Publishing Level 4 modules (5 modules)..."
./gradlew \
    :browser:anrs-store:publish \
    :browser:autofill-api:publish \
    :browser:browser-api:publish \
    :browser:crypto:execute:publish \
    :browser:privacy-config-impl:publish \
    --continue

# Level 5: Depends on Levels 1-4 (6 modules)
echo ""
echo "[Level 5/7] Publishing Level 5 modules (6 modules)..."
./gradlew \
    :browser:anrs-impl:publish \
    :browser:autofill-store:publish \
    :browser:bandwidth-impl:publish \
    :browser:downloads-impl:publish \
    :browser:macos-impl:publish \
    :browser:remote-messaging-impl:publish \
    --continue

# Level 6: Depends on Levels 1-5 (1 module)
echo ""
echo "[Level 6/7] Publishing Level 6 modules (1 module)..."
./gradlew \
    :browser:autofill-impl:publish \
    --continue

# Level 7: Final module - browser:app (1 module)
echo ""
echo "[Level 7/7] Publishing browser:app..."
./gradlew \
    :browser:app:publish

echo ""
echo "=========================================="
echo "All browser modules published successfully!"
echo "=========================================="
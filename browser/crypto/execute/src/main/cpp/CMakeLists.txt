cmake_minimum_required(VERSION 3.4.1)

# Configure for 16KB page size support (Android 15+ requirement)
# This ensures compatibility with devices using 16KB memory pages
add_compile_definitions(ANDROID_SUPPORT_FLEXIBLE_PAGE_SIZES)
add_link_options(-Wl,-z,max-page-size=16384)

add_library(keys SHARED
            keys.c)

# Include libraries needed for native-lib lib
target_link_libraries(keys
                      android
                      log)

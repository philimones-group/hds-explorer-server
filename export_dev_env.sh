#!/bin/bash

# Path to your global gradle.properties
GRADLE_PROPERTIES="$HOME/.gradle/gradle.properties"

# Function to read property from gradle.properties (ignores commented lines)
get_gradle_property() {
    local prop_key="$1"
    grep -v '^#' "$GRADLE_PROPERTIES" | grep "^${prop_key}=" | tail -n 1 | cut -d'=' -f2-
}

# Read properties
DB_URL=$(get_gradle_property "DEV_HDS_EXPLORER_SERVER_DB_URL")
DB_USER=$(get_gradle_property "DEV_HDS_EXPLORER_SERVER_DB_USER")
DB_PASS=$(get_gradle_property "DEV_HDS_EXPLORER_SERVER_DB_PASS")

# Export variables for Grails
export DEV_DATABASE_URL="$DB_URL"
export DEV_DATABASE_USER="$DB_USER"
export DEV_DATABASE_PASS="$DB_PASS"

# Confirm exported values
echo "DEV_DATABASE_URL=$DEV_DATABASE_URL"
echo "DEV_DATABASE_USER=$DEV_DATABASE_USER"
echo "DEV_DATABASE_PASS=$DEV_DATABASE_PASS"

# Example: You can uncomment this to directly run your command after exporting
# grails dbm-gorm-diff "$@"

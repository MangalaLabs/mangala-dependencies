# Mangala Dependencies

Contains libraries needed for the Mangala Wallet

## Installation

1. Add your GitHub username in `local.properties` with key `GITHUB_ACTOR` e.g. `GITHUB_ACTOR=your-username`
2. Get a Personal Access Token from GitHub for that account with the `write:packages` scope.
2. Paste the value in `local.properties` with key `GITHUB_TOKEN` e.g. `GITHUB_TOKEN=ghp_1234567890`

## Publishing
1. Be sure to set up your GitHub username and token in `local.properties` (follow the steps in Installation section of this README)
2. Run `./gradlew :modulename:publish` to publish the library to GitHub Packages
3. For modules that depends on other modules, you can run `./gradlew :modulename:publishToMavenLocal` to publish the library to your local maven repository
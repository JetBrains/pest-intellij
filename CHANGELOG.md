<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# PEST IntelliJ Changelog

## [Unreleased]
### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security
## [0.3.3]
### Fixed
- Fixed running with dataset ([#47](https://github.com/pestphp/pest-intellij/pull/47))

## [0.3.2]
### Added
- Added dark/light mode icons ([#45](https://github.com/pestphp/pest-intellij/pull/45))

## [0.3.1]
### Changed
- Change the name of the plugin

## [0.3.0]
### Added
- Basic autocompletion for `$this` for PhpUnit TestCase base class ([#11](https://github.com/pestphp/pest-intellij/pull/11))
- Line markers now works, for the whole file and the single test. ([#17](https://github.com/pestphp/pest-intellij/pull/17), [#24](https://github.com/pestphp/pest-intellij/pull/24))
- Add running support with debugger ([#19](https://github.com/pestphp/pest-intellij/pull/19))
- `$this->field` type support for fields declared in `beforeEach` and `beforeAll` functions ([#22](https://github.com/pestphp/pest-intellij/pull/22))
- Run tests scoped based on the cursor ([#24](https://github.com/pestphp/pest-intellij/pull/24), [#26](https://github.com/pestphp/pest-intellij/pull/26))
- Added support for rerun tests when using new pest version ([#39](https://github.com/pestphp/pest-intellij/pull/39))
- Added coverage support with clover output ([#39](https://github.com/pestphp/pest-intellij/pull/39))

### Changed
- Migrated all Java classes to Kotlin

### Fixed
- Plugin require restart as PhpTestFrameworkType does not support dynamic plugins
- Line markers reported false positives with method calls([#31](https://github.com/pestphp/pest-intellij/pull/31))

## [v0.1.1]
### Added
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

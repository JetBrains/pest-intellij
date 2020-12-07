<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# PEST IntelliJ Changelog

## [Unreleased]
### Added
- Added path mapping support ([#77](https://github.com/pestphp/pest-intellij/pull/77))

### Removed
- Disabled version checking (did not work with path mapping) ([#77](https://github.com/pestphp/pest-intellij/pull/77))

### Fixed
## [0.4.1]
### Added
- Added support for auto-generated `it` test names. ([#72](https://github.com/pestphp/pest-intellij/pull/72))

### Changed
- Made the regex tightly bound and reused the same regex in rerun action. ([#72](https://github.com/pestphp/pest-intellij/pull/72))

## [0.4.0]
### Added
- Added support for showing pest version ([#52](https://github.com/pestphp/pest-intellij/pull/52))
- Type provider for Pest test functions ([#48](https://github.com/pestphp/pest-intellij/pull/48))
- Added support for navigation between tests and test subject ([#53](https://github.com/pestphp/pest-intellij/pull/53))
- Added error reporting to GitHub issues ([#55](https://github.com/pestphp/pest-intellij/pull/55))
- Added inspection for duplicate test names in same file. ([#56](https://github.com/pestphp/pest-intellij/pull/56))
- Added completions for static and protected $this methods. ([#66](https://github.com/pestphp/pest-intellij/pull/66))
- Added completions $this fields declared in beforeEach functions. ([#66](https://github.com/pestphp/pest-intellij/pull/66))
- Added pcov coverage engine support ([#64](https://github.com/pestphp/pest-intellij/pull/64))

### Fixed
- Fixed duplicate test name error when no test name is given yet. ([#61](https://github.com/pestphp/pest-intellij/pull/61))
- Fixed finding tests with namespace at the top. ([#65](https://github.com/pestphp/pest-intellij/pull/65))
- Fixed allow location to be null in location provider. ([#68](https://github.com/pestphp/pest-intellij/pull/68))
- Fixed rerunning tests with namespaces ([#69](https://github.com/pestphp/pest-intellij/pull/69))

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

<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# PEST IntelliJ Changelog

## [Unreleased]
### Added
- Basic autocompletion for `$this` for PhpUnit TestCase base class ([#11](https://github.com/pestphp/pest-intellij/pull/11))
- Line markers now works, for the whole file and the single test. ([#17](https://github.com/pestphp/pest-intellij/pull/17), [#24](https://github.com/pestphp/pest-intellij/pull/24))
- Add running support with debugger ([#19](https://github.com/pestphp/pest-intellij/pull/19))
- `$this->field` type support for fields declared in `beforeEach` and `beforeAll` functions ([#22](https://github.com/pestphp/pest-intellij/pull/22))
- Run tests scoped based on the cursor ([#24](https://github.com/pestphp/pest-intellij/pull/24), [#26](https://github.com/pestphp/pest-intellij/pull/26))

### Fixed
- Plugin require restart as PhpTestFrameworkType does not support dynamic plugins

## [v0.1.1]
### Added
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

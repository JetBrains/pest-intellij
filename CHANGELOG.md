<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# PEST IntelliJ Changelog

## Unreleased

## 1.9.2 - 2023-03-01

### Fixed
- Fixed "Preferred Coverage Engine" not being saved

## 1.9.1 - 2023-02-28

### Fixed
- Fixed ComposerLibraryManager being nullable now.
- Fixed running tests with filenames containing `_`.

### Changed
- Changed logic for base path to be from composer.json file.

## 1.9.0 - 2023-01-15

### Added
- Added support for running specific tests on Pest 2.0
- Added support for running todo's as tests

### Fixed
- Fixed running tests with `?` in the name

## 1.8.3

### Added
- Added support for test names with string concat statements
- Added stacktrace folding for Pest 2.0 output

### Changed
- Removed the "test started at" text on the test console output

### Fixed
- Fixed regex to match tests that have both named and unnamed datasets

## 1.8.1

### Fixed
- Fixed originalFile in iconProvider sometimes being null
- Fixed DuplicateCustomExpectation testing crashing on unfinished inspections

## 1.8.0

### Added
- Added support for using goto location when using remote interpreters

### Fixed
- Fixed nested `readAction` calls in Icon Provider

### Changed
- Changed Icon Provider to use indexes for better performance

## 1.7.0

### Added
- Added `uses->in` folder reference
- Added registry entry for disabling expectation file generation

### Changed
- Changed goto and completion contributor to reference provider
- Changed icons to use build-in dark mode switching

### Fixed
- Fixed dataset reference error when no dataset provided yet.

## 1.6.2

### Fixed
- Fixed duplicate type provider key with nette plugin

## 1.6.1

### Added
- Added inspection for checking if dataset exists

### Fixed
- Fixed dataset autocompletion triggering on all strings
- Fixed dataset goto triggering on all strings

## 1.6.0

### Added
- Added converting multiple `expect` to `and` calls instead
- Added dataset completion
- Added dataset goto

### Fixed
- Fixed automatic case changing on multicased string

## 1.5.0

### Added
- Added automatic case changing to pest test names

## 1.4.2

### Fixed
- Changed runReadAction to runReadActionInSmartMode in startup activity

## 1.4.1

### Changed
- Reduced custom expectation index size by over 95%

### Fixed
- Check if file exist in index (can happen if file is deleted outside IDE)
- Handle path separators per OS

## 1.4.0

### Added
- Added support for dynamic paths in `uses->in` statements
- Added inspection for duplicate custom expectation name
- Add surrounder for `expect`

### Changed
- Define root path from phpunit.xml instead of composer path

### Fixed
- Remove `-` from the pest generated regex
- Escape `/` in regex method name

## 1.3.0

### Fixed
- Changed services to light services for auto disposable
- Fixed null pointer error when no virtual file

### Changed
- Change reporting on GitHub to contain full stacktracepa

### Added
- Added higher order expectation type provider
- Added support for xdebug3 and xdebug2 coverage option

## 1.2.2

### Fixed
- Hide snapshot icon for import statements
- Fix ArrayIndex error from ExpectationFileService
- Fixed wrong file expectation matching in ExpectationFileService

### Added
- Add support for  in  calls
- Added support for running key value datasets

### Changed
- Changed root path for regex to be based of vendor dir location instead of working directory

### Removed
- Remove service message newline requirement as method is deprecated

## 1.2.1

### Fixed
- Moved file generation into smart invocation

## 1.2.0

### Added
- Added gutter icon for snapshots
- Added goto snapshot file

### Fixed
- Rewrote the custom expectation system to use a more robust system
- Updated custom expectation indexer to v2

### Changed
- Removed decorator in favor of implementing interface

## 1.1.0

### Fixed
- Invoke the FileListener PSI part later (should fix indexing issues)
- Fixed stub issues on PestIconProvider by wrapping `runReadAction`
- Fixed `$this->field` not working when namespace exist
- Fixed Concurrent modification errors on expectation file service
- Fixed file generation triggering on projects without pest

### Added
- Added new context type for the root of a pest file
- Added post fix template for `it` tests
- Added live template for `it` test
- Added live template for `test` test
- Added light icon for `pest.php` file

## 1.0.5

### Changed
- Bumped min IntelliJ version to 2021.1

## 1.0.4

### Added
- Added Suppress inspection for `$this->field`

## 1.0.3

### Fixed
- Fixed php type resolving during event dispatching on file listener
- Fixed PSI and index mismatch on file listener

## 1.0.2

### Fixed
- Fixed indexes being out of date in file listener

## 1.0.1

### Fixed
- Removed usage of globalType (needed for 2020.3 support)

## 1.0.0

### Added
- Added structure support for tests
- Added autocompletion for custom expectations
- Added pest icon for the Pest.php config file
- Added symbol contributor for pest tests

### Fixed
- Fixed a read only permission bug when used with Code with me
- Fixed wrong namespace in custom expectations file generation

## 0.4.3

### Added
- Added IntelliJ version to bug report
- Added new Dataset icons (Thanks @caneco!)
- Added test state icons
- Added run all test in file icon

### Fixed
- Fix support for 2021.1
- Fix running tests with circumflex (^)

### Changed
- Bumped min IntelliJ version to 2020.3

## 0.4.2

### Added
- Added path mapping support ([#77](https://github.com/pestphp/pest-intellij/pull/77))

### Changed
- Bumped min plugin version to 2020.2
- Bumped Java version to 11

### Removed
- Disabled version checking (did not work with path mapping) ([#77](https://github.com/pestphp/pest-intellij/pull/77))

### Fixed
- Escape parenthesis in regex for single test ([#80](https://github.com/pestphp/pest-intellij/pull/80))
- Suppressed expression result unused inspection when on Pest test function element. ([#84](https://github.com/pestphp/pest-intellij/pull/84))

## 0.4.1

### Added
- Added support for auto-generated `it` test names. ([#72](https://github.com/pestphp/pest-intellij/pull/72))

### Changed
- Made the regex tightly bound and reused the same regex in rerun action. ([#72](https://github.com/pestphp/pest-intellij/pull/72))

## 0.4.0

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

## 0.3.3

### Fixed
- Fixed running with dataset ([#47](https://github.com/pestphp/pest-intellij/pull/47))

## 0.3.2

### Added
- Added dark/light mode icons ([#45](https://github.com/pestphp/pest-intellij/pull/45))

## 0.3.1

### Changed
- Change the name of the plugin

## 0.3.0

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

## 0.1.1

### Added
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

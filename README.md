<p align="center">
    <img src="/art/banner.png" width="914" title="PEST IntelliJ Banner">
    <p align="center">
        <a href="https://github.com/pestphp/pest/actions"><img alt="GitHub Workflow Status (master)" src="https://github.com/pestphp/pest-intellij/workflows/Build/badge.svg"></a>
        <a href="https://packagist.org/packages/pestphp/pest-intellij"><img alt="Total Downloads" src="https://img.shields.io/jetbrains/plugin/d/14636"></a>
        <a href="https://packagist.org/packages/pestphp/pest-intellij"><img alt="Latest Version" src="https://img.shields.io/jetbrains/plugin/v/14636"></a>
    </p>
</p>

# Pest IntelliJ

<!-- Plugin description -->
This plugin adds support for using Pest PHP inside PHPStorm
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:

  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Pest"</kbd> >
  <kbd>Install Plugin</kbd>

- Manually:

  Download the [latest release](https://github.com/pestphp/pest-intellij/releases/latest) and install it manually using
  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Using Early Access Program (EAP) builds:

  <kbd>Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Manage plugin repositories</kbd>

  Add a new entry for [`https://plugins.jetbrains.com/plugins/eap/14636`](https://plugins.jetbrains.com/plugins/eap/14636)

  Then search for the plugin and install it as usual.

## Configuration

To configure pest to run properly, you need to setup the the proper local test framework

- Navigate to

  <kbd>Preferences</kbd> > <kbd>Languages & Frameworks</kbd> > <kbd>PHP</kbd> > <kbd>Test Frameworks</kbd>

  And add the following two configuration fields:

  Set "Path to Pest Executable" to
	<code>/path/to/your/project/vendor/pestphp/pest/bin/pest/</code>

  Set the "Test Runner" to
	<code>/path/to/your/project/phpunit.xml</code>


---
Plugin based on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).

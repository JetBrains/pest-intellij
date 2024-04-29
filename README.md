<p align="center">
    <img src="/art/banner.png" width="914" title="PEST IntelliJ Banner">
    <p align="center">
        <a href="https://github.com/pestphp/pest/actions"><img alt="GitHub Workflow Status (master)" src="https://github.com/pestphp/pest-intellij/workflows/Build/badge.svg"></a>
        <a href="https://plugins.jetbrains.com/plugin/14636-pest"><img alt="Total Downloads" src="https://img.shields.io/jetbrains/plugin/d/14636"></a>
        <a href="https://plugins.jetbrains.com/plugin/14636-pest"><img alt="Latest Version" src="https://img.shields.io/jetbrains/plugin/v/14636"></a>
	    <a href="https://plugins.jetbrains.com/plugin/14636-pest"><img alt="Latest EAP Version" src="https://img.shields.io/badge/dynamic/xml?label=EAP version&query=%2Fplugin-repository%2Fcategory%2Fidea-plugin%5B1%5D%2Fversion&url=https%3A%2F%2Fplugins.jetbrains.com%2Fplugins%2Flist%3Fchannel%3Deap%26pluginId%3D14636"></a>
	    <a href="https://confluence.jetbrains.com/display/ALL/JetBrains+on+GitHub"><img alt="official JetBrains project" src="https://jb.gg/badges/official.svg"></a>
    </p>
</p>

# Pest IntelliJ

<!-- Plugin description -->
This plugin adds support for using Pest PHP inside PHPStorm

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
	<code>/path/to/your/project/vendor/bin/pest</code>

  Set the "Test Runner" to
	<code>/path/to/your/project/phpunit.xml</code>


## Resources
For a great video course on how to write tests with Pest, check out [Testing Laravel](https://testing-laravel.com/) or [Pest From Scratch](https://laracasts.com/series/pest-from-scratch).

## Issue?
Please report it to [YouTrack](https://youtrack.jetbrains.com/newIssue?project=WI)

## Credits
Originally developed by [Oliver Nybroe](https://github.com/olivernybroe)

<!-- Plugin description end -->

---
Plugin based on the [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template).

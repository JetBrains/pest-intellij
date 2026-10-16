// Copyright 2000-2026 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package com.pestphp.pest;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestExecutionPolicy;
import com.jetbrains.php.config.interpreters.PhpInterpreter;
import com.jetbrains.php.testFramework.PhpTestFrameworkConfiguration;
import com.jetbrains.php.testFramework.PhpTestFrameworkSettingsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Setup and checks for a JUnit 5 Pest test.
 * This is the JUnit 5 replacement of {@code com.pestphp.pest.PestLightCodeFixture}.
 */
@TestOnly
public final class PestTestUtil {
  private static final String TEST_DATA_REL_PATH = "src/test/resources/com/pestphp/pest";
  private static final String MONOREPO_PREFIX = "phpstorm/pest";
  private static final char TEST_NAME_SEPARATOR = '#';

  private PestTestUtil() {
  }

  /**
   * Returns the absolute path of the Pest test data root.
   * The path is correct in the monorepo and in the standalone Pest project.
   * Call it from any thread.
   */
  public static @NotNull String getTestDataPath() {
    String homePath = IdeaTestExecutionPolicy.getHomePathWithPolicy();
    String basePath = Files.exists(Path.of(homePath, MONOREPO_PREFIX, TEST_DATA_REL_PATH))
                      ? "/" + MONOREPO_PREFIX + "/" + TEST_DATA_REL_PATH
                      : "/" + TEST_DATA_REL_PATH;
    return StringUtil.trimEnd(FileUtil.toSystemIndependentName(homePath), "/") + basePath;
  }

  /**
   * Sets the test data path of {@code fixture} to {@code testDataPath}.
   * Then copies {@code stubs.php} of the Pest test data root to the project, when the file exists.
   * Call it in {@code @BeforeEach}, from any thread.
   */
  public static void setUp(@NotNull CodeInsightTestFixture fixture, @NotNull String testDataPath) {
    fixture.setTestDataPath(testDataPath);
    File pestStubsFile = new File(getTestDataPath() + "/stubs.php");
    if (pestStubsFile.exists()) {
      fixture.copyFileToProject(pestStubsFile.getAbsolutePath(), "stubs.php");
    }
  }

  /**
   * Runs the basic completion, and checks that the lookup holds all {@code shouldContain} items.
   * Call it from any thread.
   */
  public static void assertCompletion(@NotNull CodeInsightTestFixture fixture, String @NotNull ... shouldContain) {
    List<String> strings = completeBasic(fixture);
    assertNotNull(strings, "empty completion result");
    List<String> expected = Arrays.asList(shouldContain);
    assertTrue(strings.containsAll(expected), () -> strings + " does not contain all of " + expected);
  }

  /**
   * Runs the basic completion, and checks that the lookup holds exactly the {@code shouldContain} items, in this order.
   * Call it from any thread.
   */
  public static void assertAllCompletion(@NotNull CodeInsightTestFixture fixture, String @NotNull ... shouldContain) {
    List<String> strings = completeBasic(fixture);
    assertNotNull(strings, "empty completion result");
    assertEquals(Arrays.asList(shouldContain), strings);
  }

  /**
   * Runs the basic completion, and checks that the lookup has no items.
   * Call it from any thread.
   */
  public static void assertNoCompletion(@NotNull CodeInsightTestFixture fixture) {
    List<String> strings = completeBasic(fixture);
    assertTrue(strings == null || strings.isEmpty(), () -> "Expected no completion, but got " + strings);
  }

  private static @Nullable List<String> completeBasic(@NotNull CodeInsightTestFixture fixture) {
    fixture.completeBasic();
    return fixture.getLookupElementStrings();
  }

  /**
   * Creates the Pest test framework configuration for a new interpreter in {@code project}.
   * Its executable path is {@code randomPath}.
   * Call it from any thread.
   */
  public static @Nullable PhpTestFrameworkConfiguration createPestFrameworkConfiguration(@NotNull Project project) {
    PhpTestFrameworkConfiguration configuration = PhpTestFrameworkSettingsManager
      .getInstance(project)
      .getOrCreateByInterpreter(PestFrameworkTypeKt.getPEST_FRAMEWORK_TYPE(), new PhpInterpreter(), null, false);
    if (configuration != null) {
      configuration.setExecutablePath("randomPath");
    }
    return configuration;
  }

  /**
   * Copies the files of the test directory to the project, and configures the test file.
   * See {@link #getFileNameBeforeRelativePath(String)} for the paths.
   * Call it from any thread.
   */
  public static @NotNull PsiFile configureByFile(@NotNull CodeInsightTestFixture fixture, @NotNull String testMethodName) {
    String path = getFileNameBeforeRelativePath(testMethodName);
    configureByDirectory(fixture, path);
    return fixture.configureByFile(path);
  }

  private static void configureByDirectory(@NotNull CodeInsightTestFixture fixture, @NotNull String relativePathToDirectory) {
    String testDataPath = fixture.getTestDataPath();
    File fromFile = new File(testDataPath + "/" + relativePathToDirectory);
    if (fromFile.exists() && fromFile.isDirectory()) {
      File[] files = fromFile.listFiles();
      if (files != null) {
        for (File file : files) {
          String relativePath = FileUtil.toSystemIndependentName(file.getPath());
          if (relativePath.startsWith(testDataPath + "/")) {
            relativePath = relativePath.substring(testDataPath.length() + 1);
          }
          fixture.copyFileToProject(relativePath);
        }
      }
    }
  }

  /**
   * Returns the relative path of the test file: the test name of {@code testMethodName} with the {@code .php} extension.
   * A {@code #} in the test name becomes a path separator.
   * Call it from any thread.
   */
  public static @NotNull String getFileNameBeforeRelativePath(@NotNull String testMethodName) {
    String phpTestPath = PlatformTestUtil.getTestName(testMethodName, false) + ".php";
    return phpTestPath.replace(TEST_NAME_SEPARATOR, File.separatorChar);
  }

  /**
   * Returns the absolute path of the test file in the test data path of {@code fixture}.
   * See {@link #getFileNameBeforeRelativePath(String)}.
   * Call it from any thread.
   */
  public static @NotNull String getFileBeforeFullPath(@NotNull CodeInsightTestFixture fixture, @NotNull String testMethodName) {
    return fixture.getTestDataPath() + "/" + getFileNameBeforeRelativePath(testMethodName);
  }
}

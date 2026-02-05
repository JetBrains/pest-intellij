# Pest Plugin

## Mock Usage Guidelines

Prefer MockK to other mocking approaches.

When using MockK, prefer explicit stubbing over inline lambda syntax:

```kotlin
val config = mockk<RunConfiguration>()
every { config.project } returns project
every { config.name } returns "Test"
```
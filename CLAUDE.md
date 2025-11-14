# CLAUDE.md - AI Assistant Guide

This document provides comprehensive information about the java-time-jsptags codebase for AI assistants working with this repository.

## Project Overview

**Name**: Java 8 java.time JSP tags
**Purpose**: JSP tag library providing formatting and parsing support for Java 8's java.time API (JSR-310)
**License**: Apache License 2.0
**Group ID**: net.sargue
**Artifact ID**: java-time-jsptags
**Current Version**: 2.0.2

### Project Status

- **Stable**: Library is in maintenance mode with no planned new features
- **Bugfix only**: Development focuses on fixing issues as they arise
- **Active**: Project is actively maintained for bug reports and community contributions

### Historical Context

- Forked from and based on the original Joda-Time JSP Tags
- Created as a migration path from Joda-Time to Java 8's java.time API
- Version 2.x migrated from Java EE to Jakarta EE package names

## Technology Stack

### Requirements

- **Java**: 17 (toolchain configured in build.gradle)
- **Servlet**: Jakarta Servlet JSP 3.0 (jakarta.servlet.jsp)
- **JSP**: Jakarta JSP 3.0
- **JSTL**: Jakarta JSTL 2.0 (jakarta.servlet.jsp.jstl)
- **Build Tool**: Gradle (with wrapper included)
- **Test Framework**: JUnit 4.13.2
- **Test Support**: Spring Framework 6.0.0-M2 (for MockPageContext)

### Key Dependencies

All runtime dependencies are `compileOnly` to avoid leaking specific JSP/JSTL implementations:
- `jakarta.servlet.jsp:jakarta.servlet.jsp-api:3.0.0` (compileOnly)
- `jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:2.0.0` (compileOnly)

## Repository Structure

```
java-time-jsptags/
├── .github/
│   └── workflows/
│       └── gradle-build.yml          # GitHub Actions CI configuration
├── gradle/
│   └── wrapper/                      # Gradle wrapper files
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── net/sargue/time/jsptags/
│   │   │       ├── FormatTag.java           # Format tag implementation
│   │   │       ├── FormatSupport.java       # Abstract base for format tag
│   │   │       ├── ParseInstantTag.java     # Parse instant tag
│   │   │       ├── ParseLocalDateTag.java   # Parse local date tag
│   │   │       ├── ParseLocalDateTimeTag.java
│   │   │       ├── ParseLocalTimeTag.java
│   │   │       ├── ParseSupport.java        # Abstract base for parse tags
│   │   │       ├── ZoneIdTag.java           # Zone ID scoping tag
│   │   │       ├── SetZoneIdIdTag.java      # Set zone ID tag
│   │   │       ├── ZoneIdSupport.java       # Zone ID support utilities
│   │   │       ├── SetZoneIdSupport.java    # Set zone ID support
│   │   │       ├── JavaTimeTagLibraryValidator.java
│   │   │       ├── Resources.java           # I18N resource bundle support
│   │   │       └── Util.java                # Utility methods
│   │   └── resources/
│   │       ├── META-INF/
│   │       │   └── javatime.tld             # Tag Library Descriptor
│   │       └── net/sargue/time/jsptags/
│   │           ├── Resources.properties     # Default messages
│   │           └── Resources_ja.properties  # Japanese messages
│   └── test/
│       └── java/
│           ├── FormatTagTest.java           # Format tag tests
│           └── ParseLocalDateTagTest.java   # Parse tests
├── build.gradle                      # Gradle build configuration
├── gradle.properties                 # Gradle properties
├── settings.gradle                   # Gradle settings
├── gradlew                          # Gradle wrapper script (Unix)
├── gradlew.bat                      # Gradle wrapper script (Windows)
├── LICENSE.txt                      # Apache 2.0 license
├── NOTICE.txt                       # Attribution notices
└── README.md                        # User documentation
```

## Architecture & Design Patterns

### Tag Class Hierarchy

The library follows a consistent inheritance pattern:

1. **Support Classes** (Abstract base classes)
   - `FormatSupport` extends `TagSupport`
   - `ParseSupport` extends `TagSupport`
   - `ZoneIdSupport` extends `TagSupport`
   - `SetZoneIdSupport` extends `TagSupport`

2. **Tag Implementation Classes** (Concrete implementations)
   - `FormatTag` extends `FormatSupport`
   - `ParseInstantTag` extends `ParseSupport`
   - `ParseLocalDateTag` extends `ParseSupport`
   - `ParseLocalDateTimeTag` extends `ParseSupport`
   - `ParseLocalTimeTag` extends `ParseSupport`
   - `ZoneIdTag` extends `ZoneIdSupport`
   - `SetZoneIdIdTag` extends `SetZoneIdSupport`

### Design Pattern Rationale

- **Template Method**: Support classes define the tag lifecycle (doEndTag, release, init)
- **Strategy Pattern**: Formatter creation varies based on pattern vs. style attributes
- **Separation of Concerns**: Tag classes handle attribute setting; Support classes handle business logic

### Key Components

#### 1. FormatSupport & FormatTag
- **Purpose**: Format any `java.time.TemporalAccessor` for display
- **Attributes**: value, style, pattern, zoneId, locale, var, scope
- **Key Logic**:
  - Creates `DateTimeFormatter` based on pattern or style
  - Applies locale and zone ID configuration
  - Defaults to system zone for time types requiring zone resolution
  - Outputs formatted string or stores in scoped variable

#### 2. ParseSupport & Parse*Tag Family
- **Purpose**: Parse strings into java.time objects (Instant, LocalDate, LocalTime, LocalDateTime)
- **Attributes**: value, style, pattern, zoneId, locale, var, scope
- **Key Logic**:
  - Creates appropriate parser based on pattern or style
  - Applies locale and zone configuration
  - Returns parsed temporal object
  - Each subclass specifies its target type (Instant, LocalDate, etc.)

#### 3. ZoneIdSupport & ZoneIdTag
- **Purpose**: Provide scoped zone ID for nested formatting/parsing tags
- **Pattern**: Scoping tag that sets zone context for child tags
- **Attributes**: value

#### 4. SetZoneIdSupport & SetZoneIdIdTag
- **Purpose**: Store zone ID in page/request/session/application scope
- **Default Variable**: `net.sargue.time.zoneId`
- **Attributes**: value, var, scope

#### 5. Util Class
- **Purpose**: Shared utility methods for tag operations
- **Key Methods**:
  - `getScope(String)`: Convert scope string to PageContext constant
  - `createFormatterForStyle(String)`: Create DateTimeFormatter from style string
  - `parseLocale(String)`: Parse locale string
  - `getFormattingLocale(PageContext, boolean, Locale[])`: Get appropriate locale for formatting

#### 6. Resources Class
- **Purpose**: I18N support with resource bundles
- **Supported Locales**: English (default), Japanese
- **Usage**: Error messages and validation messages

## Code Conventions & Style

### General Conventions

1. **Package Structure**: All code in `net.sargue.time.jsptags`
2. **Indentation**: 4 spaces (no tabs), as per gradle.properties encoding
3. **Naming**:
   - Classes: PascalCase with descriptive names (e.g., `FormatTag`, `ParseSupport`)
   - Methods: camelCase following JavaBean conventions (e.g., `setValue`, `doEndTag`)
   - Attributes: camelCase matching JSP tag attribute names
4. **Comments**: Apache-style license headers on all files

### License Headers

All source files must include the Apache license header:
```java
/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * Modifications, Copyright 2005 Stephen Colebourne, 2014-2015 Sergi Baila
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * ...
 */
```

### Attribute Handling Pattern

Tags follow a consistent pattern for attribute setters:
```java
public void setAttribute(Object value) throws JspTagException {
    if (value == null) {
        this.attribute = null;
    } else if (value instanceof ExpectedType) {
        this.attribute = (ExpectedType) value;
    } else if (value instanceof String) {
        // Parse string to expected type
    } else {
        throw new JspTagException("Error message");
    }
}
```

### Lifecycle Pattern

Tags implement the JSP tag lifecycle:
1. **Constructor**: Calls `init()`
2. **Attribute Setters**: Called by JSP container
3. **doEndTag()**: Main processing logic
4. **release()**: Cleanup, calls `init()` to reset state

### Error Handling

- Use `JspTagException` for attribute validation errors
- Use `JspException` for processing errors
- Wrap `IOException` in `JspTagException` when writing output

## Testing Guidelines

### Test Framework

- **Framework**: JUnit 4
- **Mocking**: Spring MockPageContext and MockServletContext
- **Locale/Timezone**: Tests set explicit defaults for reproducibility

### Test Patterns

```java
@Before
public void setup() {
    Locale.setDefault(Locale.forLanguageTag("ca"));
    TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"));
    mockServletContext = new MockServletContext();
}

@Test
public void testName() throws IOException, JspException {
    // Arrange: Create tag and page context
    FormatTag tag = new FormatTag();
    MockPageContext pageContext = new MockPageContext(mockServletContext);
    tag.setPageContext(pageContext);

    // Act: Set attributes and execute
    tag.setValue(someValue);
    tag.setPattern("yyyy-MM-dd");
    tag.doEndTag();

    // Assert: Verify output
    assertEquals(expected, pageContext.getContentAsString());
}
```

### Test Coverage Areas

1. **Format Tag Tests** (`FormatTagTest.java`)
   - All TemporalAccessor types (Instant, LocalDate, LocalTime, etc.)
   - Style variations (Short, Medium, Long, Full)
   - Pattern-based formatting
   - Zone ID handling
   - Locale-specific formatting

2. **Parse Tag Tests** (`ParseLocalDateTagTest.java`)
   - Basic parsing with patterns
   - Error handling for invalid inputs
   - Scope and variable storage

### Running Tests

```bash
./gradlew test          # Run all tests
./gradlew build         # Build and test
./gradlew clean test    # Clean and test
```

## Build & Development Workflow

### Common Gradle Commands

```bash
# Build project
./gradlew build

# Clean build
./gradlew clean build

# Run tests only
./gradlew test

# Generate Javadoc
./gradlew javadoc

# Create JAR files (main, sources, javadoc)
./gradlew jar

# Publish to local Maven repository
./gradlew publishToMavenLocal

# Publish to Maven Central (requires credentials)
./gradlew publish
```

### Build Configuration Details

**From build.gradle:**
- Java toolchain: Java 17
- Source compatibility: Java 17
- Generates: Main JAR, sources JAR, javadoc JAR
- Signing: GPG signing enabled for Maven Central
- Publishing: Configured for OSSRH (Sonatype)

### Gradle Properties

Configure in `gradle.properties`:
- `sonatypeUsername`: Maven Central username (for publishing)
- `sonatypePassword`: Maven Central password (for publishing)
- `org.gradle.jvmargs=-Dfile.encoding=UTF-8`: Ensures UTF-8 encoding

### CI/CD Pipeline

**GitHub Actions** (`.github/workflows/gradle-build.yml`):
- **Trigger**: On every push
- **Environment**: Ubuntu latest
- **Java**: Temurin JDK 17
- **Steps**:
  1. Checkout code
  2. Setup JDK 17
  3. Setup Gradle
  4. Execute `./gradlew build`

## Tag Library Descriptor (TLD)

**Location**: `src/main/resources/META-INF/javatime.tld`

### TLD Configuration

- **URI**: `http://sargue.net/jsptags/time`
- **Prefix**: `javatime` (conventional)
- **Version**: 2.0 (JSP TLD version)
- **Validator**: `JavaTimeTagLibraryValidator`

### Defined Tags

1. **format**: Format temporal values
2. **parseInstant**: Parse to Instant
3. **parseLocalDate**: Parse to LocalDate
4. **parseLocalTime**: Parse to LocalTime
5. **parseLocalDateTime**: Parse to LocalDateTime
6. **zoneId**: Zone scoping tag
7. **setZoneId**: Set zone in scope

## Common Development Tasks

### Adding a New Tag

1. **Create Support Class** (if needed)
   - Extend `TagSupport`
   - Define protected fields for attributes
   - Implement `doEndTag()` with business logic
   - Implement `init()` for state reset
   - Override `release()` to call `init()`

2. **Create Tag Implementation Class**
   - Extend the Support class
   - Implement public setters for all attributes
   - Handle type conversions (Object/String to expected types)
   - Add proper error handling

3. **Update TLD File**
   - Add `<tag>` element in `javatime.tld`
   - Specify tag name, class, body-content
   - Define all attributes with proper metadata
   - Add documentation in description elements

4. **Write Tests**
   - Create test class extending appropriate pattern
   - Test all attribute combinations
   - Test error conditions
   - Test locale/timezone variations

5. **Update README.md**
   - Add tag documentation
   - Provide usage examples
   - Document attributes

### Modifying Existing Tags

1. **Read existing tests** to understand current behavior
2. **Make changes** to Support or Tag class
3. **Update tests** to cover new/changed behavior
4. **Update TLD** if attributes changed
5. **Update README.md** if public API changed
6. **Run full test suite**: `./gradlew clean test`

### Fixing Bugs

1. **Write failing test** that reproduces the bug
2. **Fix the bug** in source code
3. **Verify test passes**
4. **Check for regressions**: `./gradlew test`
5. **Update documentation** if behavior clarified

## Important Files for AI Assistants

### Critical Files (Read Before Major Changes)

1. **README.md**: User-facing documentation, examples
2. **build.gradle**: Build configuration, dependencies, versions
3. **javatime.tld**: Tag definitions, must stay in sync with code
4. **Util.java**: Shared utilities, referenced throughout codebase
5. **FormatSupport.java**: Reference implementation pattern

### Files Requiring Special Care

- **gradle.properties**: Contains credentials (never commit real credentials)
- **LICENSE.txt**: Legal document, do not modify
- **NOTICE.txt**: Attribution requirements, only add, never remove

### Generated/Ignored Files (Do Not Modify)

- `.gradle/`: Gradle cache
- `build/`: Build outputs
- `.idea/`: IntelliJ IDEA project files
- `*.iml`: IntelliJ module files

## Common Pitfalls & Gotchas

### 1. Zone ID Handling

**Issue**: Different temporal types have different zone requirements
- `Instant`, `LocalDateTime`, `LocalTime`, etc. may need zone for formatting
- Default behavior: Falls back to system default zone

**Solution**: Always test with explicit zone IDs in tests

### 2. Locale-Dependent Tests

**Issue**: Test results vary by system locale
**Solution**: Tests explicitly set `Locale.setDefault()` in `@Before`

### 3. Jakarta vs. Javax Packages

**Issue**: Version 2.x uses Jakarta EE packages (jakarta.servlet.*)
**Important**: Never mix javax.servlet and jakarta.servlet imports

### 4. CompileOnly Dependencies

**Issue**: Runtime JSP/JSTL dependencies are `compileOnly`
**Reason**: Avoids leaking specific implementations to consumers
**Impact**: These dependencies must be provided by the runtime environment

### 5. Tag State Management

**Issue**: JSP container may reuse tag instances
**Solution**: Always implement proper `init()` and `release()` methods

### 6. SuppressWarnings Annotations

Many setters have `@SuppressWarnings("UnusedDeclaration")` because:
- They're called by JSP container via reflection
- IDEs don't detect this usage pattern
- Annotations prevent false positive warnings

## Dependency Updates

### How to Update Dependencies

1. **Check current versions** in `build.gradle`
2. **Verify compatibility**:
   - Jakarta EE version alignment
   - Java version requirements
   - Spring test framework compatibility
3. **Update version numbers** in `build.gradle`
4. **Run full test suite**: `./gradlew clean test`
5. **Check for deprecation warnings**: Review build output
6. **Test build artifacts**: `./gradlew build`

### Critical Dependencies to Monitor

- Jakarta Servlet JSP API (currently 3.0.0)
- Jakarta JSTL API (currently 2.0.0)
- JUnit (currently 4.13.2)
- Spring Test (currently 6.0.0-M2)

## Release Process

### Versioning Strategy

- **Major**: Breaking changes (e.g., 1.x → 2.x for Jakarta migration)
- **Minor**: New features (rare, project is in maintenance mode)
- **Patch**: Bug fixes (e.g., 2.0.0 → 2.0.1 → 2.0.2)

### Pre-Release Checklist

1. All tests pass: `./gradlew clean test`
2. Build succeeds: `./gradlew build`
3. README.md updated with version
4. Changelog section in README.md updated
5. Version in build.gradle updated
6. Git tag created
7. Artifacts built and signed

### Publishing to Maven Central

Requires:
- Sonatype account credentials in `gradle.properties`
- GPG key for signing
- Command: `./gradlew publish`

## Troubleshooting Guide

### Build Failures

**Problem**: "Could not find jakarta.servlet.jsp..."
**Solution**: Check internet connection, Gradle cache may be corrupted
```bash
./gradlew clean --refresh-dependencies
```

**Problem**: "Task 'test' not found"
**Solution**: Ensure you're in project root, use `./gradlew` not `gradle`

### Test Failures

**Problem**: Locale-dependent test failures
**Solution**: Tests set explicit locale, check if default locale setup is correct

**Problem**: Time zone test failures
**Solution**: Check system time zone, tests expect specific zones

### Runtime Issues (For Users)

**Problem**: NoClassDefFoundError for jakarta.servlet classes
**Solution**: Ensure Jakarta EE 9+ runtime with correct JSP/JSTL versions

**Problem**: Tag not found
**Solution**: Verify TLD included in JAR, check taglib declaration in JSP

## Contributing Guidelines

### Pull Request Requirements

1. **Code Quality**:
   - Follows existing code style
   - Includes Apache license header
   - No warnings or errors in build

2. **Testing**:
   - All existing tests pass
   - New tests for new functionality
   - Tests for bug fixes demonstrating the fix

3. **Documentation**:
   - README.md updated if public API changes
   - Javadoc comments for new public methods
   - CLAUDE.md updated for architectural changes

4. **Commit Messages**:
   - Clear, descriptive messages
   - Reference issue numbers if applicable

### Code Review Focus Areas

- Tag lifecycle implementation correctness
- Error handling and validation
- Thread safety (tag reuse considerations)
- Backward compatibility
- Performance implications

## Additional Resources

### External Documentation

- **Java 8 java.time**: https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html
- **DateTimeFormatter**: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
- **JSR-310 Specification**: https://jcp.org/en/jsr/detail?id=310
- **Jakarta Servlet JSP**: https://jakarta.ee/specifications/pages/
- **Javadocs (Published)**: http://www.javadoc.io/doc/net.sargue/java-time-jsptags

### Related Projects

- **Original Joda-Time JSP Tags**: Predecessor project
- **JSTL fmt library**: Similar API for java.util.Date

## Project Contacts

- **Maintainer**: Sergi Baila
- **GitHub**: https://github.com/sargue/java-time-jsptags
- **Issues**: https://github.com/sargue/java-time-jsptags/issues
- **Maven Central**: https://search.maven.org/artifact/net.sargue/java-time-jsptags

---

**Last Updated**: 2025-11-14
**Document Version**: 1.0
**Project Version**: 2.0.2

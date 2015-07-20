## Java 8 java.time JSP tags

This project provides JSP tags for the new java.time package present in Java 8.

The java.time packages are specified in JSR-310 and are based on the Joda-Time library.

This project is forked from and based on the original Joda-Time JSP Tags.

### Project status

The status is currently alpha stage.

I started this project because I needed a replacement of Joda-Time JSP Tags after
migration of a project to Java 8. There are some basic test but the project just
uses some basic formatting so everything about locale, zones, parsing is untested.

### About

This library works very similarly to the date-related tags in the jstl fmt library and
almost exactly as the tags in the original Joda-Time JSP Tags.

### Requirements

* Java 8
* Servlet 2.4
* JSP 2.0
* JSTL 1.1

### Usage

Add the dependency to your project:

#### Gradle
`compile 'net.sargue:java-time-jsptags:1.0.0'`

#### Maven

```xml
<dependency>
    <groupId>net.sargue</groupId>
    <artifactId>java-time-jsptags</artifactId>
    <version>1.0.0</version>
</dependency>
```

#### Tag library declaration

Declare the library as follows in your jsp pages:

```
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
```

#### Tags

##### FORMAT

Example:
```
<javatime:format value="${dt}" style="MS" />
```

Formats any `java.util.Temporal` like `Instant`, `LocalDateTime`, `LocalDate`, `LocalTime`, etc.
The `var` and `scope` attributes can be used to set the value of a variable instead of printing the result.
The time zone may be specified using an attribute, an enclosing `<javatime:zoneId/>` tag,
preceding `<javatime:setZoneId/>` tag, or via the `net.sargue.time.zoneId` scoped variable.

Attributes:

| Attribute        | Description                                                                                           |
|:-----------------|:------------------------------------------------------------------------------------------------------|
| value (required) | Must be a Temporal.                                                                                   |
| var              | The scoped variable to set.                                                                           |
| scope            | The scope of the variable to set.                                                                     |
| locale           | The locale to use for formatting.                                                                     |
| style            | The style to use for formatting (see java.time format documentation for recognized style strings)     |
| pattern          | The pattern to use for formatting (see java.time format documentation for recognized pattern strings) |
| zoneId           | The time zone to use for formatting.                                                                  |

##### PARSEINSTANT

Example:
```
<javatime:parseInstant value="${dt}" style="MS" />
```

Parses a string into an `Instant` object.
The `var` and `scope` attributes can be used to set the value of a variable instead of printing the result.
The time zone may be specified using an attribute, an enclosing `<javatime:zoneId/>` tag,
preceding `<javatime:setZoneId/>` tag, or via the `net.sargue.time.zoneId` scoped variable.

Attributes:

| Attribute | Description                                                                                                                                   |
|:----------|:----------------------------------------------------------------------------------------------------------------------------------------------|
| value     | Required unless value is nested within tag. Must be a string which can be parsed into a `Instant` according to the parsing options specified. |
| var       | The scoped variable to set.                                                                                                                   |
| scope     | The scope of the variable to set.                                                                                                             |
| locale    | The locale to use for parsing.                                                                                                                |
| style     | The style to use for parsing (see java.time format documentation for recognized style strings)                                                |
| pattern   | The pattern to use for parsing (see java.time format documentation for recognized pattern strings)                                            |
| zoneId    | The time zone to use for parsing.                                                                                                             |

##### ZONEID

Example:
```
<javatime:zoneId value="Asia/Bangkok">
  <javatime:format value="${dt}" style="MS" />
</javatime:zoneId>
```

Provides a default time zone to all `<javatime:format/>` tags which are nested within it.
The `<javatime:format/>` tag may override this value with an explicit `zoneId` attribute.

| Attribute        | Description                                   |
|:-----------------|:----------------------------------------------|
| value (required) | The default time zone for nested tags to use. |

##### SETZONEID

Example:
```
<javatime:setZoneId value="Asia/Bangkok" />
<javatime:format value="${dt}" style="MS" />
```
Sets the time zone object in the given scoped variable.
If `var` is not specified, it will be stored in a scoped variable called `net.sargue.time.zoneId`.
The `<javatime:format/>` tag will default to using a time zone stored under this name if it does not have
a `zoneId` attribute and is not nested within a `<javatime:zoneId/>` tag.

| Attribute        | Description                       |
|:-----------------|:----------------------------------|
| value (required) | The time zone to set.             |
| var              | The scoped variable to set.       |
| scope            | The scope of the variable to set. |

### Build

Build is based on gradle. See build.gradle included in the repository.

### Changelog

##### v1.0.0
Some tests added. Minor refactorings and no functionality changed.
Some documentation. Moved to gradle build. Preparing to publish to Maven Central.

##### v0.1
First released version just with some refactoring, no tests, no documentation.

### Contributing

If you found any bug please report it to the GitHub issues page.

PR are welcome but please try to be clear and provide some tests.

[![Build Status](https://travis-ci.org/sargue/java-time-jsptags.svg?branch=master)](https://travis-ci.org/sargue/java-time-jsptags)

Java 8 java.time JSP tags
=========================

This project provides JSP tags for the new java.time package present in Java 8.

The java.time packages are specified in JSR-310 and are based on the Joda-Time 
library.

This project is forked from and based on the original Joda-Time JSP Tags.

Project status
--------------

I started this project because I needed a replacement of Joda-Time JSP Tags
after migration of a project to Java 8.

The library is pretty stable right now. Should you have any problem
please file an issue. There is no planned development for this library,
just bugfix maintenance.

Regarding v2.x and the Java EE to Jakarta EE package name migration
----------------------------------------------------------------------

**TL;DR Use only v2.x of the library if you are migrating your project to the new 
Jakarta EE 9 or higher with the new package names.**

Version 2.0.0 switches to the new Jakarta EE package naming. No other changes 
are introduced but a major version is used as it will break compilation
of existing code. Thanks to [Jon Schewe](https://github.com/jpschewe) for [the 
contribution](https://github.com/sargue/java-time-jsptags/pull/11) that made this possible.


About
-----

This library works very similarly to the date-related tags in the jstl fmt 
library and almost exactly as the tags in the original Joda-Time JSP Tags.

Requirements
------------

* Java 17
* Servlet 5.0
* JSP 3.0
* JSTL 2.0

Usage
-----

Add the dependency to your project:

### Gradle
`compile 'net.sargue:java-time-jsptags:2.0.0'`

### Maven

```xml
<dependency>
    <groupId>net.sargue</groupId>
    <artifactId>java-time-jsptags</artifactId>
    <version>2.0.0</version>
</dependency>
```

### Tag library declaration

Declare the library as follows in your jsp pages:

```
<%@ taglib uri="http://sargue.net/jsptags/time" prefix="javatime" %>
```

### Javadocs

You can [browse online the javadocs](http://www.javadoc.io/doc/net.sargue/java-time-jsptags) thanks to the great [javadoc.io](http://javadoc.io) service.

### *Style* and *pattern* attributes

Most tags have the attributes `style` and `pattern` which control the formatter beneath the tag.

The `style` expected value is two characters, one for date, one for time, from S=Short, M=Medium, L=Long, F=Full, -=None.
They directly map to the enum [`FormatStyle`](https://docs.oracle.com/javase/8/docs/api/java/time/format/FormatStyle.html)

The `pattern` attribute is for complete control over your formatting.
The syntax is explained in the [`DateTimeFormatter`](https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html)

### Time zone (ZoneId)

A time zone may be necessary to perform some formatting. It depends on the
desired format and the value object. An `Instant` has no time zone so if you
want a _long_ style time format (which outputs the time zone) you will need
to adjust using a time zone. A `ZonedDateTime` has all the information needed
but you may want to change the display time zone.

The time zone may be specified using an attribute, an enclosing 
`<javatime:zoneId/>` tag, preceding `<javatime:setZoneId/>` tag, or via the 
`net.sargue.time.zoneId` scoped variable.

The time zone will default to the **system default time-zone** if none is
specified and the value object is one of these classes:

* `Instant`
* `LocalDateTime`
* `LocalTime`
* `OffsetDateTime`
* `OffsetLocalTime`

Tags
====

&lt;javatime:format&gt;
-----------------------

Example:
```
<javatime:format value="${dt}" style="MS" />
```

Formats any `java.util.Temporal` like `Instant`, `LocalDateTime`, `LocalDate`, `LocalTime`, etc.
The `var` and `scope` attributes can be used to set the value of a variable instead of printing the result.

Attributes:

| Attribute        | Description                                                                                           |
|:-----------------|:------------------------------------------------------------------------------------------------------|
| value (required) | Must be a Temporal.                                                                                   |
| var              | The scoped variable to set.                                                                           |
| scope            | The scope of the variable to set.                                                                     |
| locale           | The locale to use for formatting.                                                                     |
| style            | The style to use for formatting (two characters, one for date, one for time, from S=Short, M=Medium, L=Long, F=Full, -=None)|
| pattern          | The pattern to use for formatting (see java.time format documentation for recognized pattern strings) |
| zoneId           | The time zone to use for formatting. See comment above for fallback and defaults.                     |

&lt;javatime:parseInstant&gt;
-----------------------------

Example:
```
<javatime:parseInstant value="${dt}" style="MS" />
```

Parses a string into an `java.time.Instant` object.
The `var` and `scope` attributes can be used to set the value of a variable instead of printing the result.
The time zone may be specified using an attribute, an enclosing `<javatime:zoneId/>` tag,
preceding `<javatime:setZoneId/>` tag, or via the `net.sargue.time.zoneId` scoped variable.

Attributes:

| Attribute | Description                                                                                                                                   |
|:----------|:----------------------------------------------------------------------------------------------------------------------------------------------|
| value     | Required. Must be a string which can be parsed into a `java.time.Instant` according to the parsing options specified. |
| var       | The scoped variable to set.                                                                                                                   |
| scope     | The scope of the variable to set.                                                                                                             |
| locale    | The locale to use for parsing.                                                                                                                |
| style     | The style to use for parsing (two characters, one for date, one for time, from S=Short, M=Medium, L=Long, F=Full, -=None)|
| pattern   | The pattern to use for parsing (see java.time format documentation for recognized pattern strings)                                            |
| zoneId    | The time zone to use for parsing. See comment above for fallback and defaults.                                                                |

&lt;javatime:parseLocalDateTime&gt;
-----------------------------------

Example:
```
<javatime:parseLocalDateTime value="${dt}" style="MS" />
```

Parses a string into an `java.time.LocalDateTime` object.
The `var` and `scope` attributes can be used to set the value of a variable instead of printing the result.
The time zone may be specified using an attribute, an enclosing `<javatime:zoneId/>` tag,
preceding `<javatime:setZoneId/>` tag, or via the `net.sargue.time.zoneId` scoped variable.

Attributes:

| Attribute | Description                                                                                                                                   |
|:----------|:----------------------------------------------------------------------------------------------------------------------------------------------|
| value     | Required. Must be a string which can be parsed into a `java.time.LocalDateTime` according to the parsing options specified. |
| var       | The scoped variable to set.                                                                                                                   |
| scope     | The scope of the variable to set.                                                                                                             |
| locale    | The locale to use for parsing.                                                                                                                |
| style     | The style to use for parsing (two characters, one for date, one for time, from S=Short, M=Medium, L=Long, F=Full, -=None)|
| pattern   | The pattern to use for parsing (see java.time format documentation for recognized pattern strings)                                            |
| zoneId    | The time zone to use for parsing. See comment above for fallback and defaults.                                                                |

&lt;javatime:parseLocalDate&gt;
-------------------------------

Example:
```
<javatime:parseLocalDate value="28/10/2015" pattern="dd/MM/yyyy" />
```

Parses a string into an `java.time.LocalDate` object.
The `var` and `scope` attributes can be used to set the value of a variable instead of printing the result.
The time zone may be specified using an attribute, an enclosing `<javatime:zoneId/>` tag,
preceding `<javatime:setZoneId/>` tag, or via the `net.sargue.time.zoneId` scoped variable.

Attributes:

| Attribute | Description                                                                                                                                   |
|:----------|:----------------------------------------------------------------------------------------------------------------------------------------------|
| value     | Required. Must be a string which can be parsed into a `java.time.LocalDate` according to the parsing options specified. |
| var       | The scoped variable to set.                                                                                                                   |
| scope     | The scope of the variable to set.                                                                                                             |
| locale    | The locale to use for parsing.                                                                                                                |
| style     | The style to use for parsing (two characters, one for date, one for time, from S=Short, M=Medium, L=Long, F=Full, -=None)|
| pattern   | The pattern to use for parsing (see java.time format documentation for recognized pattern strings)                                            |
| zoneId    | The time zone to use for parsing. See comment above for fallback and defaults.                                                                |

&lt;javatime:parseLocalTime&gt;
-------------------------------

Example:
```
<javatime:parseLocalTime value="10:43" pattern="HH:mm" />
```

Parses a string into an `java.time.LocalTime` object.
The `var` and `scope` attributes can be used to set the value of a variable instead of printing the result.
The time zone may be specified using an attribute, an enclosing `<javatime:zoneId/>` tag,
preceding `<javatime:setZoneId/>` tag, or via the `net.sargue.time.zoneId` scoped variable.

Attributes:

| Attribute | Description                                                                                                                                   |
|:----------|:----------------------------------------------------------------------------------------------------------------------------------------------|
| value     | Required. Must be a string which can be parsed into a `java.time.LocalTime` according to the parsing options specified. |
| var       | The scoped variable to set.                                                                                                                   |
| scope     | The scope of the variable to set.                                                                                                             |
| locale    | The locale to use for parsing.                                                                                                                |
| style     | The style to use for parsing (two characters, one for date, one for time, from S=Short, M=Medium, L=Long, F=Full, -=None)|
| pattern   | The pattern to use for parsing (see java.time format documentation for recognized pattern strings)                                            |
| zoneId    | The time zone to use for parsing. See comment above for fallback and defaults.                                                                |

&lt;javatime:zoneId&gt;
-----------------------

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

&lt;javatime:setZoneId&gt;
--------------------------

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

Build
=====

Build is based on gradle. See `build.gradle` included in the repository.

Changelog
---------

### v2.0.2

Changed dependency types to "compileOnly" so this library is not leaking specific JSP/JSTL libraries.

### v2.0.0

Updated for jakarta package names for J2EE classes.

Requires Java 17 now due to dependency on spring-test 6.0.

### v1.1.4
Made helper method public [by request](https://github.com/sargue/java-time-jsptags/issues/7).

### v1.1.3
Fixed issue [#5](https://github.com/sargue/java-time-jsptags/issues/5) about error messages.

### v1.1.2
I have changed the gradle build to use the gradle wrapper and gradle version
2.12 which finally includes a compile-only (like *provided*) configuration.
I have updated the build script acordingly. It shouldn't break any build but I
detected that including this library before this change leaked some undesired
jar files (like the JSTL API).

### v1.1.1
Fixed issue [#2](https://github.com/sargue/java-time-jsptags/issues/2), better 
support of time zones on formatting.

### v1.1.0
Fixed issue [#1](https://github.com/sargue/java-time-jsptags/issues/1), added more parse tags.

### v1.0.0
Some tests added. Minor refactorings and no functionality changed.
Some documentation. Moved to gradle build. Preparing to publish to Maven Central.

### v0.1
First released version just with some refactoring, no tests, no documentation.

Contributing
============

If you found any bug please report it to the GitHub issues page.

PR are welcome but please try to be clear and provide some tests.

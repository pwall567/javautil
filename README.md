# javautil

[![Build Status](https://travis-ci.org/pwall567/javautil.svg?branch=master)](https://travis-ci.org/pwall567/javautil)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/net.pwall.json/javautil?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.pwall.util%22%20AND%20a:%22javautil%22)

Java utility classes, including:

+ `Strings` - split, join, escape, unescape etc.
+ `ParseText` - simplified text parsing
+ `ChunkedArrayList` - an `ArrayList` optimised for sequential growth
+ `ByteArrayBuilder` - similar to `StringBuilder` but for byte arrays
+ `ReaderBuffer` - allows `CharSequence`-style access to the contents of a file
+ `ListMap` - an ordered `Map` optimised for a small numbers of entries

More documentation to follow (Java source has extensive javadoc).

## Dependency Specification

The latest version of the library is 2.4, and it may be obtained from the Maven Central repository.

### Maven
```xml
<dependency>
  <groupId>net.pwall.util</groupId>
  <artifactId>javautil</artifactId>
  <version>2.4</version>
</dependency>
```
### Gradle
```groovy
    implementation 'net.pwall.util:javautil:2.4'
```
### Gradle (kts)
```kotlin
    implementation("net.pwall.util:javautil:2.4")
```

The above version requires Java 8; for those who need a Java 7 version, use version 1.3.

Peter Wall

2020-12-26

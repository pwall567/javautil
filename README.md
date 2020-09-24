# javautil

Java utility classes, including:

+ `Strings` - split, join, escape, unescape etc.
+ `ParseText` - simplified text parsing
+ `ChunkedArrayList` - an `ArrayList` optimised for sequential growth
+ `ByteArrayBuilder` - similar to `StringBuilder` but for byte arrays
+ `ReaderBuffer` - allows `CharSequence`-style access to the contents of a file

More documentation to follow (Java source has extensive javadoc).

## Dependency Specification

The latest version of the library is 2.3, and it may be obtained from the Maven Central repository.

### Maven
```xml
<dependency>
  <groupId>net.pwall.util</groupId>
  <artifactId>javautil</artifactId>
  <version>2.3</version>
</dependency>
```
### Gradle
```groovy
    implementation 'net.pwall.util:javautil:2.3'
```
### Gradle (kts)
```kotlin
    implementation("net.pwall.util:javautil:2.3")
```

The above version requires Java 8; for those who need a Java 7 version, use version 1.3.

Peter Wall

2020-09-23

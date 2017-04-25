# javautil

Java utility classes, including:

+ `Strings` - split, join, escape, unescape etc.
+ `ParseText` - simplified text parsing
+ `ChunkedArrayList` - an `ArrayList` optimised for sequential growth
+ `ByteArrayBuilder` - similar to `StringBuilder` but for byte arrays
+ `ReaderBuffer` - allows `CharSequence`-style access to the contents of a file

More documentation to follow (Java source has extensive javadoc).

## Maven

The library is in the Maven Central Repository; the co-ordinates are:

```xml
<dependency>
  <groupId>net.pwall.util</groupId>
  <artifactId>javautil</artifactId>
  <version>2.0</version>
</dependency>
```

The above version requires Java 8; for those who need a Java 7 version, use:

```xml
<dependency>
  <groupId>net.pwall.util</groupId>
  <artifactId>javautil</artifactId>
  <version>1.3</version>
</dependency>
```

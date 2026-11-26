# Introduction

[yaz4j] is a Java binding for the
high-level, client-side portion of the [YAZ] toolkit known as the ZOOM API.
With yaz4j you can program clients for YAZ-supported protocols like Z39.50,
SRU/W and Solr. yaz4j calls the YAZ shared library directly using the
Java Foreign Function and Memory API, so it is pure Java and needs no
native component of its own. It requires Java 22 or later.

yaz4j is covered by the
[Revised BSD](https://www.indexdata.com/licences/revised-bsd) license.
That should be same as the
[BSD 3 Clause License](https://opensource.org/licenses/BSD-3-Clause).


```java

package org.yaz4j.dinosaur;

import org.junit.*;
import org.yaz4j.Connection;
import org.yaz4j.PrefixQuery;
import org.yaz4j.Record;
import org.yaz4j.ResultSet;
import org.yaz4j.exception.ZoomException;

public class DinosaurTest {
  @Test
  public void test() {
    try (Connection con = new Connection("lx2.loc.gov/LCDB", 0)) {
      con.setSyntax("usmarc");
      con.connect();
      ResultSet set = con.search(new PrefixQuery("@attr 1=7 0253333490"));
      Record rec = set.getRecord(0);
      System.out.println(rec.render());
    } catch (ZoomException ze) {
      Assert.fail(ze.getMessage());
    }
  }
}

```

# Installation

Index Data provides a Maven repository with the artifact
for development as well as a Debian packages for the most
recent Debian and Ubuntu distributions.

## Debian/Ubuntu

The easiest way to install the package is to enable Index Data's
APT. For Debian refer to
[README](https://ftp.indexdata.com/pub/yaz4j/debian/README).
For Ubuntu refer to
[README](https://ftp.indexdata.com/pub/yaz4j/ubuntu/README).
When done, proceed with

    sudo apt update
    sudo apt install yaz4j

## Windows

yaz4j is part of the YAZ package for Windows.
[32-bit](https://ftp.indexdata.com/pub/yaz/win32/)
[64-bit](https://ftp.indexdata.com/pub/yaz/win64/).

# Compilation from source

You will need JDK 22 or later and [Maven](https://maven.apache.org).
The tests also need the [YAZ] shared library installed (for example,
`apt install libyaz5` or `brew install yaz`). No C compiler, SWIG or
YAZ development headers are required.

For compilation of YAZ please consult YAZ [manual].

[yaz4j] can be cloned the [GitHub][yaz4jgithub] repository:

    git clone https://github.com/indexdata/yaz4j.git

Compile, test and package with:

    mvn install

The result is the Java archive `target/yaz4j-VERSION.jar`. The same JAR
works on every platform where YAZ is installed.

# Running

yaz4j loads the YAZ shared library at runtime. It looks for the usual
library names (`libyaz.so.5` on Linux, `libyaz.5.dylib` on macOS,
`yaz5.dll` on Windows) in each `java.library.path` directory, a few
common install locations (Homebrew on macOS, `C:\Program Files\YAZ\bin`
on Windows) and the system's dynamic linker search path. To use a
specific library, set the `yaz4j.library` system property:

    java -Dyaz4j.library=/opt/yaz/lib/libyaz.so.5 ...

Calling native code is a restricted operation. To avoid JDK warnings
(and, in future JDKs, errors), run applications with:

    java --enable-native-access=ALL-UNNAMED ...

or, when yaz4j is on the module path, `--enable-native-access=` followed by
the name of the module that contains yaz4j. The yaz4j JAR's manifest already
enables native access when it is run directly with `java -jar`.

## Using Maven

If you are using Maven to build your application you can include Index Data's
Maven repository and include yaz4j as a dependency in your jar or war project.

You still need the [YAZ] runtime (`apt install libyaz5` or similar)

Index Data's Maven repository (put under `<repositories/>` in pom.xml):

    <repository>
      <id>indexdata</id>
      <name>Index Data</name>
      <url>https://maven.indexdata.com/</url>
    </repository>

yaz4j API dependency (put under `<dependencies/>` in pom.xml):

    <dependency>
      <groupId>org.yaz4j</groupId>
      <artifactId>yaz4j</artifactId>
      <version>VERSION</version>
      <scope>provided</scope>
    </dependency>

It's crucial that the scope of this dependency is set to `provided` for web
application type projects, otherwise the library would end up packaged in 
the .war archive and we wouldn't want that.

yaz4j includes a trivial HTTP to Z39.50 gateway under `examples/zgate` that shows
best how to use yaz4j in a servlet.

[yaz4j]: https://www.indexdata.com/yaz4j

[yaz4jgithub]: https://github.com/indexdata/yaz4j

[YAZ]: https://www.indexdata.com/yaz "YAZ"

[manual]: https://www.indexdata.com/yaz/doc/installation.html




# Introduction

[yaz4j] is a Java binding for the
high-level, client-side portion of the [YAZ] toolkit known as the ZOOM API.
With yaz4j you can program clients for YAZ-supported protocols like Z39.50,
SRU/W and Solr. yaz4j includes a native component and supports Linux
and Windows.

yaz4j is covered by the
[Revised BSD](http://www.indexdata.com/licences/revised-bsd) license.
That should be same as the
[BSD 3 Clause License](http://opensource.org/licenses/BSD-3-Clause).


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
    Connection con = new Connection("lx2.loc.gov/LCDB", 0);
    try {
      con.setSyntax("usmarc");
      con.connect();
      ResultSet set = con.search(new PrefixQuery("@attr 1=7 0253333490"));
      Record rec = set.getRecord(0);
      System.out.println(rec.render());
      set.close();
    } catch (ZoomException ze) {
      Assert.fail(ze.getMessage());
    } finally {
      con.close();
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
[README](http://ftp.indexdata.dk/pub/yaz4j/debian/README).
For Ubuntu refer to
[README](http://ftp.indexdata.dk/pub/yaz4j/ubuntu/README).
When done, proceed with

    sudo apt update
    sudo apt install yaz4j libyaz4j

## Windows

yaz4j is part of the YAZ package for Windows.
[32-bit](http://ftp.indexdata.dk/pub/yaz/win32/)
[64-bit](http://ftp.indexdata.dk/pub/yaz/win64/).

# Compilation from source

You will need JDK, [Maven](https://maven.apache.org),
[SWIG](http://swig.org) and [YAZ] development packages.

For compilation of YAZ please consult YAZ [manual].

[yaz4j] can be cloned the [GitHub][yaz4jgithub] repository:

    git clone https://github.com/indexdata/yaz4j.git

In all cases you'll have to invoke the Maven command `mvn` to
compile yaz4j. For example

    mvn install

## Unix

On Unix, the `yaz-config` utility is used to get compiler flags and
linker libraries for the shared object. Usually it's enough
to install as whole. If there are packages for YAZ already, use
install the "devel" package or "dev" package. Check that `yaz-config`
is in the `PATH`. After running `mvn install`, the result is the Java
archive `target/yaz4j-VERSION.jar` and a shared
object `target/native/libyaz4j.so`.

## Windows

Besides the exact same requirements as in the Unix case (JDK, Maven, SWIG,
YAZ), you will need the Visual Studio to compile yaz4j. Since you
are not using the YAZ installer you are probably also compiling YAZ
on your own. Use same compiler for yaz4j as you use for YAZ.

Use the command prompt provided with the Windows SDK, navigate to the yaz4j
source directory and run:

    mvn install

Default 64-bit YAZ installer location, that is`C:\Program Files\YAZ`,
is assumed for the `yaz.path` property. Nothing is assumed for `swig`, 
so you either need to specify an absolute path or update the `PATH` 
environment variable to include the directory containing 
`swig.exe`. Both can be specified with:

    mvn -Dyaz.path=/path/to/yaz/installdir -Dswig=/path/to/swig/binary install

The compiled jar file ends up in `target/yaz4j-version.jar` while the
native library in `target/native/yaz4j.dll`.

## Using Maven

If you are using maven to build your application you can include Index Data's
maven repository and include yaz4j as a dependency in your jar or war project:

Index Data's Maven repository (put under `<repositories/>` in pom.xml):

    <repository>
      <id>indexdata</id>
      <name>Index Data</name>
      <url>http://maven.indexdata.com/</url>
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

[YAZ]: http://www.indexdata.com/yaz "YAZ"

[manual]: http://www.indexdata.com/yaz/doc/installation.html




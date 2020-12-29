INTRODUCTION
============

[yaz4j] is a Java binding for the
high-level, client-side portion of the [YAZ] toolkit known as the ZOOM API.
With yaz4j you can program clients for YAZ-supported protocols like Z39.50,
SRU/W and Solr. yaz4j includes a native component and supports Linux
and Windows.

yaz4j is covered by the
[Revised BSD](http://www.indexdata.com/licences/revised-bsd) license.
That should be same as the
[BSD 3 Clause License](http://opensource.org/licenses/BSD-3-Clause).

INSTALLATION
============

Index Data provides a Maven repository with the artifact
for development as well as a Debian packages for the most
recent Debian and Ubuntu distributions.


Installing from Debian/Ubuntu
-----------------------------

The easiest way to install the package is to enable Index Data's
APT. For Debian refer to
[README](http://ftp.indexdata.dk/pub/yaz4j/debian/README).
For Ubuntu refer to
[README](http://ftp.indexdata.dk/pub/yaz4j/ubuntu/README).
When done, proceed with

    sudo apt update
    sudo apt install yaz4j libyaz4j

COMPILATION FROM SOURCE
=======================

Unix
----

[yaz4j] can be cloned the [GitHub][yaz4jgithub] repository:

    git clone https://github.com/indexdata/yaz4j.git

You will need JDK, Maven, SWIG and YAZ development packages.
Consult your package manager on how to install those. For compilation
of YAZ please consult YAZ [manual]. If using Debian/Ubuntu, see
debian/control file for packages required.

If yaz-config is in the `PATH`, the following command should suffice:

    mvn install

Windows
-------

(Windows support was removed for version 1.6.0 due to major
changes in the build structure for yaz4j. Will be available again).

Besides the exact same requirements as in the Unix case (JDK, Maven, SWIG,
YAZ), you will need the Windows SDK installed (which in turn requires
.NET Framework 4) to compile yaz4j. Again it's much easier to use the
YAZ Installer. Git must be installed to checkout yaz4j source code.

Use the command prompt provided with the Windows SDK, navigate to the yaz4j
source directory and run:

    mvn install

Default 64-bit YAZ installer location, that is`C:\Program Files\YAZ\`,
is assumed for the `yaz.path` property. Nothing is assumed for `swig`, 
so you either need to specify an absolute path or update the `PATH` 
environment variable to include the directory containing 
`swig.exe`. Both can be specified with:

    mvn -Dyaz.path=/path/to/yaz/installdir -Dswig=/path/to/swig/binary install

The compiled jar file ends up in `any/target/yaz4j.jar` while the native library
in `win32/target/libyaz4j.dll`.


Using Maven
===========

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

[yaz4j]: https:/www.indexdata.com/yaz4j

[yaz4jgithub]: https://github.com/indexdata/yaz4j

[YAZ]: http://www.indexdata.com/yaz "YAZ"

[manual]: http://www.indexdata.com/yaz/doc/installation.html




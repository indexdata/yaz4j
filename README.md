INTRODUCTION
============

Yaz4j is a Java binding for the high-level, client-side portion of the YAZ
toolkit known as the ZOOM API. With Yaz4j you can program clients for YAZ-
supported protocols like Z39.50, SRU/W and Solr. Yaz4j includes a native
component and supports Windows, Linux and OSX.

Yaz4j is covered by the [Revised BSD](http://www.indexdata.com/licences/revised-bsd) license. That should be same as the the [BSD 3 Clause License](http://opensource.org/licenses/BSD-3-Clause).

INSTALLATION
============

Index Data provides ready to use yaz4j RPMs for CentOS 5 and 6, available from
our public YUM repository. On Windows yaz4j can be installed with the YAZ
[installer][4]. Those methods are the simplest ways to get yaz4j up and running
on the particular platforms and are highly recommended.


Index Data YUM repository (CentOS)
----------------------------------

Yaz4j with it's runtime and compilation dependencies are provided through
Index Data's YUM repository, the repository is enabled by placing the following
contents:

    [indexdata-main]
    name=Index Data Main Repository
    baseurl=http://ftp.indexdata.com/pub/yum/centos/6/main/$basearch
    failovermethod=priority
    gpgcheck=1
    gpgkey=file:///etc/pki/rpm-gpg/RPM-GPG-KEY-indexdata
    enabled=1
    priority=1

at `/etc/yum.repos.d/indexdata.repo`.

With the above repository enabled, yaz4j can be simply installed with:

    yum install yaz4j

And the installation can be verified by running a provided test program which
executes a search against a public Index Data Z39.50 test server:

    java -jar /usr/share/java/yaz4j.jar


YAZ Installer (Windows)
-----------------------

YAZ Windows installer can be downloaded from [here][4]. Yaz4j is bundled with
the installer: just make sure during the installation yaz4j box is checked. 
It is also recommended to check the box for updating updating PATH environment
variable with a path to yaz binaries. After installation yaz4j can be tested
with (Java runtime environment required):

    java -jar C:\Program Files\YAZ\java\yaz4j.jar

All native libraries and binaries are located in `C:\Program Files\YAZ\bin\`
after installation.

COMPILATION FROM SOURCE
=======================

Checking out the source code
----------------------------

Yaz4j can be checked out from Index Data's Git repository:

    git clone git://git.indexdata.com/yaz4j

It's recommended to build the latest tagged version (see tags with `git tag`),
e.g:

    git checkout v1.5 -b v1.5


Compilation on CentOS/RHEL
--------------------------

Compilation requires [JDK][1], [Maven][2], [Swig][3] and [YAZ][4] development 
packages installed.

Installing build dependencies can be done through the package manager specific 
for the distribution (subject to availability), for CentOS 5/6 (YUM) JDK and Swig RPMs can be installed  with:

    yum install java-1.7.0-openjdk-devel swig

While YAZ development package needs to be installed from Index Data's YUM 
repository (see INSTALLATION on how to enable the YUM repo):

    yum install libyaz5-devel

Maven is not part of CentOS so a binary distribution needs to be downloaded
from Maven [website][2] and installed manually. Refer to Maven's website for
details. In case Index Data's YUM repo is enabled, Maven 3 can also be 
installed with:

    yum install maven3-indexdata

in which case Maven program is called `mvn-id` rather than `mvn`.

With all dependencies in place you can continue the yaz4j compilation with:

    cd yaz4j
    mvn install

Which will also run tests that open a connection to Index Data's public Z39.50
server.

Notice that `yaz-config` binary must be on the PATH (this is assured when 
`libyaz5-devel` package is installed), if it isn't e.g if a local YAZ (source) 
installation is used, the binary location can be specified with:

    mvn -Dyaz.config=/path/to/yaz-config install

The compiled jar file ends up in any/target/yaz4j.jar while the native library
in unix/target/libyaz4j.so.

Compilation on generic Unix
---------------------------

You will need the JDK, Maven, Swig and YAZ development packages.
Consult your package manager on how to install those.

If yaz-config is in your PATH, the following command should suffice:

    mvn install

If yaz-config is not in your PATH, you'll have to tell where YAZ is located:

    mvn -Dyaz.config=/path/to/yaz-config install

Windows
-------

Besides the exact same requirements as in the Unix case (JDK, Maven, Swig,
YAZ), you will need the Windows [SDK][5] installed (which in turn requires
.NET Framework 4) to compile yaz4j. Again it's much easier to use the
YAZ Installer. Git must be installed to checkout yaz4j source code.

Use the command prompt provided with the Windows SDK, navigate to the yaz4j
source directory and run:

    mvn install

Default 64-bit YAZ installer location is assumed for the 'yaz.path' property.
Nothing is assumed for 'swig', so you either need to specify an absolute path
or update the PATH environment to include the directory containing swig.exe.
Both can be specified with:

    mvn -Dyaz.path=/path/to/yaz/installdir -Dswig=/path/to/swig/binary install

YAZ4J AND A SERVLET CONTAINER
=============================

If you are coding a web application that uses yaz4j there's a couple of things
to keep in mind. First, you are not invoking the JVM directly, but the servlet
container (e.g Tomcat) run/init script is doing that for you and that's the place
to configure any environment settings (e.g the PATH). Second, yaz4j includes
static initializers to load the native part and can't be packaged along with the
webapp as that would break on consecutive redeployments. It must be deployed to
the servlet container common classloader similar to JDBC drivers.

For convenience, `yaz4j-tomcat6` RPM is provided in the ID's YUM repo which will
set up the default RPM-provided Tomcat 6 with yaz4j automatically:

    sudo yum install yaz4j-tomcat6

Linux (CentOS)
--------------

In case when yaz4j is installed through the RPM (Index Data's YUM repo) the
native libraries are placed in the standard system locations (/usr/lib/.. etc)
and are readily available to all applications, including Tomcat. The only other
thing to configure is to add yaz4j.jar (the pure Java component) to Tomcat's
common class loader (further down).

In case when yaz4j is built from source or for some other reason the native
pars are not present in the standard system library locations, they need to be
placed on the servlet  container's shared libraries load path. One way
to do this in Tomcat (assuming Tomcat is run from a tarball rather than RPM) is
by editing  (create it if it does not exist) the CATALINA_HOME/bin/setenv.sh`
script and putting the following lines in there:

    LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/path/to/libyaz4j.so
    export LD_LIBRARY_PATH

Notice, that this has to be done for all native libraries that `yaz4j.so`
dependson, e.g `libyaz5.so` and so on, unless they are already on the default s
ystem library paths (e.g when installed from RPMs).

If Tomcat is started by a custom init scripts similar operation needs to be
performed there.

The pure Java yaz4j.jar must be added to Tomcat's common classloader so that
it becomes available to all deployed webapps and is loaded only once. There
are a couple ways to do that.

One (employed by `yaz4j-tomcat6` RPM) is to place (or symlink) `yaz4j.jar` to
to `$CATALINA_BASE/lib` (CATALINA_BASE is `/usr/share/tomcat6` when Tomcat was
installed from RPMs on CentOS):

    ln -s /path/to/yaz4j.jar /usr/share/tomcat6/lib

and restart Tomcat.

Another option is to edit the file `catalina.properties` shipped with Tomcat
(and located in `CATALINA_BASE/conf/` e.g `/etc/tomcat6/`on RPM-packaged Tomcat)
and extend the `common.loader=` property with the following:

    common.loader=${catalina.base}/lib,${catalina.base}/lib/*.jar,${catalina.home}/lib,${catalina.home}/lib/*.jar,/path/to/yaz4j.jar

again restarting Tomcat afterwards.

Please consult Tomcat documentation for more information on [classloading][6].

Windows
-------

On Windows Tomcat will most likely be run from the binary distribution which
includes `CATALINA_BASE/bin/setenv.bat` for the purpose of setting up the 
environment. Unless, you have installed yaz4j.dll with the YAZ Installer and 
checked the option to update the global PATH and include all native YAZ and
yaz4j components, edit setenv.bat with the following:

    set PATH=%PATH;X:\path\to\yaz\bin;X:\path\to\yaz4j.dll

The `X:\path\to\yaz\bin` is `C:\Program Files\YAZ\bin` when the installer was
used and includes also yaz4j.dll.

In case Tomcat is start up does not execute `setenv.sh`, e.g when custom startup
script is used, please include similar steps there.

To deploy `yaz4j.jar` you must follow steps identical to those from the Linux 
section. Again, when installer is used `yaz4j.jar` is located at `C:\Program Files\YAZ\java\yaz4j.jar`.


Deploy a test app
-----------------

Under yaz4j/examples you'll find a small zgate app. This can be deployed
to Tomcat to test the yaz4j installation. To do so:

    cd yaz4j/examples/zgate
    mvn install
    cp target/zgate.war CATALINA_BASE/webapps

(substitute / with \\ and copy as necessary under Windows!)

If successful you can run the application with a URL as follows:

[http://localhost:8080/zgate/?zurl=z3950.indexdata.com/marc&query=computer&syntax=usmarc](http://localhost:8080/zgate/?zurl=z3950.indexdata.com/marc&query=computer&syntax=usmarc)

PREPARING A DEVELOPMENT ENVIRONMENT
===================================

Maven
-----

If you are using maven to build your application you can include Index Data's
maven repository and include yaz4j as a dependency in your jar or war project:

Index Data's Maven repository (put under `<repositories/>` in pom.xml):

    <repository>
      <id>id-maven-repo</id>
      <name>Indexdata Maven Repository</name>
      <url>http://maven.indexdata.com/</url>
    </repository>

Yaz4j API dependency (put under `<dependencies/>` in pom.xml):

    <dependency>
      <groupId>org.yaz4j</groupId>
      <artifactId>yaz4j-any</artifactId>
      <version>VERSION</version>
      <scope>provided</scope>
    </dependency>


It's crucial that the scope of this dependency is set to `provided` for web
application type projecets, otherwise the library would end up packaged in 
the .war archive and we wouldn't want that.

Yaz4j includes a trivial HTTP to z3590 gateway under `examples/zgate` that shows
best how to use yaz4j in a servlet. There's also a blog entry on building the
gateway [here][7]


LINKS
=====

The following is most probably already outdated, consult Google.

[1]: http://www.oracle.com/technetwork/java/javase/downloads/index.html "JDK"

[2]: http://maven.apache.org/download.cgi "Maven"

[3]: http://www.swig.org/download.html "Swig"

[4]: http://www.indexdata.com/yaz "YAZ"

[5]: http://www.microsoft.com/en-us/download/details.aspx?id=8279 "Windows SDK"

[6]: https://tomcat.apache.org/tomcat-6.0-doc/class-loader-howto.html "Tomcat 6 class loading"

[7]: http://www.indexdata.com/blog/2010/02/building-simple-http-z3950-gateway-using-yaz4j-and-tomcat "Building a simple HTTP-to-Z39.50 gateway using Yaz4j and Tomcat"

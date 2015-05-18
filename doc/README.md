INTRODUCTION
============

Yaz4j is a Java binding for the high-level, client-side portion of the YAZ
toolkit known as the ZOOM API. With Yaz4j you can program clients for YAZ-
supported protocols like z3950, SRU/W and SOLR. Yaz4j includes a native
component and supports Windows, Linux and OSX.


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
executes a search against a public Index Data z3950 test server:

    java -jar /usr/share/java/yaz4j.jar


YAZ Installer (Windows)
-----------------------

YAZ Windows installer can be downloaded from [here][2]. Yaz4j is bundled with
the installer: just make sure during the installation yaz4j box is checked. 
It is also recommended to check the box for updating updating PATH environment
variable with a path to yaz binaries. After installation yaz4j can be tested
with (Java runtime environment required):

  java -jar C:\Program Files\YAZ\java\yaz4j.jar


COMPILATION FROM SOURCE
=======================

Checking out the source code
----------------------------

Yaz4j can be checked out from Index Data's Git repository:

    git clone git://git.indexdata.com/yaz4j

It's recommended to build the latest tagged version (see tags with `git tag`),
e.g:

    git checkout v1.5 -b v1.5


Linux (e.g CentOS)
------------------

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

Which will also run tests that open a connection to Index Data's public z3950
server.

Notice that `yaz-config` binary must be on the PATH (this is assured when 
`libyaz5-devel` package is installed), if it isn't e.g if a local YAZ (source) 
installation is used, the binary location can be specified with:

    mvn -Dyaz.config=/path/to/yaz-config install

The compiled jar file ends up in any/target/yaz4j.jar while the native library
in unix/target/libyaz4j.so.

Windows
-------

Besides the exact same requirements as in the Unix case (JDK, Maven, Swig, YAZ), you will need the Windows [SDK][5] installed (which in turn requires .NET 
Framework 4) to compile yaz4j. Again it's much easier to use the 
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


PREPARING A DEVELOPMENT ENVIRONMENT
===================================

LINKS
=====

The following is most probably already outdated, consult Google.

[1]: http://www.oracle.com/technetwork/java/javase/downloads/index.html "JDK"

[2]: http://maven.apache.org/download.cgi "Maven"

[3]: http://www.swig.org/download.html "Swig"

[4]: http://www.indexdata.com/yaz "YAZ"

[5]: http://www.microsoft.com/en-us/download/details.aspx?id=8279 "Windows SDK"

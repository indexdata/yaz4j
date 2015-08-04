# spec file for yaz4j
%define idmetaversion %(. ./IDMETA; echo $VERSION)
Name:		yaz4j
Version:	%{idmetaversion}
Release:	1.indexdata
Summary:	Z39.50 client toolkit for Java

Group:		Applications/Internet
License:	BSD
URL:		http://www.indexdata.com/yaz4j
Source0:	yaz4j-%{version}.tar.gz
BuildRoot:	%(mktemp -ud %{_tmppath}/%{name}-%{version}-%{release}-XXXXXX)

BuildRequires:	libyaz5-devel
BuildRequires:	redhat-rpm-config 
BuildRequires:	maven3-indexdata
BuildRequires:	swig

Requires:	libyaz5

%description
yaz4j is a toolkit for Java which includes a wrapper for the ZOOM API of YAZ.
This allows developers to write Z39.50/SRU clients in Java. yaz4j supports
both search and scan. See the javadoc for details.

%package -n yaz4j-tomcat6
Summary:	yaz4j tomcat6 integration via symlink
Requires:	yaz4j = %{version}
Requires:	tomcat6
Group:		Applications/Internet

%description -n yaz4j-tomcat6
tomcat6 stuff.

%post
/sbin/ldconfig

%postun
/sbin/ldconfig

%post -n yaz4j-tomcat6
ln -sf %{_datadir}/java/yaz4j.jar /usr/share/tomcat6/lib

%preun -n yaz4j-tomcat6
if [ $1 = 0 ]; then
	rm -f /usr/share/tomcat6/lib/yaz4j.jar
fi

%prep
%setup -q

%build
mvn-id package

%install
rm -rf %{RPM_BUILD_ROOT}
mkdir -p ${RPM_BUILD_ROOT}/%{_datadir}/java
cp any/target/yaz4j.jar ${RPM_BUILD_ROOT}/%{_datadir}/java
mkdir -p ${RPM_BUILD_ROOT}/%{_libdir}
cp unix/target/libyaz4j.so ${RPM_BUILD_ROOT}/%{_libdir}

%clean
rm -rf %{RPM_BUILD_ROOT}

%files
%defattr(-,root,root,-)
%{_datadir}/java/yaz4j.jar
%{_libdir}/*.so
%doc

%files -n yaz4j-tomcat6

%changelog


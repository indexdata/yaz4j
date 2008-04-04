@echo .
@echo .NET Framework 2.0 Software Development Kit (SDK) (x86) or equilavent framework must be installed
@echo This batch file should be called from a DOS command prompt
@echo .
@echo Before calling 'yaz4j.dll' ensure the yaz bin directory (e.g. yaz_3.0.14\bin) is added to the Windows system 'path'
@echo .

@echo .
@echo **********************
@echo **** SETTING PATH ****
set path=%path%;C:\Program Files\Microsoft Visual Studio 8\VC\bin;C:\Program Files\Microsoft Visual Studio 8\Common7\IDE

@echo .
@echo **************
@echo **** SWIG ****
E:/General/Software/Swig/swigwin-1.3.31/swig.exe -I"include" -I"..\dependencies\yaz_3.0.14\include\yaz" -outdir "..\src\org\yaz4j\jni" -package org.yaz4j.jni -o "src\libyaz4j.cpp" -c++ -java libyaz4j.i

@echo If have SWIG errors (e.g. Syntax error in input) the cpp file may still be created correctly

@echo .
@echo ***************************
@echo **** Compiling (yaz4j) ****
cl -c /Gz "/IC:\Program Files\Microsoft Visual Studio 8\VC\include" "/I.\include" "/I..\dependencies\yaz_3.0.14\include" "/I..\dependencies\yaz_3.0.14\include\yaz" "/IC:\Program Files\java\jdk1.5.0_07\include" "/IC:\Program Files\java\jdk1.5.0_07\include\win32" /Fo"obj\yaz4j.obj" "src\libyaz4j.cpp"

@echo .
@echo ********************************
@echo **** Compiling (zoom-extra) ****
cl -c /Gz "/IC:\Program Files\Microsoft Visual Studio 8\VC\include" "/I.\include" "/I..\dependencies\yaz_3.0.14\include" "/I..\dependencies\yaz_3.0.14\include\yaz" "/IC:\Program Files\java\jdk1.5.0_07\include" "/IC:\Program Files\java\jdk1.5.0_07\include\win32" /Fo"obj\zoom-extra.obj" "src\zoom-extra.cpp"

@echo .
@echo *****************
@echo **** Linking ****
link "/LIBPATH:C:\Program Files\Microsoft Visual Studio 8\VC\lib" "/DEFAULTLIB:..\dependencies\yaz_3.0.14\lib\yaz3.lib" "/OUT:lib\yaz4j.dll" "obj\zoom-extra.obj" "obj\yaz4j.obj" /dll
IF EXIST ".\lib\yaz4j.exp". del ".\lib\yaz4j.exp"
IF EXIST ".\lib\yaz4j.lib". del ".\lib\yaz4j.lib"





@echo off

rem -------------- notes only ------------------------

rem .NET Framework 2.0 Software Development Kit (SDK) (x86)
rem http://www.microsoft.com/downloads/details.aspx?familyid=fe6f2099-b7b4-4f47-a244-c96d69c35dec&displaylang=en

rem C++ options:
rem http://msdn2.microsoft.com/en-us/library/91621w01(VS.80).aspx

rem Compiler options:
rem http://msdn2.microsoft.com/en-us/library/19z1t1wy(VS.80).aspx

rem Linker options:
rem http://msdn2.microsoft.com/en-us/library/y0zzbyt4(VS.80).aspx

rem Tried to do above using g++ but could not get the last (link) stage to work, so used microsoft compiler as above
rem E:/General/Software/Swig/swigwin-1.3.31/swig.exe -I"..\dependencies\yaz_3.0.14\include\yaz" -outdir "..\src\org\yaz4j\jni" -package org.yaz4j.jni -o "src\libyaz4j.cpp" -c++ -java libyaz4j.i
rem g++ -I"..\dependencies\yaz_3.0.14\include" -I"..\dependencies\yaz_3.0.14\include\yaz" -I"C:\Program Files\java\jdk1.5.0_13\include" -I"C:\Program Files\java\jdk1.5.0_13\include\win32" -g3 -O0 -Wall -c -o "obj\libyaz4j.o" "src\libyaz4j.cpp"
rem g++ -L"..\dependencies\yaz_3.0.14\lib" -shared "obj\libyaz4j.o" -l"yaz3" -mno-cygwin -Wl,--add-stdcall-alias -Wall -o "lib\yaz4jlib.dll"

rem #strings "obj\libyaz4j.o"

@echo on
#!/bin/bash

echo Swigging the Zoom Headers
swig -I"include" -I"../dependencies/yaz-2.1.28/include/yaz" -I"../dependencies/yaz-2.1.28/include" -outdir ../src/org/yaz4j/jni -package org.yaz4j.jni -o src/libyaz4j.cpp -c++ -java libyaz4j.i

echo Compiling...
g++ -I"include" -I"/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Headers" -I"../dependencies/yaz-2.1.28/include/yaz" -I"../dependencies/yaz-2.1.28/include" -Wall -c -o obj/libyaz4j.o src/libyaz4j.cpp

g++ -I"include" -I"/System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Headers" -I"../dependencies/yaz-2.1.28/include/yaz" -I"../dependencies/yaz-2.1.28/include" -Wall -c -o obj/zoom-extra.o src/zoom-extra.c

echo Linking...
g++ -L"../dependencies/yaz-2.1.28/lib" obj/zoom-extra.o obj/libyaz4j.o -lyaz -bundle -o lib/libyaz4j.so


#!/bin/bash
JNI_DIR=${JAVA_HOME}/include

swig -I"include" -I"../dependencies/yaz-2.1.28/include/yaz/" -outdir ../src/org/yaz4j/jni -package org.yaz4j.jni -o src/libyaz4j.cpp -c++ -java libyaz4j.i

g++ -I"include" -I"$JNI_DIR" -I"$JNI_DIR/linux" -I"../dependencies/yaz-2.1.28/include/yaz" -I"../dependencies/yaz-2.1.28/include" -c -Wall -o obj/libyaz4j.o src/libyaz4j.cpp

g++ -I"include" -I"$JNI_DIR" -I"$JNI_DIR/linux" -I"../dependencies/yaz-2.1.28/include/yaz" -I"../dependencies/yaz-2.1.28/include" -c -Wall -o obj/zoom-extra.o src/zoom-extra.cpp

g++ -L"../dependencies/yaz-2.1.28/lib" -shared obj/libyaz4j.o obj/zoom-extra.o -lyaz -o lib/libyaz4j.so



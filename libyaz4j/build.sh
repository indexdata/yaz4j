#!/bin/bash
JNI_DIR=${JAVA_HOME:-/usr/lib/jvm/java-6-sun}/include
set -x
YAZ_CONFIG=yaz-config
YAZ_PREFIX=`${YAZ_CONFIG} --prefix`
YAZ_CFLAGS=`${YAZ_CONFIG} --cflags`
YAZ_LIBS=`${YAZ_CONFIG} --libs`

swig -I"include" -I"${YAZ_PREFIX}/include" -outdir ../src/org/yaz4j/jni -package org.yaz4j.jni -o src/libyaz4j.cpp -c++ -java libyaz4j.i

g++ -fPIC -I"include" -I"$JNI_DIR" -I"$JNI_DIR/linux" ${YAZ_CFLAGS} -c -Wall -o obj/libyaz4j.o src/libyaz4j.cpp

g++ -fPIC -I"include" -I"$JNI_DIR" -I"$JNI_DIR/linux" ${YAZ_CFLAGS} -c -Wall -o obj/zoom-extra.o src/zoom-extra.cpp

g++ -shared obj/libyaz4j.o obj/zoom-extra.o  -o lib/libyaz4j.so ${YAZ_LIBS}



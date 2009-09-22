JNI_DIR=/usr/lib/jvm/java-6-sun/include
YAZ_CONFIG=yaz-config
YAZ_PREFIX=`${YAZ_CONFIG} --prefix`
YAZ_CFLAGS=`${YAZ_CONFIG} --cflags`
YAZ_LIBS=`${YAZ_CONFIG} --libs`

all: lib/libyaz4j.so

.PHONY: generate-sources

generate-sources: src/libyaz4j.cpp

src/libyaz4j.cpp: libyaz4j.i
	swig -I"include" -I"$(YAZ_PREFIX)/include" -outdir ../src/main/java/org/yaz4j/jni -package org.yaz4j.jni -o src/libyaz4j.cpp -c++ -java libyaz4j.i

obj/libyaz4j.o: src/libyaz4j.cpp
	g++ -fPIC -I"include" -I"$(JNI_DIR)" -I"$(JNI_DIR)/linux" $(YAZ_CFLAGS) -c -Wall -o obj/libyaz4j.o src/libyaz4j.cpp

obj/zoom-extra.o: src/zoom-extra.cpp
	g++ -fPIC -I"include" -I"$(JNI_DIR)" -I"$(JNI_DIR)/linux" $(YAZ_CFLAGS) -c -Wall -o obj/zoom-extra.o src/zoom-extra.cpp

lib/libyaz4j.so: obj/zoom-extra.o obj/libyaz4j.o
	g++ -shared obj/libyaz4j.o obj/zoom-extra.o  -o lib/libyaz4j.so $(YAZ_LIBS)

clean:
	rm -f obj/*.o lib/libyaz4j.* src/libyaz4j.cpp ../src/main/java/org/jni/*.java

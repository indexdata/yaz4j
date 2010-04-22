#!/bin/sh
V=1.2
git archive --format=tar --prefix=yaz4j-$V/ v$V |gzip >yaz4j-$V.tar.gz

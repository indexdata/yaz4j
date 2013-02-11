#!/bin/sh
V=1.3
git archive --format=tar --prefix=yaz4j-$V/ v$V |gzip >yaz4j-$V.tar.gz

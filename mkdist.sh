#!/bin/sh
. ./IDMETA

echo "You should be using mkdist.sh from git-tools"

git archive --format=tar --prefix=$NAME-$VERSION/ HEAD |gzip >$NAME-$VERSION.tar.gz

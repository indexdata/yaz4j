#!/bin/sh
. ./IDMETA

git archive --format=tar --prefix=$NAME-$VERSION/ HEAD |gzip >$NAME-$VERSION.tar.gz

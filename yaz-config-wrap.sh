#!/bin/sh
# yaz-config is unavailable in Debian's libyaz-dev package and pkg-config
# must be used instead.
if which yaz-config >/dev/null 2>&1; then
	exec yaz-config $1
else
	exec pkg-config $1 yaz
fi

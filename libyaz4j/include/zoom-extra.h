#ifndef INCLUDED_WRAPPER
#define INCLUDED_WRAPPER
#include <yaz/zoom.h>
struct CharStarByteArray
{
	char* data;
	long length;
};
struct CharStarByteArray ZOOM_record_get_bytes(ZOOM_record rec, const char *type, int *len);
#endif


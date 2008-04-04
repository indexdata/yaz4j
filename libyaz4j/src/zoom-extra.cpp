#include "zoom-extra.h"
#include <zoom.h>
#include <string.h>

struct CharStarByteArray ZOOM_record_get_bytes(ZOOM_record rec, const char *type, int *len)
{
	struct CharStarByteArray node;
	const char* pageData = ZOOM_record_get(rec, type, len);
	node.length = strlen(pageData);
	node.data = (char*) malloc(node.length+1);
	strcpy(node.data, pageData);
	return node;
}

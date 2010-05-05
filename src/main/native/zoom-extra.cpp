#include "zoom-extra.h"
#include <string.h>

struct CharStarByteArray ZOOM_record_get_bytes(ZOOM_record rec, const char *type) {
    struct CharStarByteArray node;
    node.data = ZOOM_record_get(rec, type, &node.length);
    return node;
}

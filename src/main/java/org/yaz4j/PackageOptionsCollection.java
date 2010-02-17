package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_package_p;
import org.yaz4j.jni.yaz4jlib;

public class PackageOptionsCollection {

    private SWIGTYPE_p_ZOOM_package_p pack = null;

    PackageOptionsCollection(SWIGTYPE_p_ZOOM_package_p pack) {
        this.pack = pack;
    }

    public void dispose() {
        pack = null;
    }

    public String get(String key) {
        return yaz4jlib.ZOOM_package_option_get(pack, key);
    }

    public void set(String key, String value) {
        yaz4jlib.ZOOM_package_option_set(pack, key, value);
    }
}

package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_package_p;
import org.yaz4j.jni.yaz4jlib;

public class Package {

    private SWIGTYPE_p_ZOOM_package_p pack = null;
    private ConnectionExtended connection = null;
    private String type;

    Package(SWIGTYPE_p_ZOOM_package_p pack, ConnectionExtended connection, String type) {
        this.type = type;
        this.connection = connection;
        this.pack = pack;
    }

    public void finalize() {
        dispose();
    }

    public PackageOptionsCollection getPackageOptions() {
        return new PackageOptionsCollection(pack);
    }

    public void send() {
        yaz4jlib.ZOOM_package_send(pack, type);
    }

    public void dispose() {
        if (pack != null) {
            yaz4jlib.ZOOM_package_destroy(pack);
            connection = null;
            pack = null;
        }
    }
}

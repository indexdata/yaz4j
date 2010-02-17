package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_scanset_p;
import org.yaz4j.jni.SWIGTYPE_p_int;
import org.yaz4j.jni.SWIGTYPE_p_size_t;
import org.yaz4j.jni.yaz4jlib;

public class ScanSet {

    private SWIGTYPE_p_ZOOM_scanset_p scanSet = null;
    private Connection connection;
    private boolean disposed = false;

    ScanSet(SWIGTYPE_p_ZOOM_scanset_p scanSet, Connection connection) {
        this.connection = connection;
        this.scanSet = scanSet;
    }

    public void finalize() {
        _dispose();
    }

    public ScanTerm get(long index) {
        SWIGTYPE_p_size_t occ = yaz4jlib.new_size_tp();
        SWIGTYPE_p_size_t length = yaz4jlib.new_size_tp();
        String term = yaz4jlib.ZOOM_scanset_term(scanSet, (long) index, occ, length);
        long occurences = yaz4jlib.size_tp_value(occ);
        yaz4jlib.delete_size_tp(occ);
        yaz4jlib.delete_size_tp(length);
        return new ScanTerm(term, occurences);
    }

    public long getSize() {
        return yaz4jlib.ZOOM_scanset_size(scanSet);
    }

    void _dispose() {
        if (!disposed) {
            yaz4jlib.ZOOM_scanset_destroy(scanSet);
            connection = null;
            scanSet = null;
            disposed = true;
        }
    }
}

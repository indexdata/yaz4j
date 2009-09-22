package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_scanset_p;
import org.yaz4j.jni.SWIGTYPE_p_int;
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
        Dispose();
    }

    public ScanTerm get(long index) {
        SWIGTYPE_p_int occ = yaz4jlib.new_intp();
        SWIGTYPE_p_int length = yaz4jlib.new_intp();
        String term = yaz4jlib.ZOOM_scanset_term(scanSet, (long) index, occ, length);
        int occurences = yaz4jlib.intp_value(occ);
        yaz4jlib.delete_intp(occ);
        yaz4jlib.delete_intp(length);
        return new ScanTerm(term, occurences);
    }

    public long getSize() {
        return yaz4jlib.ZOOM_scanset_size(scanSet);
    }

    public void Dispose() {
        if (!disposed) {
            yaz4jlib.ZOOM_scanset_destroy(scanSet);
            connection = null;
            scanSet = null;
            disposed = true;
        }
    }
}

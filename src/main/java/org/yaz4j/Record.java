package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_record_p;
import org.yaz4j.jni.SWIGTYPE_p_int;
import org.yaz4j.jni.yaz4jlib;

public class Record {

    private SWIGTYPE_p_ZOOM_record_p record = null;
    private ResultSet resultSet = null;
    private boolean disposed = false;

    Record(SWIGTYPE_p_ZOOM_record_p record, ResultSet resultSet) {
        this.resultSet = resultSet;
        this.record = record;
    }

    public void finalize() {
        dispose();
    }

    public byte[] get(String type) {
        SWIGTYPE_p_int length = null;
        return yaz4jlib.ZOOM_record_get_bytes(record, type, length);
    }

    public String render() {
        return new String(get("render"));
    }

    public byte[] getContent() {
        return get("raw");
    }

    public String getSyntax() {
        return new String(get("syntax"));
    }

    public String getDatabase() {
        return new String(get("database"));
    }

    public void dispose() {
        if (!disposed) {
            resultSet = null;
            record = null;
            disposed = true;
        }
    }
}

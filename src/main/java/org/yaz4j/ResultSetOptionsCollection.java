package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_resultset_p;
import org.yaz4j.jni.yaz4jlib;

public class ResultSetOptionsCollection {

    private SWIGTYPE_p_ZOOM_resultset_p resultSet = null;

    ResultSetOptionsCollection(SWIGTYPE_p_ZOOM_resultset_p resultSet) {
        this.resultSet = resultSet;
    }

    public void finalize() {
        resultSet = null;
    }

    public String get(String key) {
        return yaz4jlib.ZOOM_resultset_option_get(resultSet, key);
    }

    public void set(String key, String value) {
        yaz4jlib.ZOOM_resultset_option_set(resultSet, key, value);
    }
}

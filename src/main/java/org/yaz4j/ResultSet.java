package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_record_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_resultset_p;
import org.yaz4j.jni.yaz4jlib;

public class ResultSet {
    //for GC refcount
    private Connection conn;
    private SWIGTYPE_p_ZOOM_resultset_p resultSet;
    private long size = 0;
    private boolean disposed = false;

    ResultSet(SWIGTYPE_p_ZOOM_resultset_p resultSet, Connection conn) {
        this.resultSet = resultSet;
        size = yaz4jlib.ZOOM_resultset_size(this.resultSet);
        this.conn = conn;
    }

    @Override
    public void finalize() {
        this._dispose();
    }

    /**
     * Read option by name.
     * @param name option name
     * @return option value
     */
    public String option(String name) {
      return yaz4jlib.ZOOM_resultset_option_get(resultSet, name);
    }

    /**
     * Write option with a given name.
     * @param name option name
     * @param value option value
     * @return result set (self) for chainability
     */
    public ResultSet option(String name, String value) {
      yaz4jlib.ZOOM_resultset_option_set(resultSet, name, value);
      return this;
    }

    public Record getRecord(int index) {
      SWIGTYPE_p_ZOOM_record_p record = 
        yaz4jlib.ZOOM_resultset_record(resultSet, index);
      return new Record(record, this);
    }

    public long getSize() {
        return size;
    }

    void _dispose() {
        if (!disposed) {
            yaz4jlib.ZOOM_resultset_destroy(resultSet);
            resultSet = null;
            conn = null;
            disposed = true;
        }
    }
}

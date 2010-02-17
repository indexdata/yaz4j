package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_connection_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_record_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_resultset_p;
import org.yaz4j.jni.yaz4jlib;

public class ResultSet {

    private SWIGTYPE_p_ZOOM_resultset_p resultSet;
    private SWIGTYPE_p_ZOOM_connection_p connection;
    private long size = 0;
    private boolean disposed = false;

    ResultSet(SWIGTYPE_p_ZOOM_resultset_p resultSet, SWIGTYPE_p_ZOOM_connection_p connection) {
        this.resultSet = resultSet;
        this.connection = connection;
        size = yaz4jlib.ZOOM_resultset_size(this.resultSet);
    }

    @Override
    public void finalize() {
        this._dispose();
    }

    ResultSetOptionsCollection getResultSetOptions() {
        return new ResultSetOptionsCollection(resultSet);
    }

    public Record getRecord(int index) {
      SWIGTYPE_p_ZOOM_record_p recordTemp = yaz4jlib.ZOOM_resultset_record(resultSet, index);
      return new Record(recordTemp, this);
    }

    public long getSize() {
        return size;
    }

    void _dispose() {
        if (!disposed) {
            yaz4jlib.ZOOM_resultset_destroy(resultSet);
            connection = null;
            resultSet = null;
            disposed = true;
        }
    }
}

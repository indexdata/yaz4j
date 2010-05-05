package org.yaz4j;

import org.yaz4j.exception.ZoomException;
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

  public Record getRecord(int index) throws ZoomException {
    SWIGTYPE_p_ZOOM_record_p record =
      yaz4jlib.ZOOM_resultset_record(resultSet, index);
    //may be out of range or unsupported syntax
    if (record == null) {
      return null;
    }
    int errorCode = yaz4jlib.ZOOM_record_error(record, null, null, null);
    if (errorCode != 0) {
      throw new ZoomException("Record excpetion, code " + errorCode);
    }
    return new Record(record, this);
  }

  public long getHitCount() {
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

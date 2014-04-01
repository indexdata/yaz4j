package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_record_p;
import org.yaz4j.jni.yaz4jlib;

public class Record implements Cloneable {

  private SWIGTYPE_p_ZOOM_record_p record;
  private ResultSet rset;
  private boolean disposed = false;

  Record(SWIGTYPE_p_ZOOM_record_p record, ResultSet rset) {
    this.record = record;
    this.rset = rset;
  }

  protected Record(SWIGTYPE_p_ZOOM_record_p record) {
    this.record = record;
  }

  @Override
  public void finalize() {
    _dispose();
  }

  public byte[] get(String type) {
    if (type == null)
      throw new NullPointerException("type cannot be null");
    return yaz4jlib.ZOOM_record_get_bytes(record, type);
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

  @Override
  public Object clone() {
    SWIGTYPE_p_ZOOM_record_p clone = yaz4jlib.ZOOM_record_clone(record);
    return new Record(clone);
  }

  void _dispose() {
    if (!disposed) {
      //was cloned, need to dealloc?
      if (rset == null) {
        yaz4jlib.ZOOM_record_destroy(record);
      }
      rset = null;
      record = null;
      disposed = true;
    }
  }
}

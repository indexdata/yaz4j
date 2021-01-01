package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_record_p;
import org.yaz4j.jni.yaz4jlib;

import java.io.Closeable;

public class Record implements Closeable {

  private SWIGTYPE_p_ZOOM_record_p record;
  private SWIGTYPE_p_ZOOM_record_p record_cloned;

  protected Record(SWIGTYPE_p_ZOOM_record_p record) {
    this.record = record;
  }

  public byte[] get(String type) {
    check();
    if (type == null)
      throw new IllegalArgumentException("type cannot be null");
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
  public Record clone() {
    check();
    Record newRecord = new Record(yaz4jlib.ZOOM_record_clone(this.record));
    newRecord.record_cloned = newRecord.record;
    return newRecord;
  }

  protected void check() {
    if (record == null) {
      throw new IllegalStateException("record is closed");
    }
  }

  public void close() {
    // only cloned records are really closed
    if (record_cloned != null) {
      yaz4jlib.ZOOM_record_destroy(record_cloned);
      record_cloned = null;
    }
    record = null;
  }
}

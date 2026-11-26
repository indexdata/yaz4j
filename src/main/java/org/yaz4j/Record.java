package org.yaz4j;

import java.lang.foreign.MemorySegment;

import java.io.Closeable;

public class Record implements Closeable {

  private MemorySegment record;
  private MemorySegment record_cloned;

  protected Record(MemorySegment record) {
    this.record = record;
  }

  public byte[] get(String type) {
    check();
    if (type == null)
      throw new IllegalArgumentException("type cannot be null");
    return ZoomNative.recordGet(record, type);
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
    Record newRecord = new Record(ZoomNative.recordClone(this.record));
    newRecord.record_cloned = newRecord.record;
    return newRecord;
  }

  protected void check() {
    if (record == null) {
      throw new IllegalStateException("record is closed");
    }
  }

  @Override
  public void close() {
    // only cloned records are really closed
    if (record_cloned != null) {
      ZoomNative.recordDestroy(record_cloned);
      record_cloned = null;
    }
    record = null;
  }
}

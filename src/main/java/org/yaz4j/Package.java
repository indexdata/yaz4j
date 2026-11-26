package org.yaz4j;

import org.yaz4j.exception.ZoomException;
import java.lang.foreign.MemorySegment;

import java.io.Closeable;

/**
 * Allows to perform an extended-service operation.
 * 
 * Once created, a package is configured by means of options, then the package 
 * is send and the result is inspected (again, by means of options).
 * 
 * @see org.yaz4j.ConnectionExtended#getPackage(java.lang.String)
 * 
 * @author jakub
 */
public class Package implements Closeable {
  private ConnectionExtended conn;
  private MemorySegment pack;
  private final String type;

  Package(MemorySegment pack, ConnectionExtended conn, String type) {
    if (type == null)
      throw new IllegalArgumentException("type cannot be null");
    this.type = type;
    this.pack = pack;
    this.conn = conn;
  }

  /**
   * Write option for a specified key
   * @param key option name
   * @param value option value
   * @return package (self) for chainability
   */
  public Package option(String key, String value) {
    if (key == null)
      throw new IllegalArgumentException("option name cannot be null");
    check();
    ZoomNative.packageOptionSet(pack, key, value);
    return this;
  }

  /**
   * Read option for a specified key.
   * @param key option name
   * @return option value
   */
  public String option(String key) {
    if (key == null)
      throw new IllegalArgumentException("option name cannot be null");
    check();
    return ZoomNative.packageOptionGet(pack, key);
  }

  /**
   * Send the package.
   */
  public void send() throws ZoomException {
    check();
    ZoomNative.packageSend(pack, type);
    ZoomException e = conn.getZoomException();
    if (e != null) {
      throw e;
    }
  }

  protected void check() {
    if (pack == null) {
      throw new IllegalStateException("package is closed");
    }
  }

  @Override
  public void close() {
    if (pack != null) {
      ZoomNative.packageDestroy(pack);
      pack = null;
    }
    conn = null;
  }
}

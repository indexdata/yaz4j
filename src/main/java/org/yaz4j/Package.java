package org.yaz4j;

import org.yaz4j.exception.ZoomException;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_package_p;
import org.yaz4j.jni.yaz4jlib;

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
public class Package {
  //for GC ref count
  private ConnectionExtended conn;
  private SWIGTYPE_p_ZOOM_package_p pack;
  private final String type;

  Package(SWIGTYPE_p_ZOOM_package_p pack, ConnectionExtended conn, String type) {
    if (type == null)
      throw new NullPointerException("type cannot be null");
    this.type = type;
    this.pack = pack;
    this.conn = conn;
  }

  public void finalize() {
    _dispose();
  }

  /**
   * Write option for a specified key
   * @param key option name
   * @param value option value
   * @return package (self) for chainability
   */
  public Package option(String key, String value) {
    if (key == null)
      throw new NullPointerException("option name cannot be null");
    yaz4jlib.ZOOM_package_option_set(pack, key, value);
    return this;
  }

  /**
   * Read option for a specified key.
   * @param key option name
   * @return option value
   */
  public String option(String key) {
    if (key == null)
      throw new NullPointerException("option name cannot be null");
    return yaz4jlib.ZOOM_package_option_get(pack, key);
  }

  /**
   * Send the package.
   */
  public void send() {
    yaz4jlib.ZOOM_package_send(pack, type);
  }

  void _dispose() {
    if (pack != null) {
      yaz4jlib.ZOOM_package_destroy(pack);
      pack = null;
      conn = null;
    }
  }
}

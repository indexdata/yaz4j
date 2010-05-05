package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_package_p;
import org.yaz4j.jni.yaz4jlib;

public class Package {

  private SWIGTYPE_p_ZOOM_package_p pack;
  private ConnectionExtended connection;
  private String type;

  Package(SWIGTYPE_p_ZOOM_package_p pack, ConnectionExtended connection,
    String type) {
    this.type = type;
    this.connection = connection;
    this.pack = pack;
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
    yaz4jlib.ZOOM_package_option_set(pack, key, value);
    return this;
  }

  /**
   * Read option for a specified key.
   * @param key option name
   * @return option value
   */
  public String option(String key) {
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
      connection = null;
      pack = null;
    }
  }
}

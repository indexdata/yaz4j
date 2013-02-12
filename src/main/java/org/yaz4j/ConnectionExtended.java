package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_options_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_package_p;
import org.yaz4j.jni.yaz4jlib;

/**
 * Offers an interface to a subset of the Z39.50 extended services as well as a 
 * few privately defined ones. E.g, Z59.50 ILL, record update, database drop,
 * create, commit.
 * 
 * @see <a href="http://www.indexdata.com/yaz/doc/zoom.extendedservices.html">ZOOM Extended Services</a>
 * 
 * @author jakub
 */
public class ConnectionExtended extends Connection {

  public ConnectionExtended(String host, int port) {
    super(host, port);
  }

  /**
   * Creates an extended-services package for the connection.
   * @param type type of package (operation)
   * @return 
   */
  public Package getPackage(String type) {
    if (closed) {
      throw new IllegalStateException("Connection is closed.");
    }
    Package pack = null;
    SWIGTYPE_p_ZOOM_options_p options = yaz4jlib.ZOOM_options_create();
    SWIGTYPE_p_ZOOM_package_p yazPackage = yaz4jlib.ZOOM_connection_package(
      zoomConnection, options);
    pack = new Package(yazPackage, this, type);
    return pack;
  }
}

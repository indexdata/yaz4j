package org.yaz4j;

import java.lang.foreign.MemorySegment;

import java.util.LinkedList;
import java.util.List;

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

  List<Package> packages = new LinkedList<>(); // all packages returnded

  public ConnectionExtended(String host, int port) {
    super(host, port);
  }

  /**
   * Creates an extended-services package for the connection.
   * @param type type of package (operation)
   * @return 
   */
  public Package getPackage(String type) {
    check();
    MemorySegment options = ZoomNative.optionsCreate();
    MemorySegment yazPackage = ZoomNative.connectionPackage(
      zoomConnection, options);
    Package pack = new Package(yazPackage, this, type);
    packages.add(pack);
    return pack;
  }

  protected void check() {
    super.check();
  }

  @Override
  public void close() {
    super.close();
    for (Package pack: packages) {
      pack.close();
    }
  }
}

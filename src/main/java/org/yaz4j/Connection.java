package org.yaz4j;

import java.io.Closeable;
import org.yaz4j.exception.ZoomException;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_connection_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_query_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_resultset_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_scanset_p;
import org.yaz4j.jni.yaz4jlib;

/**
 * Class representing an on-going communication with an IR server.
 *
 * Creating an instance of this class does not automatically connect (e.g open
 * a socket) to the remote server as the programmer may want to specify options
 * on the object before establishing the actual connection.
 *
 * The work-flow for synchronous (the only addressed) operation when using this
 * class should be as follows (in pseudocode):
 *
 * <blockquote><pre>
 *
 * try {
 *  c = new Connection(...)
 *  //possibly set some options
 *  c.connect //establishes connection
 *  c.search //or other operation
 *  //possibly retrieve records
 * catch (ZoomException e) {
 *  //handle any protocol- or network-level errors
 * } finally {
 *  c.close //close the socket
 * }
 *
 * </pre></blockquote>
 * @see <a href="http://www.indexdata.com/yaz/doc/zoom.html#zoom-connections">YAZ ZOOM Connection</a>
 * @author jakub
 */
public class Connection implements Closeable {
  protected final String host;
  protected final int port;
  protected SWIGTYPE_p_ZOOM_connection_p zoomConnection;
  //connection is initially closed
  protected boolean closed = true;
  private boolean disposed = false;

  public enum QueryType {

    CQLQuery, PrefixQuery
  };

  static {
    // on Linux   'yaz4j' maps to 'libyaz4j.so' (i.e. 'lib' prefix & '.so'  extension)
    // on Windows 'yaz4j' maps to 'yaz4j.dll'   (i.e.                '.dll' extension)
    String libName = "yaz4j";
    System.loadLibrary(libName);
  }

  /**
   * Create new connection object without physically opening a connection to the
   * remote server.
   * @param host host name of the server
   * @param port port of the server
   */
  public Connection(String host, int port) {
    if (host == null)
      throw new NullPointerException("host cannot be null");
    this.host = host;
    this.port = port;
    zoomConnection = yaz4jlib.ZOOM_connection_create(null);
  }

  public void finalize() {
    _dispose();
  }

  /**
   * Performs a search operation (submits the query to the server, waits for
   * response and creates a new result set that allows to retrieve particular
   * results)
   * @deprecated Does not allow specifying sort criteria prior to search
   * use {@link #search(org.yaz4j.Query) search(Query)} instead.
   * @param query search query
   * @param queryType type of the query (e.g RPN. CQL)
   * @return result set containing records (hits)
   * @throws ZoomException protocol or network-level error
   */
  @Deprecated
  public ResultSet search(String query, QueryType queryType) throws
    ZoomException {
    if (query == null)
      throw new NullPointerException("query cannot be null");
    if (queryType == null)
      throw new NullPointerException("queryType cannot be null");
    if (closed)
      throw new IllegalStateException("Connection is closed.");
    SWIGTYPE_p_ZOOM_query_p yazQuery = null;
    if (queryType == QueryType.CQLQuery) {
      yazQuery = yaz4jlib.ZOOM_query_create();
      yaz4jlib.ZOOM_query_cql(yazQuery, query);
    } else if (queryType == QueryType.PrefixQuery) {
      yazQuery = yaz4jlib.ZOOM_query_create();
      yaz4jlib.ZOOM_query_prefix(yazQuery, query);
    }
    SWIGTYPE_p_ZOOM_resultset_p yazResultSet = yaz4jlib.ZOOM_connection_search(
      zoomConnection, yazQuery);
    yaz4jlib.ZOOM_query_destroy(yazQuery);
    ZoomException err = ExceptionUtil.getError(zoomConnection, host,
      port);
    if (err != null) {
      yaz4jlib.ZOOM_resultset_destroy(yazResultSet);
      throw err;
    }
    return new ResultSet(yazResultSet, this);
  }
  
    /**
   * Performs a search operation (submits the query to the server, waits for
   * response and creates a new result set that allows to retrieve particular
   * results). Sort criteria may be specified prior to the search, directly
   * on the query object.
   * @param query search query of any type supported by YAZ.
   * @return result set containing records (hits)
   * @throws ZoomException protocol or network-level error
   */
  public ResultSet search(Query query) throws ZoomException {
    if (query == null)
      throw new NullPointerException("query cannot be null");
    if (closed)
      throw new IllegalStateException("Connection is closed.");
    SWIGTYPE_p_ZOOM_resultset_p yazResultSet = yaz4jlib.ZOOM_connection_search(
      zoomConnection, query.query);
    ZoomException err = ExceptionUtil.getError(zoomConnection, host,
      port);
    if (err != null) {
      yaz4jlib.ZOOM_resultset_destroy(yazResultSet);
      throw err;
    }
    return new ResultSet(yazResultSet, this);
  }
  
  

  /**
   * Performs a scan operation (obtains a list of candidate search terms against
   * a particular access point). 
   * @deprecated Only allows PQF scan queries, use {@link #scan(org.yaz4j.Query) scan(Query)} instead
   * @param query query for scanning
   * @return a scan set with the terms
   * @throws ZoomException a protocol or network-level error
   */
  @Deprecated
  public ScanSet scan(String query) throws ZoomException {
    if (query == null)
      throw new NullPointerException("query cannot be null");
    if (closed)
      throw new IllegalStateException("Connection is closed.");
    SWIGTYPE_p_ZOOM_scanset_p yazScanSet = yaz4jlib.ZOOM_connection_scan(
      zoomConnection, query);
    ZoomException err = ExceptionUtil.getError(zoomConnection, host, port);
    if (err != null) {
      yaz4jlib.ZOOM_scanset_destroy(yazScanSet);
      throw err;
    }
    ScanSet scanSet = new ScanSet(yazScanSet, this);
    return scanSet;
  }
  
    /**
   * Performs a scan operation (obtains a list of candidate search terms against
   * a particular access point). Allows to use both CQL and PQF for Scan.
   * @see <a href="http://www.indexdata.com/yaz/doc/zoom.scan.html">ZOOM-API Scan</a>
   * @param query scan query of type supported by YAZ
   * @return a scan set with the terms
   * @throws ZoomException a protocol or network-level error
   */
  public ScanSet scan(Query query) throws ZoomException {
    if (query == null)
      throw new NullPointerException("query cannot be null");
    if (closed)
      throw new IllegalStateException("Connection is closed.");
    SWIGTYPE_p_ZOOM_scanset_p yazScanSet = yaz4jlib.ZOOM_connection_scan1(
      zoomConnection, query.query);
    ZoomException err = ExceptionUtil.getError(zoomConnection, host, port);
    if (err != null) {
      yaz4jlib.ZOOM_scanset_destroy(yazScanSet);
      throw err;
    }
    ScanSet scanSet = new ScanSet(yazScanSet, this);
    return scanSet;
  }

  /**
   * Establishes a connection to the remote server.
   * @throws ZoomException any (possibly network-level) errors that may occurr
   */
  public void connect() throws ZoomException {
    yaz4jlib.ZOOM_connection_connect(zoomConnection, host, port);
    ZoomException err = ExceptionUtil.getError(zoomConnection, host, port);
    if (err != null) {
      throw err;
    }
    closed = false;
  }

  /**
   * Closes the connection.
   */
  @Override
  public void close() {
    yaz4jlib.ZOOM_connection_close(zoomConnection);
    closed = true;
  }
  
  /**
   * Return exception type from current connection
   *
   * @return null if no error
   */
  ZoomException getZoomException() {
    ZoomException err = ExceptionUtil.getError(zoomConnection, host, port);
    return err;
  }

  /**
   * Write option with a given name.
   * @param name option name
   * @param value option value
   * @return connection (self) for chainability
   */
  public Connection option(String name, String value) {
    if (name == null)
      throw new NullPointerException("option name cannot be null");
    yaz4jlib.ZOOM_connection_option_set(zoomConnection, name, value);
    return this;
  }

  /**
   * Read option with a given name
   * @param name option name
   * @return option value
   */
  public String option(String name) {
    if (name == null)
      throw new NullPointerException("option name cannot be null");
    return yaz4jlib.ZOOM_connection_option_get(zoomConnection, name);
  }

  /**
   * Same as option("preferredRecordSyntax")
   * @return value of preferred record syntax
   */
  public String getSyntax() {
    return option("preferredRecordSyntax");
  }

  /**
   * Same as option("preferredRecordSyntax", value)
   * @param value value of preferred record syntax
   */
  public void setSyntax(String value) {
    option("preferredRecordSyntax", value);
  }

  /**
   * Same as option("databaseName")
   * @return value of databaseName
   */
  public String getDatabaseName() {
    return option("databaseName");
  }

  /**
   * Same as option("databaseName", value)
   * @param value value of databaseName
   */
  public void setDatabaseName(String value) {
    option("databaseName", value);
  }

  /**
   * Same as option("user")
   * @return value of user
   */
  public String getUsername() {
    return option("user");
  }

  /**
   * Same as option("user", value)
   * @param value value of user
   */
  public void setUsername(String value) {
    option("user", value);
  }

  /**
   * Same as option("password")
   * @return value of password
   */
  public String getPassword() {
    return option("password");
  }

  /**
   * Same as option("password", value)
   * @param value
   */
  public void setPassword(String value) {
    option("password", value);
  }

  /**
   * INTERNAL, GC-ONLY
   */
  void _dispose() {
    if (!disposed) {
      yaz4jlib.ZOOM_connection_destroy(zoomConnection);
      zoomConnection = null;
      disposed = true;
    }
  }
}

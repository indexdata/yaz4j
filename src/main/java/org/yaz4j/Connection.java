package org.yaz4j;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.yaz4j.exception.ZoomException;
import java.lang.foreign.MemorySegment;

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
  private final String host;
  private final int port;
  protected MemorySegment zoomConnection;
  private List<ResultSet> resultSets = new LinkedList<>(); // all result sets returned
  private List<ScanSet> scanSets = new LinkedList<>(); // all scan sets returned

  public enum QueryType {
    CQLQuery, PrefixQuery
  }

  static Logger logger = Logger.getLogger("Connection");

  /**
   * Create new connection object without physically opening a connection to the
   * remote server.
   * @param host host name of the server
   * @param port port of the server
   */
  public Connection(String host, int port) {
    if (host == null)
      throw new IllegalArgumentException("host cannot be null");
    this.host = host;
    this.port = port;
    zoomConnection = ZoomNative.connectionCreate();
  }

  protected void check() {
    if (zoomConnection == null)
      throw new IllegalStateException("Connection is closed");
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
  public ResultSet search(String query, QueryType queryType) throws ZoomException {
    if (query == null)
      throw new IllegalArgumentException("query cannot be null");
    if (queryType == null)
      throw new IllegalArgumentException("queryType cannot be null");
    check();
    MemorySegment yazQuery = null;
    if (queryType == QueryType.CQLQuery) {
      yazQuery = ZoomNative.queryCreate();
      ZoomNative.queryCql(yazQuery, query);
    } else if (queryType == QueryType.PrefixQuery) {
      yazQuery = ZoomNative.queryCreate();
      ZoomNative.queryPrefix(yazQuery, query);
    }
    return search(yazQuery);
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
      throw new IllegalArgumentException("query cannot be null");
    check();
    MemorySegment nativeQuery = query.getNativeQuery();
    return search(nativeQuery);
  }

  private ResultSet search(MemorySegment nativeQuery) throws ZoomException {
    try {
      MemorySegment yazResultSet = ZoomNative.connectionSearch(zoomConnection, nativeQuery);
      ZoomException err = ExceptionUtil.getError(zoomConnection, host, port);
      if (err != null) {
        ZoomNative.resultsetDestroy(yazResultSet);
        throw err;
      }
      ResultSet set = new ResultSet(yazResultSet, this);
      resultSets.add(set);
      return set;
    } finally {
      ZoomNative.queryDestroy(nativeQuery);
    }
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
      throw new IllegalArgumentException("query cannot be null");
    check();
    return scan(ZoomNative.connectionScan(zoomConnection, query));
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
      throw new IllegalArgumentException("query cannot be null");
    check();
    MemorySegment nativeQuery = query.getNativeQuery();
    try {
      ScanSet scanSet = scan(ZoomNative.connectionScan1(zoomConnection, nativeQuery));
      return scanSet;
    } finally {
      ZoomNative.queryDestroy(nativeQuery);
    }
  }

  private ScanSet scan(MemorySegment yazScanSet) throws ZoomException {
    ZoomException err = ExceptionUtil.getError(zoomConnection, host, port);
    if (err != null) {
      ZoomNative.scansetDestroy(yazScanSet);
      throw err;
    }
    ScanSet set = new ScanSet(yazScanSet);
    scanSets.add(set);
    return set;
  }

  /**
   * Establishes a connection to the remote server.
   * @throws ZoomException any (possibly network-level) errors that may occur
   */
  public void connect() throws ZoomException {
    check();
    ZoomNative.connectionConnect(zoomConnection, host, port);
    ZoomException err = ExceptionUtil.getError(zoomConnection, host, port);
    if (err != null) {
      throw err;
    }
  }

  /**
   * Closes the connection.
   */
  @Override
  public void close() {
    for (ResultSet set : resultSets) {
      set.close();
    }
    resultSets.clear();
    for (ScanSet set : scanSets) {
      set.close();
    }
    scanSets.clear();
    if (zoomConnection != null) {
      ZoomNative.connectionDestroy(zoomConnection);
      zoomConnection = null;
    }
  }
  
  /**
   * Return exception type from current connection
   *
   * @return null if no error
   */
  ZoomException getZoomException() {
    if (zoomConnection == null) {
      return null;
    }
    return ExceptionUtil.getError(zoomConnection, host, port);
  }

  /**
   * Write option with a given name.
   * @param name option name
   * @param value option value
   * @return connection (self) for fluent interface
   */
  public Connection option(String name, String value) {
    if (name == null)
      throw new IllegalArgumentException("option name cannot be null");
    check();
    ZoomNative.connectionOptionSet(zoomConnection, name, value);
    return this;
  }

  /**
   * Read option with a given name
   * @param name option name
   * @return option value
   */
  public String option(String name) {
    if (name == null)
      throw new IllegalArgumentException("option name cannot be null");
    check();
    return ZoomNative.connectionOptionGet(zoomConnection, name);
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

}

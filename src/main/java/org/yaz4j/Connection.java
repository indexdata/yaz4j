package org.yaz4j;

import org.yaz4j.exception.ZoomException;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_connection_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_query_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_resultset_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_scanset_p;
import org.yaz4j.jni.yaz4jlib;

public class Connection {
    private String host;
    private int port;
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

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
        zoomConnection = yaz4jlib.ZOOM_connection_create(null);
    }

    public void finalize() {
        _dispose();
    }

    public ResultSet search(String query, QueryType queryType) throws ZoomException {
      if (closed) throw new IllegalStateException("Connection is closed.");
      SWIGTYPE_p_ZOOM_query_p yazQuery = null;
      if (queryType == QueryType.CQLQuery) {
          yazQuery = yaz4jlib.ZOOM_query_create();
          yaz4jlib.ZOOM_query_cql(yazQuery, query);
      } else if (queryType == QueryType.PrefixQuery) {
          yazQuery = yaz4jlib.ZOOM_query_create();
          yaz4jlib.ZOOM_query_prefix(yazQuery, query);
      }
      SWIGTYPE_p_ZOOM_resultset_p yazResultSet = yaz4jlib.ZOOM_connection_search(zoomConnection, yazQuery);
      ZoomException err = ExceptionUtil.getError(zoomConnection, host,
        port);
      if (err != null) {
          yaz4jlib.ZOOM_resultset_destroy(yazResultSet);
          yaz4jlib.ZOOM_query_destroy(yazQuery);
          throw err;
      }
      return new ResultSet(yazResultSet, this);
    }

    public ScanSet scan(String query) throws ZoomException {
      if (closed) throw new IllegalStateException("Connection is closed.");
        SWIGTYPE_p_ZOOM_scanset_p yazScanSet = yaz4jlib.ZOOM_connection_scan(zoomConnection, query);
        ZoomException err = ExceptionUtil.getError(zoomConnection, host, port);
        if (err != null) {
            yaz4jlib.ZOOM_scanset_destroy(yazScanSet);
            throw err;
        }
        ScanSet scanSet = new ScanSet(yazScanSet, this);
        return scanSet;
    }

    /**
     * Initiates the connection
     */
    public void connect() throws ZoomException {
      yaz4jlib.ZOOM_connection_connect(zoomConnection, host, port);
      ZoomException err = ExceptionUtil.getError(zoomConnection, host, port);
      if (err != null) throw err;
      closed = false;
    }

    /**
     * Closes the connection.
     */
    public void close() {
      yaz4jlib.ZOOM_connection_close(zoomConnection);
      closed = true;
    }

    /**
     * Write option with a given name.
     * @param name option name
     * @param value option value
     * @return connection (self) for chainability
     */
    public Connection option(String name, String value) {
      yaz4jlib.ZOOM_connection_option_set(zoomConnection, name, value);
      return this;
    }

    /**
     * Read option with a given name
     * @param name option name
     * @return option value
     */
    public String option(String name) {
      return yaz4jlib.ZOOM_connection_option_get(zoomConnection, name);
    }

    public String getSyntax() {
        return option("preferredRecordSyntax");
    }

    public void setSyntax(String value) {
        option("preferredRecordSyntax", value);
    }

    public String getDatabaseName() {
        return option("databaseName");
    }

    public void setDatabaseName(String value) {
        option("databaseName", value);
    }

    public String getUsername() {
        return option("user");
    }

    public void setUsername(String value) {
        option("user", value);
    }

    public String getPassword() {
        return option("password");
    }

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

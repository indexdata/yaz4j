package org.yaz4j;

import org.yaz4j.exception.ZoomImplementationException;
import org.yaz4j.exception.ConnectionTimeoutException;
import org.yaz4j.exception.InitRejectedException;
import org.yaz4j.exception.Bib1Exception;
import org.yaz4j.exception.InvalidQueryException;
import org.yaz4j.exception.Bib1Diagnostic;
import org.yaz4j.exception.ConnectionUnavailableException;
import org.yaz4j.exception.ZoomException;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_connection_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_query_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_resultset_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_scanset_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_options_p;
import org.yaz4j.jni.yaz4jlib;
import org.yaz4j.jni.yaz4jlibConstants;

public class Connection {
    private String host;
    private int port;
    final private SWIGTYPE_p_ZOOM_options_p options;
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
        try {
            // System.err.println( "Loading library '"+ System.mapLibraryName( libName ) + "'" );
            System.loadLibrary(libName);
        } catch (AbstractMethodError e) {
            System.err.println("Fatal Error: Failed to load library '" + System.mapLibraryName(libName) + "'");
            e.printStackTrace();
        }
    }

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;
        options = yaz4jlib.ZOOM_options_create();
    }

    public void finalize() {
        _dispose();
    }

    public ResultSet search(String query, QueryType queryType) throws ZoomException {
      if (closed) throw new IllegalStateException("Connection is closed.");
        SWIGTYPE_p_ZOOM_query_p yazQuery = yaz4jlib.ZOOM_query_create();
        ResultSet resultSet = null;

        try {
            if (queryType == QueryType.CQLQuery) {
                yaz4jlib.ZOOM_query_cql(yazQuery, query);
            } else if (queryType == QueryType.PrefixQuery) {
                yaz4jlib.ZOOM_query_prefix(yazQuery, query);
            } else {
                throw new InvalidQueryException("queryType");
            }

            SWIGTYPE_p_ZOOM_resultset_p yazResultSet = yaz4jlib.ZOOM_connection_search(zoomConnection, yazQuery);

            int errorCode = yaz4jlib.ZOOM_connection_errcode(zoomConnection);
            if (errorCode != yaz4jlib.ZOOM_ERROR_NONE) {
                yaz4jlib.ZOOM_resultset_destroy(yazResultSet);
            }
            checkErrorCodeAndThrow(errorCode);

            resultSet = new ResultSet(yazResultSet, this);
        } finally {
            yaz4jlib.ZOOM_query_destroy(yazQuery); // deallocate yazQuery also when exceptions
            yazQuery = null;
        }
        return resultSet;
    }

    public ScanSet scan(String query) throws ZoomException {
      if (closed) throw new IllegalStateException("Connection is closed.");
        SWIGTYPE_p_ZOOM_scanset_p yazScanSet = yaz4jlib.ZOOM_connection_scan(zoomConnection, query);

        int errorCode = yaz4jlib.ZOOM_connection_errcode(zoomConnection);
        if (errorCode != yaz4jlib.ZOOM_ERROR_NONE) {
            yaz4jlib.ZOOM_scanset_destroy(yazScanSet);
        }
        checkErrorCodeAndThrow(errorCode);

        ScanSet scanSet = new ScanSet(yazScanSet, this);
        return scanSet;
    }

    /**
     * Initiates the connection
     */
    public void connect() throws ZoomException {
      //this is temporary before ZOOM-C has proper close method, right now
      // simply recreate connection
      if (closed)
        zoomConnection = yaz4jlib.ZOOM_connection_create(options);
      yaz4jlib.ZOOM_connection_connect(zoomConnection, host, port);
      int errorCode = yaz4jlib.ZOOM_connection_errcode(zoomConnection);
      checkErrorCodeAndThrow(errorCode);
      closed = false;
    }

    /**
     * Closes the connection.
     */
    public void close() {
      if (!closed) {
        yaz4jlib.ZOOM_connection_destroy(zoomConnection);
        zoomConnection = null;
        closed = true;
      }
    }

    private void checkErrorCodeAndThrow(int errorCode) throws ZoomException {
        String message;

        if (errorCode == yaz4jlibConstants.ZOOM_ERROR_NONE) {
            return;
        } else if (errorCode == yaz4jlib.ZOOM_ERROR_CONNECT) {
            message = String.format("Connection could not be made to %s:%d", host, port);
            throw new ConnectionUnavailableException(message);
        } else if (errorCode == yaz4jlib.ZOOM_ERROR_INVALID_QUERY) {
            message = String.format("The query requested is not valid or not supported");
            throw new InvalidQueryException(message);
        } else if (errorCode == yaz4jlib.ZOOM_ERROR_INIT) {
            message = String.format("Server %s:%d rejected our init request", host, port);
            throw new InitRejectedException(message);
        } else if (errorCode == yaz4jlib.ZOOM_ERROR_TIMEOUT) {
            message = String.format("Server %s:%d timed out handling our request", host, port);
            throw new ConnectionTimeoutException(message);
        } else if ((errorCode == yaz4jlib.ZOOM_ERROR_MEMORY) || (errorCode == yaz4jlib.ZOOM_ERROR_ENCODE) || (errorCode == yaz4jlib.ZOOM_ERROR_DECODE) || (errorCode == yaz4jlib.ZOOM_ERROR_CONNECTION_LOST) || (errorCode == yaz4jlib.ZOOM_ERROR_INTERNAL) || (errorCode == yaz4jlib.ZOOM_ERROR_UNSUPPORTED_PROTOCOL) || (errorCode == yaz4jlib.ZOOM_ERROR_UNSUPPORTED_QUERY)) {
            message = yaz4jlib.ZOOM_connection_errmsg(zoomConnection);
            throw new ZoomImplementationException("A fatal error occurred in Yaz: " + errorCode + " - " + message);
        } else {
            String errMsgBib1 = "Bib1Exception: Error Code = " + errorCode + " (" + Bib1Diagnostic.getError(errorCode) + ")";
            throw new Bib1Exception(errMsgBib1);
        }
    }

    /**
     * Write option with a given name.
     * @param name option name
     * @param value option value
     * @return connection (self) for chainability
     */
    public Connection option(String name, String value) {
      yaz4jlib.ZOOM_options_set(options, name, value);
      return this;
    }

    /**
     * Read option with a given name
     * @param name option name
     * @return option value
     */
    public String option(String name) {
      return yaz4jlib.ZOOM_options_get(options, name);
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
          close();
          disposed = true;
        }
    }
}

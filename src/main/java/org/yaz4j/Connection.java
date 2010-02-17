package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_connection_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_query_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_resultset_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_scanset_p;
import org.yaz4j.jni.SWIGTYPE_p_p_char;
import org.yaz4j.jni.yaz4jlib;
import org.yaz4j.jni.yaz4jlibConstants;

public class Connection {

    private String host;
    private int port;
    private ConnectionOptionsCollection options = null;
    protected SWIGTYPE_p_ZOOM_connection_p zoomConnection = null;
    private boolean connected = false;
    private boolean disposed = false;

    static {
        // on Linux   'yaz4j' maps to 'libyaz4j.so' (i.e. 'lib' prefix & '.so'  extension)
        // on Windows 'yaz4j' maps to 'yaz4j.dll'   (i.e.                '.dll' extension)
        String libName = "yaz4j";
        try {
            // System.err.println( "Loading library '"+ System.mapLibraryName( libName ) + "'" );
            System.loadLibrary(libName);
        } catch (Throwable e) {
            System.err.println("Fatal Error: Failed to load library '" + System.mapLibraryName(libName) + "'");
            e.printStackTrace();
        }
    }

    public Connection(String host, int port) {
        this.host = host;
        this.port = port;

        options = new ConnectionOptionsCollection();
        zoomConnection = yaz4jlib.ZOOM_connection_create(options.zoomOptions);

        //remove
        SWIGTYPE_p_p_char cp = null;
        SWIGTYPE_p_p_char addinfo = null;
        int errorCode = yaz4jlib.ZOOM_connection_error(zoomConnection, cp, addinfo);
        checkErrorCodeAndThrow(errorCode);
    }

    public void finalize() {
        dispose();
    }

    private void checkErrorCodeAndThrow(int errorCode) {
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

    private enum QueryType {

        CQLQuery, PrefixQuery
    };

    public ResultSet search(PrefixQuery query) {
        return search(query.getQueryString(), QueryType.PrefixQuery);
    }

    public ResultSet search(CQLQuery query) {
        return search(query.getQueryString(), QueryType.CQLQuery);
    }

    private ResultSet search(String query, QueryType queryType) {
        ensureConnected();

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

            resultSet = new ResultSet(yazResultSet, zoomConnection);
        } finally {
            yaz4jlib.ZOOM_query_destroy(yazQuery); // deallocate yazQuery also when exceptions
            yazQuery = null;
        }
        return resultSet;
    }

    public ScanSet scan(String query) {
        ensureConnected();
        SWIGTYPE_p_ZOOM_scanset_p yazScanSet = yaz4jlib.ZOOM_connection_scan(zoomConnection, query);

        int errorCode = yaz4jlib.ZOOM_connection_errcode(zoomConnection);
        if (errorCode != yaz4jlib.ZOOM_ERROR_NONE) {
            yaz4jlib.ZOOM_scanset_destroy(yazScanSet);
        }
        checkErrorCodeAndThrow(errorCode);

        ScanSet scanSet = new ScanSet(yazScanSet, this);
        return scanSet;
    }

    public ConnectionOptionsCollection getOptions() {
        return options;
    }

    protected void ensureConnected() {
        if (!connected) {
            connect();
        }
    }

    public void connect() {
        yaz4jlib.ZOOM_connection_connect(zoomConnection, host, port);
        int errorCode = yaz4jlib.ZOOM_connection_errcode(zoomConnection);
        checkErrorCodeAndThrow(errorCode);
        connected = true;
    }

    public void dispose() {
        if (!disposed) {
            yaz4jlib.ZOOM_connection_destroy(zoomConnection);
            zoomConnection = null;
            disposed = true;
        }
    }

    public String getSyntax() {
        return options.get("preferredRecordSyntax");
    }

    public void setSyntax(String value) {
        options.set("preferredRecordSyntax", value);
    }

    public String getDatabaseName() {
        return options.get("databaseName");
    }

    public void setDatabaseName(String value) {
        options.set("databaseName", value);
    }

    public String getUsername() {
        return options.get("user");
    }

    public void setUsername(String value) {
        options.set("user", value);
    }

    public String getPassword() {
        return options.get("password");
    }

    public void setPassword(String value) {
        options.set("password", value);
    }
}

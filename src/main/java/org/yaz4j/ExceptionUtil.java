/*
 * Copyright (c) 1995-2010, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j;

import org.yaz4j.exception.*;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_connection_p;
import org.yaz4j.jni.yaz4jlib;
import org.yaz4j.jni.yaz4jlibConstants;

/**
 * Utility class to map ZOOM errors into ZoomExceptions
 * @author jakub
 */
class ExceptionUtil {

  static ZoomException getError(SWIGTYPE_p_ZOOM_connection_p zoomConnection, String host, int port) {
    int errorCode = yaz4jlib.ZOOM_connection_errcode(zoomConnection);
    String message;
    switch (errorCode) {
      case yaz4jlibConstants.ZOOM_ERROR_NONE:
        return null;
      case yaz4jlib.ZOOM_ERROR_CONNECT:
        message = String.format("Connection could not be made to %s:%d", host,
        port);
        return new ConnectionUnavailableException(message);
      case yaz4jlib.ZOOM_ERROR_INVALID_QUERY:
        message = String.format(
        "The query requested is not valid or not supported");
        return new InvalidQueryException(message);
      case yaz4jlib.ZOOM_ERROR_INIT:
        message = String.format("Server %s:%d rejected our init request", host,
        port);
        return new InitRejectedException(message);
      case yaz4jlib.ZOOM_ERROR_TIMEOUT:
        message = String.format("Server %s:%d timed out handling our request",
        host, port);
        return new ConnectionTimeoutException(message);
      case yaz4jlib.ZOOM_ERROR_MEMORY:
      case yaz4jlib.ZOOM_ERROR_ENCODE:
      case yaz4jlib.ZOOM_ERROR_DECODE:
      case yaz4jlib.ZOOM_ERROR_CONNECTION_LOST:
      case yaz4jlib.ZOOM_ERROR_INTERNAL:
      case yaz4jlib.ZOOM_ERROR_UNSUPPORTED_PROTOCOL:
      case yaz4jlib.ZOOM_ERROR_UNSUPPORTED_QUERY:
        message = yaz4jlib.ZOOM_connection_errmsg(zoomConnection);
        return new ZoomImplementationException("A fatal error occurred in Yaz: " +
          errorCode + " - " + message);
      default:
        String errMsgBib1 = "Bib1Exception: Error Code = " + errorCode + " (" +
          Bib1Diagnostic.getError(errorCode) + ")";
        return new Bib1Exception(errMsgBib1);
    }
  }

}
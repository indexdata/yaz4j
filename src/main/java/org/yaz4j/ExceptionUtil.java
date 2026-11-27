/*
 * Copyright (c) 1995-2010, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j;

import org.yaz4j.exception.*;
import java.lang.foreign.MemorySegment;

/**
 * Utility class to map ZOOM errors into ZoomExceptions
 * @author jakub
 */
class ExceptionUtil {

  static ZoomException getError(MemorySegment zoomConnection,
    String host, int port) {

    String fHost = host + (port == 0 ? "" : ":" + port);

    int errorCode = ZoomNative.connectionErrcode(zoomConnection);
    String message;
    switch (errorCode) {
      case ZoomNative.ZOOM_ERROR_NONE:
        return null;
      case ZoomNative.ZOOM_ERROR_CONNECT:
        message = String.format("Connection could not be made to %s", fHost);
        return new ConnectionUnavailableException(message);
      case ZoomNative.ZOOM_ERROR_INVALID_QUERY:
        message = String.format(
          "The query requested is not valid or not supported");
        return new InvalidQueryException(message);
      case ZoomNative.ZOOM_ERROR_INIT:
        message = String.format("Server %s rejected our init request", fHost);
        return new InitRejectedException(message);
      case ZoomNative.ZOOM_ERROR_TIMEOUT:
        message = String.format("Server %s timed out handling our request", fHost);
        return new ConnectionTimeoutException(message);
      case ZoomNative.ZOOM_ERROR_MEMORY:
      case ZoomNative.ZOOM_ERROR_ENCODE:
      case ZoomNative.ZOOM_ERROR_DECODE:
      case ZoomNative.ZOOM_ERROR_CONNECTION_LOST:
      case ZoomNative.ZOOM_ERROR_INTERNAL:
      case ZoomNative.ZOOM_ERROR_UNSUPPORTED_PROTOCOL:
      case ZoomNative.ZOOM_ERROR_UNSUPPORTED_QUERY:
        message = ZoomNative.connectionErrmsg(zoomConnection);
        return new ZoomImplementationException("A fatal error occurred in Yaz: "
          + errorCode + " - " + message);
      default:
        String errMsgBib1 = "Bib1Exception: Error Code = " + errorCode + " (" + Bib1Diagnostic.
          getError(errorCode) + ")";
        return new Bib1Exception(errMsgBib1);
    }
  }
}

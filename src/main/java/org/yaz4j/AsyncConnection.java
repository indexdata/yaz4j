/*
 * Copyright (c) 1995-2015, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j;

import org.yaz4j.exception.ZoomException;
import org.yaz4j.jni.yaz4jlib;
import static org.yaz4j.jni.yaz4jlib.*;

/**
 *
 * @author jakub
 */
public class AsyncConnection extends Connection {
  private ResultSet lastResultSet;
  ErrorHandler eh;
  SearchHandler sh;
  RecordHandler rh;
  
  public interface SearchHandler {
    public void handle(ResultSet rs);
  }
  
  public interface RecordHandler {
    public void handle(Record r);
  }
  
  public interface ErrorHandler {
    public void handle(ZoomException e);
  }

  public AsyncConnection(String host, int port) {
    super(host, port);
    ZOOM_connection_option_set(zoomConnection, "async", "1");
    //what about piggy back?
    ZOOM_connection_option_set(zoomConnection, "count", "100");
    ZOOM_connection_option_set(zoomConnection, "step", "20");
    closed = false;
  }

  @Override
  public ResultSet search(Query query) throws ZoomException {
    lastResultSet = super.search(query);
    return null;
  }
  
  public AsyncConnection onSearch(SearchHandler sh) {
    this.sh = sh;
    return this;
  }
  
  public AsyncConnection onRecord(RecordHandler rh) {
    this.rh = rh;
    return this;
  }
  
  public AsyncConnection onError(ErrorHandler eh) {
    this.eh = eh;
    return this;
  }
  
  //actuall handler, pkg-private
  
  void handleSearch() {
    handleError();
    //handle search
    if (sh != null) sh.handle(lastResultSet);
  }
  
  void handleRecord() {
    try {
      if (rh != null) rh.handle(lastResultSet.getRecord(lastResultSet.asyncRecordOffset));
    } catch (ZoomException ex) {
      if (eh != null) eh.handle(ex);
    } finally {
      lastResultSet.asyncRecordOffset++;
    }
  }
  
  void handleError() {
    //handle error
    ZoomException err = ExceptionUtil.getError(zoomConnection, host, port);
    if (err != null) {
      if (eh != null) eh.handle(err);
    }
  }
  
}

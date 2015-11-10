/*
 * Copyright (c) 1995-2015, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j;

import org.yaz4j.exception.ZoomException;
import static org.yaz4j.jni.yaz4jlib.*;
import org.yaz4j.util.Unstable;

/**
 *
 * @author jakub
 */
@Unstable
public class AsyncConnection extends Connection {
  private ResultSet lastResultSet;
  ErrorHandler eh;
  //make sure error is only handled once
  boolean errorHandled = false;
  ErrorHandler reh;
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
    closed = false;
  }

  @Override
  public ResultSet search(Query query) throws ZoomException {
    errorHandled = false;
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
  
  public AsyncConnection onRecordError(ErrorHandler reh) {
    this.reh = reh;
    return this;
  }
  
  //actuall handler, pkg-private
  
  void handleSearch() {
    handleError();
    //handle search
    if (sh != null) sh.handle(lastResultSet);
  }
  
  void handleRecord() {
    //TODO clone the record to detach it from the result set
    try {
      if (rh != null) rh.handle(lastResultSet.getRecord(lastResultSet.asyncRecordOffset));
    } catch (ZoomException ex) {
      if (reh != null) reh.handle(ex);
    } finally {
      lastResultSet.asyncRecordOffset++;
    }
  }
  
  void handleError() {
    //handle error
    if (!errorHandled) {
      ZoomException err = ExceptionUtil.getError(zoomConnection, host, port);
      if (err != null) {
        if (eh != null) {
          eh.handle(err);
          errorHandled = true;
        }
      }
    }
  }
  
}

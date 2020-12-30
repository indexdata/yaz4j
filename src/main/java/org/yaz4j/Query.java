/*
 * Copyright (c) 1995-2013, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j;

import org.yaz4j.exception.ZoomException;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_query_p;
import org.yaz4j.jni.yaz4jlib;

/**
 * @see <a href="http://www.indexdata.com/yaz/doc/zoom.query.html">YAZ ZOOM Query</a>
 * @author jakub
 */
public abstract class Query {

  // TODO make accessor and throw exception when referred to and null (if closed)
  SWIGTYPE_p_ZOOM_query_p query;

  protected Query(String queryString) {
    if (queryString == null)
      throw new IllegalArgumentException("query string cannot be null");
    query = yaz4jlib.ZOOM_query_create();
  }
  
  public void sortBy(String strategy, String criteria) throws ZoomException {
    int ret = yaz4jlib.ZOOM_query_sortby2(query, strategy, criteria);
    if (ret != 0) {
      throw new ZoomException("query sortBy failed");
    }
  }
  
  public void close() {
    if (query != null) {
      yaz4jlib.ZOOM_query_destroy(query);
      query = null;
    }
  }
  
}

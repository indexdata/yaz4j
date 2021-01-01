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
  String queryString;
  String strategy;
  String criteria;

  protected Query(String queryString) throws ZoomException {
    if (queryString == null)
      throw new IllegalArgumentException("query string cannot be null");
    this.queryString = queryString;
    SWIGTYPE_p_ZOOM_query_p nativeQuery = getNativeQuery();
    yaz4jlib.ZOOM_query_destroy(nativeQuery);
  }
  
  public void sortBy(String strategy, String criteria) throws ZoomException {
    if (strategy == null)
      throw new IllegalArgumentException("strategy string cannot be null");
    this.strategy = strategy;
    this.criteria = criteria;
    SWIGTYPE_p_ZOOM_query_p nativeQuery = getNativeQuery();
    yaz4jlib.ZOOM_query_destroy(nativeQuery);
  }

  abstract int createQuery(SWIGTYPE_p_ZOOM_query_p nativeQuery, String queryString);

  protected SWIGTYPE_p_ZOOM_query_p getNativeQuery() throws ZoomException {
    SWIGTYPE_p_ZOOM_query_p nativeQuery = yaz4jlib.ZOOM_query_create();
    if (createQuery(nativeQuery, queryString) != 0) {
      yaz4jlib.ZOOM_query_destroy(nativeQuery);
      throw new ZoomException("invalid query " + queryString);
    }
    if (strategy != null && criteria != null) {
      int ret = yaz4jlib.ZOOM_query_sortby2(nativeQuery, strategy, criteria);
      if (ret == -1) {
        yaz4jlib.ZOOM_query_destroy(nativeQuery);
        throw new ZoomException("invalid sort strategy " + strategy);
      }
      if (ret == -2) {
        yaz4jlib.ZOOM_query_destroy(nativeQuery);
        throw new ZoomException("invalid sort criteria " + criteria);
      }
    }
    return nativeQuery;
  }
}
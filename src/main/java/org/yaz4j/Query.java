/*
 * Copyright (c) 1995-2013, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j;

import org.yaz4j.exception.ZoomException;
import java.lang.foreign.MemorySegment;

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
    MemorySegment nativeQuery = getNativeQuery();
    ZoomNative.queryDestroy(nativeQuery);
  }
  
  public void sortBy(String strategy, String criteria) throws ZoomException {
    if (strategy == null)
      throw new IllegalArgumentException("strategy string cannot be null");
    this.strategy = strategy;
    this.criteria = criteria;
    MemorySegment nativeQuery = getNativeQuery();
    ZoomNative.queryDestroy(nativeQuery);
  }

  abstract int createQuery(MemorySegment nativeQuery, String queryString);

  protected MemorySegment getNativeQuery() throws ZoomException {
    MemorySegment nativeQuery = ZoomNative.queryCreate();
    if (createQuery(nativeQuery, queryString) != 0) {
      ZoomNative.queryDestroy(nativeQuery);
      throw new ZoomException("invalid query " + queryString);
    }
    if (strategy != null && criteria != null) {
      int ret = ZoomNative.querySortby2(nativeQuery, strategy, criteria);
      if (ret == -1) {
        ZoomNative.queryDestroy(nativeQuery);
        throw new ZoomException("invalid sort strategy " + strategy);
      }
      if (ret == -2) {
        ZoomNative.queryDestroy(nativeQuery);
        throw new ZoomException("invalid sort criteria " + criteria);
      }
    }
    return nativeQuery;
  }
}
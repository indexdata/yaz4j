/*
 * Copyright (c) 1995-2013, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j;

import org.yaz4j.exception.ZoomException;
import java.lang.foreign.MemorySegment;

/**
 *
 * @author jakub
 */
public class CQLQuery extends Query {

  public CQLQuery(String cqlQuery) throws ZoomException {
    super(cqlQuery);
  }

  int createQuery(MemorySegment nativeQuery, String queryString) {
    return ZoomNative.queryCql(nativeQuery, queryString);
  }

}

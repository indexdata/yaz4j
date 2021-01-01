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
 *
 * @author jakub
 */
public class CQLQuery extends Query {

  public CQLQuery(String cqlQuery) throws ZoomException {
    super(cqlQuery);
  }

  int createQuery(SWIGTYPE_p_ZOOM_query_p nativeQuery, String queryString) {
    return yaz4jlib.ZOOM_query_cql(nativeQuery, queryString);
  }

}

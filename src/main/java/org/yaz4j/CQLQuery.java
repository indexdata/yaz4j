/*
 * Copyright (c) 1995-2013, Index Datassss
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j;

import org.yaz4j.jni.yaz4jlib;

/**
 *
 * @author jakub
 */
public class CQLQuery extends Query {

  public CQLQuery(String cqlQuery) {
    super(cqlQuery);
    yaz4jlib.ZOOM_query_cql(query, cqlQuery);
  }
  
}

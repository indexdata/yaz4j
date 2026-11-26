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
public class PrefixQuery extends Query {

  public PrefixQuery(String prefixQuery) throws ZoomException {
    super(prefixQuery);
  }

  int createQuery(MemorySegment nativeQuery, String queryString) {
    return ZoomNative.queryPrefix(nativeQuery, queryString);
  }

}

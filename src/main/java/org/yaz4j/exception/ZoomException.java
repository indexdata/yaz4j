/*
 * Copyright (c) 1995-2010, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */

package org.yaz4j.exception;

/**
 * Generic exception representing any ZOOM-C error situation.
 * @author jakub
 */
public class ZoomException extends Exception {

  public ZoomException() {
    super();
  }

  public ZoomException(String msg) {
    super(msg);
  }

}

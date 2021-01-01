package org.yaz4j;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yaz4j.exception.ZoomException;

public class QueryTest {

  @Test
  public void PrefixQueryNull() throws ZoomException {
    String msg = null;
    try {
      new PrefixQuery(null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("query string cannot be null", msg);
  }

  @Test
  public void SortByNull() throws ZoomException {
    String msg = null;
    try {
      Query q = new PrefixQuery("x");
      q.sortBy(null, null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("strategy string cannot be null", msg);
  }

  @Test
  public void PrefixQueryInvalid() {
    String msg = null;
    try {
      new PrefixQuery("@attr 1=4");
    } catch (ZoomException e) {
      msg = e.getMessage();
    }
    assertEquals("invalid query @attr 1=4", msg);
  }

  @Test
  public void CQLQueryInvalid() {
    String msg = null;
    try {
      new CQLQuery("(");
    } catch (ZoomException e) {
      msg = e.getMessage();
    }
    // CQL are sent verbatim to server, so no error here
    assertNull(msg);
  }

  @Test
  public void QuerySortByFailed1() {
    String msg = null;
    try {

      Query q = new PrefixQuery("@attr 1=4 title");
      q.sortBy("x", "1=4");
    } catch (ZoomException e) {
      msg = e.getMessage();
    }
    assertEquals("invalid sort strategy x", msg);
  }

  @Test
  public void QuerySortByFailed2() {
    String msg = null;
    try {
      Query q = new PrefixQuery("@attr 1=4 title");
      q.sortBy("z3950", "1=");
    } catch (ZoomException e) {
      msg = e.getMessage();
    }
    assertEquals("invalid sort criteria 1=", msg);
  }

}

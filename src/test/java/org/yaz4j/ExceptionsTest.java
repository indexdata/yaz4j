package org.yaz4j;

import org.junit.*;
import static org.junit.Assert.*;
import org.yaz4j.exception.*;

@SuppressWarnings("deprecation")
public class ExceptionsTest {

  @Test
  public void testNullPointers4() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ResultSet s = conn.search(new CQLQuery(null));
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (IllegalArgumentException npe) {
      assertEquals("query string cannot be null", npe.getMessage());
    }
  }
  
  @Test
  public void testNullPointers5() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ResultSet s = conn.search(new PrefixQuery(null));
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (IllegalArgumentException npe) {
      assertEquals("query string cannot be null", npe.getMessage());
    }
  }

  @Test
  public void testNullPointers15() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ResultSet s = conn.search(new PrefixQuery("@attr 1=4 water"));
      s.sort(null, null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (IllegalArgumentException npe) {
      assertEquals("sort type cannot be null", npe.getMessage());
    }
  }
  
  @Test
  public void testNullPointers16() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ResultSet s = conn.search(new PrefixQuery("@attr 1=4 water"));
      s.sort("some", null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (IllegalArgumentException npe) {
      assertEquals("sort spec cannot be null", npe.getMessage());
    }
  }
  
  @Test
  public void testNullPointers17() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ResultSet s = conn.search(new PrefixQuery("@attr 1=4 water"));
      Record r = s.getRecord(0);
      r.get(null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (IllegalArgumentException npe) {
      assertEquals("type cannot be null", npe.getMessage());
    }
  }
  
  @Test
  public void testRecordUnknownType() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ResultSet s = conn.search(new PrefixQuery("@attr 1=4 water"));
      Record r = s.getRecord(0);
      byte[] b = r.get("unknownType");
      String str = new String(b);
      assertEquals("", str);
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    }
  }
  
  @Test
  public void testNullPointers19() {
    try {
      ConnectionExtended conn = new ConnectionExtended("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      Package p = conn.getPackage(null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (IllegalArgumentException npe) {
      assertEquals("type cannot be null", npe.getMessage());
    }
  }
  
  @Test
  public void testPackageOptionValueNull() {
    try {
      ConnectionExtended conn = new ConnectionExtended("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      Package p = conn.getPackage("some");
      p.option("some", null);
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    }
  }
  
  @Test
  public void testPackageNameNull() {
    try {
      ConnectionExtended conn = new ConnectionExtended("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      Package p = conn.getPackage("some");
      p.option(null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (IllegalArgumentException npe) {
      assertEquals("option name cannot be null", npe.getMessage());
    }
  }

}

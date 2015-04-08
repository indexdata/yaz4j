package org.yaz4j;

import org.junit.*;
import static org.junit.Assert.*;
import org.yaz4j.exception.*;

@SuppressWarnings("deprecation")
public class NullPointersTest {
   
  @Test
  public void testNullPointers1() {
    try {
      Connection conn = new Connection(null, 0);
      conn.connect();
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage()); 
    }
  }
  
  @Test
  public void testNullPointers2() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ResultSet s = conn.search(null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
  @Test
  public void testNullPointers3() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ResultSet s = conn.search(null, null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
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
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
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
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
  @Test
  public void testNullPointers6() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ResultSet s = conn.search(null, Connection.QueryType.CQLQuery);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
  @Test
  public void testNullPointers7() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ResultSet s = conn.search(null, Connection.QueryType.PrefixQuery);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
   @Test
  public void testNullPointers8() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ScanSet s = conn.scan((String) null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
   @Test
  public void testNullPointers9() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ScanSet s = conn.scan((Query) null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
  @Test
  public void testNullPointers10() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ScanSet s = conn.scan(new PrefixQuery(null));
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
  @Test
  public void testNullPointers11() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      ScanSet s = conn.scan(new CQLQuery(null));
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
  @Test
  public void testNullPointers12() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      conn.option(null, null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
  @Test
  public void testNullPointers13() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      String opt = conn.option(null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
  @Test
  public void testNullPointers14() {
    try {
      Connection conn = new Connection("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      conn.option("some", null);
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      fail("NPE even though option name can be null");
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
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
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
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
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
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
  @Test
  public void testNullPointers18() {
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
    } catch (NullPointerException npe) {
      fail("NPE even though record type is not null");      
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
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
  @Test
  public void testNullPointer20() {
    try {
      ConnectionExtended conn = new ConnectionExtended("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      Package p = conn.getPackage("some");
      p.option("some", null);
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      fail("NPE even though option name can be null");
    }
  }
  
  @Test
  public void testNullPointer21() {
    try {
      ConnectionExtended conn = new ConnectionExtended("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      Package p = conn.getPackage("some");
      p.option(null);
      fail("NPE not raised");
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } catch (NullPointerException npe) {
      System.out.println("Caught expected NPE: " +npe.getMessage());
      
    }
  }
  
  @Test
  public void testNullPointer22() {
    try {
      ConnectionExtended conn = new ConnectionExtended("z3950.indexdata.dk:210/gils", 0);
      conn.setSyntax("sutrs");
      conn.connect();
      Package create = conn.getPackage("create"); //db create
      create.option("databaseName", "yaz4j");
      create.send();
      Package drop = conn.getPackage("drop");
      drop.send();
    } catch (ZoomException ze) {
      assertEquals("Bib1Exception: Error Code = 223 (EsPermissionDeniedOnEsCannotModifyOrDelete)", 
        ze.getMessage());
    } catch (NullPointerException npe) {
      fail(npe.getMessage()); 
    }
  }
  


}

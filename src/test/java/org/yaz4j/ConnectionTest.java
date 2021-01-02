package org.yaz4j;

import org.junit.*;
import static org.junit.Assert.*;
import org.yaz4j.exception.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
public class ConnectionTest {

  static Logger logger = Logger.getLogger("ConnectionTest");

  @Test
  public void testConnectionScan() {
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      assertNotNull(con);
      con.setSyntax("sutrs");
      con.connect();
      ScanSet s = con.scan(new PrefixQuery("@attr 1=4 utah"));
      assertNotNull(s);
      assertEquals(s.getSize(), 9);
      ScanTerm rec = s.get(0);
      assertNotNull(rec);
      String term = rec.getTerm();
      long occur = rec.getOccurences();
      assertEquals("utah", term);
      assertEquals(9, occur);
      // read scan entries check that they are sorted
      String p = "";
      for (ScanTerm r : s) {
        assertNotNull(r);
        String term1 = r.getTerm();
        assertTrue(p.compareTo(term1) < 0);
        p = term;
      }
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    }
  }

  @Test
  public void testConnectionSorting() {
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      assertNotNull(con);
      con.setSyntax("sutrs");
      con.connect();
      ResultSet s = con.search("@attr 1=4 utah", Connection.QueryType.PrefixQuery);
      assertNotNull(s);
      assertEquals(s.getHitCount(), 9);

      // dig title out of our SUTRS records originating from GRS-1
      Pattern MY_PATTERN = Pattern.compile("title:\\s*(.*)\\n");

      for (Record r : s) {
        assertNotNull(r);
        String content = new String(r.getContent());
        Matcher m = MY_PATTERN.matcher(content);
        assertTrue(m.find());
        String title = m.group(1);
        logger.fine("TITLE=" + title);
        assertEquals("SUTRS", r.getSyntax());
      }
      s.sort("yaz", "1=4 <i");
      logger.fine("sorted ones");
      List<Record> all = s.getRecords(0, (int) s.getHitCount());
      String p = "";
      for (Record r : all) {
        String content = new String(r.getContent());
        Matcher m = MY_PATTERN.matcher(content);
        assertTrue(m.find());
        String title = m.group(1);
        logger.fine("TITLE=" + title);
        assertTrue(p.compareTo(title) <= 0);
        p = title;
      }
      s.close();
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    }
  }

  @Test
  public void testConnectionSortBy() {
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      assertNotNull(con);
      con.setSyntax("sutrs");
      con.connect();
      Query query = new PrefixQuery("@attr 1=4 utah");
      query.sortBy("z3950", "1=4 <i");

      ResultSet s = con.search(query);
      assertNotNull(s);
      assertEquals(s.getHitCount(), 9);

      // dig title out of our SUTRS records originating from GRS-1
      Pattern MY_PATTERN = Pattern.compile("title:\\s*(.*)\\n");

      logger.fine("sorted ones");
      List<Record> all = s.getRecords(0, (int) s.getHitCount());
      String p = "";
      for (Record r : all) {
        String content = new String(r.getContent());
        Matcher m = MY_PATTERN.matcher(content);
        assertTrue(m.find());
        String title = m.group(1);
        logger.fine("TITLE=" + title);
        assertTrue(p.compareTo(title) <= 0);
        p = title;
      }
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    }
  }

  @Test
  public void recordError() {
    try (Connection con = new Connection("z3950.indexdata.com/gils", 0)) {
      assertNotNull(con);
      String failMessage = "";
      try {
        con.setSyntax("postscript"); // target does not support this syntax
        con.connect();
        ResultSet s = con.search(new PrefixQuery("@attr 1=4 utah"));
        assertNotNull(s);
        assertEquals(9, s.getHitCount());
        s.getRecord(0);
      } catch (ZoomException ze) {
        failMessage = ze.getMessage();
      }
      assertEquals("Record exception, code 238", failMessage);
    }
  }

  @Test
  public void testRecords() {
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      assertNotNull(con);
      try {
        con.setSyntax("usmarc");
        con.connect();
        ResultSet s = con.search(new PrefixQuery("@attr 1=4 utah"));
        assertNotNull(s);
        assertEquals(9, s.getHitCount());
        Record record1 = s.getRecord(0);
        assertNotNull(record1);
        Record record2 = record1.clone();
        record1.close();
        s.close();
        record2.close();
      } catch (ZoomException ze) {
        fail(ze.getMessage());
      }
    }
  }

  @Test
  public void connectionFailsConnectPort0() {
    String msg = "";
    try (Connection con = new Connection("z3950.indexdata.dk:211", 0)) {
      assertNotNull(con);
      con.option("timeout", "1");
      con.connect();
    } catch (ZoomException ze) {
      msg = ze.getMessage();
    }
    assertEquals("Server z3950.indexdata.dk:211 timed out handling our request", msg);
  }

  @Test
  public void connectionFailsConnecPort211() {
    String msg = "";
    try (Connection con = new Connection("z3950.indexdata.dk", 211)) {
      assertNotNull(con);
      con.option("timeout", "1");
      con.connect();
    } catch (ZoomException ze) {
      msg = ze.getMessage();
    }
    assertEquals("Server z3950.indexdata.dk:211 timed out handling our request", msg);
  }

  @Test
  public void connectionFailsSearchBadQuery() {
    String msg = "";
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      con.search(null, Connection.QueryType.PrefixQuery);
    } catch (IllegalArgumentException | ZoomException e) {
      msg = e.getMessage();
    }
    assertEquals("query cannot be null", msg);
  }

  @Test
  public void connectionFailsSearchBadQueryType() {
    String msg = "";
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      con.search("utah", null);
    } catch (IllegalArgumentException | ZoomException e) {
      msg = e.getMessage();
    }
    assertEquals("queryType cannot be null", msg);
  }

  @Test
  public void connectionFailsSearchBadQueryObject() {
    String msg = "";
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      con.search(null);
    } catch (IllegalArgumentException|ZoomException e) {
      msg = e.getMessage();
    }
    assertEquals("query cannot be null", msg);
  }

  @Test
  public void connectionFailsScanBadQueryString() {
    String msg = "";
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      con.scan((String) null);
    } catch (IllegalArgumentException|ZoomException e) {
      msg = e.getMessage();
    }
    assertEquals("query cannot be null", msg);
  }

  @Test
  public void connectionFailsScanBadQueryObject() {
    String msg = "";
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      con.scan((Query) null);
    } catch (IllegalArgumentException|ZoomException e) {
      msg = e.getMessage();
    }
    assertEquals("query cannot be null", msg);
  }

  @Test
  public void connectionNullHost() {
    String msg = "";
    try {
      new Connection(null, 0);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("host cannot be null", msg);
  }

  @Test
  public void connectionClosed() {
    Connection con = new Connection("z3950.indexdata.dk:210/gils", 0);
    con.close();
    String msg = "";
    try {
      con.option("timeout", "1");
    } catch (IllegalStateException e) {
      msg = e.getMessage();
    } finally {
      con.close();
    }
    assertEquals("Connection is closed", msg);
  }

  @Test
  public void connectionOptionInvalid1() {
    String msg = "";
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      con.option(null, null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("option name cannot be null", msg);
  }

  @Test
  public void connectionOptionInvalid2() {
    String msg = "";
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      con.option(null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    assertEquals("option name cannot be null", msg);
  }

  @Test
  public void connectionOptionGetSet() {
    try (Connection con = new Connection("z3950.indexdata.dk:210/gils", 0)) {
      assertNull(con.option("v1"));
      con.option("n1", "v1");
      assertEquals("v1", con.option("n1"));
      con.setPassword("pass");
      assertEquals("pass", con.getPassword());
      con.setUsername("user");
      assertEquals("user", con.getUsername());
      assertNull(con.getDatabaseName());
      con.setDatabaseName("db");
      assertEquals("db", con.getDatabaseName());
      con.setSyntax("grs-1");
      assertEquals("grs-1", con.getSyntax());
    }
  }
}

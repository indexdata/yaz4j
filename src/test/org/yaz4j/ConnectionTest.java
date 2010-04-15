package org.yaz4j;

import org.junit.*;
import static org.junit.Assert.*;
import org.yaz4j.exception.*;

public class ConnectionTest {

    @Test
    public void testConnection() {
        Connection con = new Connection("z3950.indexdata.dk:210/gils", 0);
        assertNotNull(con);
        try {
          con.setSyntax("sutrs");
          System.out.println("Open connection to z3950.indexdata.dk:210/gils...");
          con.connect();
          ResultSet s = con.search("@attr 1=4 utah", Connection.QueryType.PrefixQuery);
          System.out.println("Search for 'utah'...");
          assertNotNull(s);
          assertEquals(s.getSize(), 9);
          Record rec = s.getRecord(0);
          assertNotNull(rec);
          byte[] content = rec.getContent();
          // first SUTRS record
          assertEquals(content.length, 1940);
          assertEquals(content[0], 103);
          assertEquals(rec.getSyntax(), "SUTRS");
          assertEquals(rec.getDatabase(), "gils");
        } catch (ZoomException ze) {
          fail(ze.getMessage());
        } finally {
          con.close();
        }
    }

    @Test
    public void unsupportedSyntax() {
      System.out.println("Open connection to z3950.loc.gov:7090/voyager...");
      Connection con = new Connection("z3950.loc.gov:7090/voyager", 0);
      try {
        System.out.println("Set syntax to 'rusmarc'");
        con.setSyntax("rusmarc");
        con.connect();
        System.out.println("Search for something that exists...");
        ResultSet set = con.search("@attr 1=7 0253333490", Connection.QueryType.PrefixQuery);
        System.out.println("Result set size: " + set.getSize());
        System.out.println("Get the first record...");
        Record rec = set.getRecord(0);
        if (rec == null) {
          System.out.println("Record is null");
        } else {
          System.out.print(rec.render());
        }
      } catch (ZoomException ze) {
        //fail(ze.getMessage());
      } finally {
        con.close();
      }
    }

    @Test
    /**
     * This only works with local ztest
     */
    public void recordError() {
        Connection con = new Connection("localhost:9999", 0);
        assertNotNull(con);
        try {
          con.setSyntax("postscript");
          System.out.println("Open connection to localhost:9999...");
          con.connect();
          ResultSet s = con.search("100", Connection.QueryType.PrefixQuery);
          assertNotNull(s);
          assertEquals(s.getSize(), 100);
          Record rec = s.getRecord(0);
          fail("We should never get here and get ZoomeException instead");
        } catch (ZoomException ze) {
          // we need more specific exceptions here
          System.out.println(ze.getMessage());
        } finally {
          con.close();
        }
    }
}

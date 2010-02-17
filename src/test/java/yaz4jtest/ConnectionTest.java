package yaz4jtest;

import org.junit.*;
import static org.junit.Assert.*;
import org.yaz4j.Connection.QueryType;
import org.yaz4j.exception.ZoomException;

public class ConnectionTest {

    @Test
    public void testConnection() {
        org.yaz4j.Connection con = new org.yaz4j.Connection("z3950.indexdata.dk:210/gils", 0);
        assertNotNull(con);
        try {
          con.setSyntax("sutrs");
          System.out.println("Open connection to z3950.indexdata.dk:210/gils...");
          con.connect();
          org.yaz4j.ResultSet s = con.search("@attr 1=4 utah", QueryType.PrefixQuery);
          assertNotNull(s);
          assertEquals(s.getSize(), 9);
          org.yaz4j.Record rec = s.getRecord(0);
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
}

package org.yaz4j;

import org.junit.*;
import static org.junit.Assert.*;
import org.yaz4j.exception.*;
import java.util.List;

@SuppressWarnings("deprecation")
public class ConnectionExtendedTest {
  
  @Test
  public void testConnection() {
    ConnectionExtended con = new ConnectionExtended("z3950.indexdata.dk:210/gils", 0);
    assertNotNull(con);
    try {
      con.setSyntax("sutrs");
      System.out.println("Open connection extended to z3950.indexdata.dk:210/gils...");
      con.connect();
      ResultSet s = con.search(new PrefixQuery("@attr 1=4 utah"));
      System.out.println("Search for 'utah'...");
      assertNotNull(s);
      assertEquals(s.getHitCount(), 9);
      Record rec = s.getRecord(0);
      assertNotNull(rec);
      byte[] content = rec.getContent();
      // first SUTRS record
      assertEquals(content.length, 1940);
      assertEquals(content[0], 103);
      assertEquals(rec.getSyntax(), "SUTRS");
      assertEquals(rec.getDatabase(), "gils");
      System.out.println("Update record..");
      Package p = con.getPackage("update");
      p.option("action", "specialUpdate");
      p.option("record", rec.render());
      p.send();
    } catch (ZoomException ze) {
      assertEquals("Bib1Exception: Error Code = 223 (EsPermissionDeniedOnEsCannotModifyOrDelete)", 
        ze.getMessage());
    } finally {
      con.close();
    }
  }
  
}

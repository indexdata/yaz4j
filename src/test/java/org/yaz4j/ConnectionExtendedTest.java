package org.yaz4j;

import org.junit.*;
import static org.junit.Assert.*;
import org.yaz4j.exception.*;
import java.util.List;

@SuppressWarnings("deprecation")
public class ConnectionExtendedTest {
  
  @Test
  public void testRecordUpdate() {
    ConnectionExtended con = new ConnectionExtended("z3950.indexdata.com:210/gils", 0);
    assertNotNull(con);
    try {
      con.setSyntax("sutrs");
      con.connect();
      ResultSet s = con.search(new PrefixQuery("@attr 1=4 utah"));
      assertNotNull(s);
      assertEquals(s.getHitCount(), 9);
      Record rec = s.getRecord(0);
      assertNotNull(rec);
      assertEquals(rec.getSyntax(), "SUTRS");
      assertEquals(rec.getDatabase(), "gils");
      Package p = con.getPackage("update");
      p.option("action", "specialUpdate");
      p.option("record", rec.render());
      p.send();
      s.close();
    } catch (ZoomException ze) {
      assertEquals("Bib1Exception: Error Code = 223 (EsPermissionDeniedOnEsCannotModifyOrDelete)", 
        ze.getMessage());
    } finally {
      con.close();
    }
  }

  @Test
  public void testDatabaseDrop() {
    try {
      ConnectionExtended conn = new ConnectionExtended("z3950.indexdata.com:210/gils", 0);
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
    }
  }
}

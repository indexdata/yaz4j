package org.yaz4j;

import org.junit.*;
import static org.junit.Assert.*;
import org.yaz4j.exception.ZoomException;

/**
 * @author adam
 */
@SuppressWarnings("deprecation")
public class DinosaurTest {

  @Test
  public void test() {
    Connection con = new Connection("lx2.loc.gov/LCDB_MARC8", 0);
    try {
      assertNotNull(con);
      con.setSyntax("usmarc");
      con.connect();
      ResultSet set = con.search("@attr 1=7 0253333490",
        Connection.QueryType.PrefixQuery);
      assertNotNull(set);
      Record rec = set.getRecord(0);
      assertNotNull(rec);
    } catch (ZoomException ze) {
      fail(ze.getMessage());
    } finally {
      con.close();
    }
  }
}

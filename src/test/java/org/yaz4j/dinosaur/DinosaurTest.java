package org.yaz4j.dinosaur;

import org.junit.*;
import org.yaz4j.Connection;
import org.yaz4j.PrefixQuery;
import org.yaz4j.Record;
import org.yaz4j.ResultSet;
import org.yaz4j.exception.ZoomException;

public class DinosaurTest {
  @Test
  public void test() {
    try (Connection con = new Connection("lx2.loc.gov/LCDB", 0)) {
      con.setSyntax("usmarc");
      con.connect();
      ResultSet set = con.search(new PrefixQuery("@attr 1=7 0253333490"));
      Record rec = set.getRecord(0);
      System.out.println(rec.render());
    } catch (ZoomException ze) {
      Assert.fail(ze.getMessage());
    }
  }
}

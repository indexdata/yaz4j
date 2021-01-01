package org.yaz4j;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.yaz4j.exception.ZoomException;


public class RecordTest {
  Connection con;

  @Before
  public void before() {
    con = new Connection("z3950.indexdata.com/gils", 0);
  }

  @After
  public void after() {
    con.close();
  }

  @Test
  public void recordsClosing() throws ZoomException {
    con.connect();
    con.setSyntax("sutrs");
    ResultSet search = con.search(new PrefixQuery("@attr 1=4 utah"));
    Record record = search.getRecord(0);
    record.close();
    String msg = null;
    try {
      record.getContent();
    } catch (IllegalStateException e) {
      msg = e.getMessage();
    }
    Assert.assertEquals("record is closed", msg);
    record = search.getRecord(0);
    Record record2 = record.clone();
    record.close();
    Assert.assertEquals("SUTRS", record2.getSyntax());
    Assert.assertEquals("gils", record2.getDatabase());
    Assert.assertEquals("SUTRS", new String(record2.get("syntax")));
    Assert.assertEquals(record2.render(), new String(record2.get("render")));

    msg = null;
    try {
      record2.get(null);
    } catch (IllegalArgumentException e) {
      msg = e.getMessage();
    }
    Assert.assertEquals("type cannot be null", msg);

    record2.close();
    msg = null;
    try {
      record2.getContent();
    } catch (IllegalStateException e) {
      msg = e.getMessage();
    }
    Assert.assertEquals("record is closed", msg);
  }
}

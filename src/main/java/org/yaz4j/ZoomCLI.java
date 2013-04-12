/*
 * Copyright (c) 1995-2013, Index Datassss
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j;

import java.util.List;
import org.yaz4j.exception.ZoomException;

/**
 *
 * @author jakub
 */
class ZoomCLI {
  static public void main(String[] args) {
    System.out.println("Open connection to z3950.indexdata.dk:210/gils...");
    Connection con = new Connection("z3950.indexdata.dk:210/gils", 0);
    try {
      con.setSyntax("sutrs");
      con.connect();
      System.out.println("Search for 'utah'...");
      ResultSet s = con.search(new PrefixQuery("@attr 1=4 utah"));
      System.out.println("Retrieve all records..");
      List<Record> all = s.getRecords(0, (int) s.getHitCount());
      for (Record r : all) {
        System.out.println(r.render());
      }
      System.out.println("Success");
    } catch (ZoomException ze) {
      System.out.println("Failure");
      ze.printStackTrace();
    } finally {
      con.close();
    }
  }
}

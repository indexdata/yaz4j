/*
 * Copyright (c) 1995-2013, Index Data
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
    System.out.println("ARGS=" + args.length);
    if (args.length < 2) {
      System.out.println("Usage: <target> <query>");
      System.out.println("For example: z3950.indexdata.com/gils \"attr 1=4 utah\"");
      System.exit(1);
    }
    Connection con = new Connection(args[0], 0);
    try {
      con.setSyntax("sutrs");
      con.connect();
      ResultSet s = con.search(new PrefixQuery(args[1]));
      List<Record> all = s.getRecords(0, (int) s.getHitCount());
      for (Record r : all) {
        System.out.println(r.render());
      }
    } catch (ZoomException ze) {
      System.out.println("Failure");
      ze.printStackTrace();
    } finally {
      con.close();
    }
  }
}

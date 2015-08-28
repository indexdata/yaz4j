/*
 * Copyright (c) 1995-2015, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.yaz4j.exception.ZoomException;

import static java.lang.System.out;

/**
 *
 * @author jakub
 */
public class AsyncConnectionsTest {
  
  class Box<T> {
    T item;

    public Box() {
    }

    public Box(T item) {
      this.item = item;
    }
    
    T getItem() {
      return item;
    }
    
    void setItem(T item) {
      this.item = item;
    }
  } 
  
  public AsyncConnectionsTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test async ZOOM operation.
   */
  @Test
  public void testSingleTarget() {
    out.println("Trying async connection...");
    AsyncConnection conn = new AsyncConnection("z3950.indexdata.dk:210/gils", 0);
    AsyncConnections conns = new AsyncConnections();
    conns.add(conn);
    int expectedHitCount = 9;
    final Box<Long> actualHitCount = new Box<Long>();
    final Box<Integer> actualRecordCounter =  new Box<Integer>(0);
    try {
      conn.setSyntax("sutrs");
      conn.connect();
      conn.search(new PrefixQuery("@attr 1=4 utah"));
      conn
        .onSearch(new AsyncConnection.SearchHandler() {
        public void handle(ResultSet rs) {
          out.println("Received search, hit count "+rs.getHitCount());
          actualHitCount.setItem(rs.getHitCount());
        }
      })
        .onRecord(new AsyncConnection.RecordHandler() {
        public void handle(Record r) {
          out.println("Received a record of type "+r.getSyntax());
          actualRecordCounter.setItem(actualRecordCounter.getItem()+1);
        }
      });
      
    } catch (ZoomException ex) {
      fail(ex.getMessage());
    }
    conns.start();
    assertEquals(expectedHitCount, actualHitCount.item);
    assertEquals(expectedHitCount, actualRecordCounter.item);
    
  }
  
  
  /**
   * Test async ZOOM operation.
   */
  @Test
  public void testMulitTarget() {
    out.println("Trying async with multile connections...");
    AsyncConnections conns = new AsyncConnections();
    AsyncConnection conn = new AsyncConnection("z3950.indexdata.dk:210/gils", 0);
    conns.add(conn);
    AsyncConnection conn2 = new AsyncConnection("z3950.indexdata.dk:210/marc", 0);
    conns.add(conn2);
    int expectedHitCount = 19; //for both
    final Box<Long> actualHitCount = new Box<Long>(0L);
    final Box<Integer> actualRecordCounter =  new Box<Integer>(0);
    try {
      //we need to simplify the API for multiple
      conn.setSyntax("sutrs");
      conn.connect();
      conn.search(new PrefixQuery("@attr 1=4 utah"));
      conn
        .onSearch(new AsyncConnection.SearchHandler() {
        public void handle(ResultSet rs) {
          out.println("Received search, hit count "+rs.getHitCount());
          actualHitCount.setItem(actualHitCount.getItem() + rs.getHitCount());
        }
      })
        .onRecord(new AsyncConnection.RecordHandler() {
        public void handle(Record r) {
          out.println("Received a record of type "+r.getSyntax());
          actualRecordCounter.setItem(actualRecordCounter.getItem()+1);
        }
      });
      conn2.setSyntax("marc21");
      conn2.connect();
      conn2.search(new PrefixQuery("@attr 1=4 computer"));
      conn2
        .onSearch(new AsyncConnection.SearchHandler() {
        public void handle(ResultSet rs) {
          out.println("Received search, hit count "+rs.getHitCount());
          actualHitCount.setItem(actualHitCount.getItem() + rs.getHitCount());
        }
      })
        .onRecord(new AsyncConnection.RecordHandler() {
        public void handle(Record r) {
          out.println("Received a record of type "+r.getSyntax());
          actualRecordCounter.setItem(actualRecordCounter.getItem()+1);
        }
      })
        .onError(new AsyncConnection.ErrorHandler() {

        public void handle(ZoomException e) {
          out.println("Caught error: "+e.getMessage());
        }
      });
      
    } catch (ZoomException ex) {
      fail(ex.getMessage());
    }
    conns.start();
    assertEquals(expectedHitCount, actualHitCount.item);
    assertEquals(expectedHitCount, actualRecordCounter.item);
    
  }
}

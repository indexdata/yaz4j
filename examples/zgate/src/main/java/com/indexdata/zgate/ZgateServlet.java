/*
 * Copyright (c) 1995-2010, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package com.indexdata.zgate;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.yaz4j.Connection;
import org.yaz4j.Record;
import org.yaz4j.ResultSet;

import org.apache.commons.pool.KeyedObjectPool;
import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;


/**
 *
 * @author jakub
 */
public class ZgateServlet extends HttpServlet {

  private static class ConnKey {
    private String host;
    private int port;
    private String dbname;
    
    public ConnKey(String host, int port, String dbname) {
      this.host = host;
      this.port = port;
      this.dbname = dbname;
    }

    public static ConnKey fromZurl(String zurl) {
      int colPos = zurl.lastIndexOf(":");
      int slashPos = zurl.lastIndexOf("/");
      String host = zurl.substring(0, colPos);
      int port = Integer.parseInt(zurl.substring(colPos+1, slashPos));
      String dbname = zurl.substring(slashPos+1);
      return new ConnKey(host, port, dbname);
    }

    public String getHost() {
      return host;
    }

    public int getPort() {
      return port;
    }

    public String getDbname() {
      return dbname;
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof ConnKey)) return false;
      ConnKey other = (ConnKey) obj;
      return (host.equals(other.host) && port == other.port
        && dbname.equals(other.dbname));
    }

    @Override
    public int hashCode() {
      int hash = 7;
      hash = 59 * hash + (this.host != null ? this.host.hashCode() : 0);
      hash = 59 * hash + this.port;
      hash = 59 * hash + (this.dbname != null ? this.dbname.hashCode() : 0);
      return hash;
    }

    @Override
    public String toString() {
      return host + ":" + port + "/" + dbname;
    }    
  }

  private class ConnectionMaker extends BaseKeyedPoolableObjectFactory {
    @Override
    public Object makeObject(Object key) throws Exception {
      ConnKey ck = (ConnKey) key;
      return new Connection(ck.toString(), 0);
    }

    @Override
    public void activateObject(Object key, Object obj) throws Exception {
      ((Connection) obj).connect();
    }


    @Override
    public void destroyObject(Object key, Object obj) throws Exception {
      ((Connection) obj).close();
    }
  }

  private KeyedObjectPool pool = new GenericKeyedObjectPool(new ConnectionMaker());


  @Override
  public void init() throws ServletException {
    System.out.println("Zeta: java.library.path=" + System.getProperty("java.library.path"));
    System.out.println("Zeta: LD_LIBRARY_PATH=" + System.getenv("LD_LIBRARY_PATH"));
  }

  @Override
  /*
   * For dinosaur search use: ?zurl=z3950.loc.gov:7090/voyager&query=@attr 1=7 0253333490&syntax=usmarc
   **/
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    String zurl = request.getParameter("zurl");
    if (zurl == null || zurl.isEmpty()) { response.sendError(400, "Missing parameter 'zurl'"); return; }

    String query = request.getParameter("query");
    if (query == null || query.isEmpty()) { response.sendError(400, "Missing parameter 'query'"); return; }

    String syntax = request.getParameter("syntax");
    if (syntax == null || syntax.isEmpty()) { response.sendError(400, "Missing parameter 'syntax'"); return; }

    int maxrecs=10;
    if (request.getParameter("maxrecs") != null && !request.getParameter("maxrecs").isEmpty()) {
      try {
        maxrecs = Integer.parseInt(request.getParameter("maxrecs"));
      } catch (NumberFormatException nfe) {
        response.sendError(400, "Malformed parameter 'maxrecs'");
        return;
      }
    }

    response.getWriter().println("SEARCH PARAMETERS");
    response.getWriter().println("zurl: " + zurl);
    response.getWriter().println("query: " + query);
    response.getWriter().println("syntax: " + syntax);
    response.getWriter().println("maxrecs: " + maxrecs);
    response.getWriter().println();

    ConnKey ckey = null;
    Connection con = null;
    try {
      ckey = ConnKey.fromZurl(zurl);
      System.out.println("Connection key is" + ckey);
      con = (Connection) pool.borrowObject(ckey);
      con.setSyntax(syntax);
      ResultSet set = con.search(query, Connection.QueryType.PrefixQuery);
      response.getWriter().println("Showing " + maxrecs + " of " +set.getSize());
      response.getWriter().println();
      for(int i=0; i<set.getSize() && i<maxrecs; i++) {
        Record rec = set.getRecord(i);
        response.getWriter().print(rec.render());
      }
    } catch (Exception e) {
      throw new ServletException(e);
    } finally {
      try {
        if (ckey != null && con != null)
          pool.returnObject(ckey, con);
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
  }

  @Override
  public String getServletInfo() {
    return "Zeta search engine servlet";
  }
}

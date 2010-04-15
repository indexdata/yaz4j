/*
 * Copyright (c) 1995-2010, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package com.indexdata.zgate;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.yaz4j.Connection;
import org.yaz4j.Record;
import org.yaz4j.ResultSet;
import org.yaz4j.exception.ZoomException;

/**
 *
 * @author jakub
 */
public class ZgateServlet extends HttpServlet {

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

    Connection con = new Connection(zurl, 0);
    con.setSyntax(syntax);
    try {
      con.connect();
      ResultSet set = con.search(query, Connection.QueryType.PrefixQuery);
      response.getWriter().println("Showing " + maxrecs + " of " +set.getHitCount());
      response.getWriter().println();
      for(int i=0; i<set.getHitCount() && i<maxrecs; i++) {
        Record rec = set.getRecord(i);
        if (rec == null) continue;
        response.getWriter().print(rec.render());
      }
    } catch (ZoomException ze) {
      throw new ServletException(ze);
    } finally {
      con.close();
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

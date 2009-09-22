package org.yaz4j;

public class PrefixQuery {

    private String query = null;

    public PrefixQuery(String query) {
        this.query = query;
    }

    public String getQueryString() {
        return query;
    }

    public void setQueryString(String query) {
        this.query = query;
    }
}

package org.yaz4j;

public class CQLQuery
{
	private String query = null;
	
	public CQLQuery(String query)
	{
		this.query = query;
	}

	public String getQueryString()
	{
		return query ;
	}
	
	public void setQueryString( String query)
	{
		this.query = query ;
	}
}

package org.yaz4j;

public class ScanTerm
{
	private String term;
	private long occurences;
	
	ScanTerm( String term, long occurences )
	{
		this.term = term;
		this.occurences = occurences;
	}

	public String getTerm()
	{
		return term;
	}

	public long getOccurences()
	{
		return occurences;
	}
}

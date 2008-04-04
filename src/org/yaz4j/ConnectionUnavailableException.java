package org.yaz4j;

public class ConnectionUnavailableException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ConnectionUnavailableException()
	{
		super();
	}
	
	public ConnectionUnavailableException(String message )
	{
		super( message );
	}
}

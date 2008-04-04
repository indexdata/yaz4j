package org.yaz4j;

public class ConnectionTimeoutException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ConnectionTimeoutException()
	{
		super();
	}
	
	public ConnectionTimeoutException(String message )
	{
		super( message );
	}
}

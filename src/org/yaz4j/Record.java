package org.yaz4j;

import java.io.UnsupportedEncodingException;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_record_p;
import org.yaz4j.jni.SWIGTYPE_p_int;
import org.yaz4j.jni.yaz4jlib;

public class Record
{
	private SWIGTYPE_p_ZOOM_record_p record = null ;
	private ResultSet resultSet = null ;
	private boolean disposed = false;	

	Record(SWIGTYPE_p_ZOOM_record_p record, ResultSet resultSet)
	{
		this.resultSet = resultSet;
		this.record = record;
	}

	public void finalize()
	{
		Dispose();
	}
	
	public byte[] getContent()
	{
		String type = "raw";
		SWIGTYPE_p_int length = null ;	
		return yaz4jlib.ZOOM_record_get_bytes(record, type, length) ;
//		String contentString = yaz4jlib.ZOOM_record_get(record, type, length) ;
//		System.err.println("!!!!!");
//		System.err.println(contentString);
//		System.err.println(contentString.length());
//		System.err.println("!!!!!");
//		try {
//			byte[] bytes = contentString.getBytes("UTF8");
//			System.err.println(bytes.length);
//			return bytes ;
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException(e);
//		}
	}

	public String getSyntax()
	{
		String type = "syntax";
		SWIGTYPE_p_int length = null ;		
		String syntax = yaz4jlib.ZOOM_record_get(record, type, length);
		return syntax ;
	}
	
	public String getDatabase()
	{
		String type = "database";
		SWIGTYPE_p_int length = null ;		
		String database = yaz4jlib.ZOOM_record_get(record, type, length);
	
		return database ;
	}

	public void Dispose()
	{
		if (!disposed)
		{
			resultSet = null;
			record = null;
			disposed = true;
		}
	}
}

package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_connection_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_options_p;
import org.yaz4j.jni.yaz4jlib;

public class ConnectionOptionsCollection
{
	SWIGTYPE_p_ZOOM_options_p zoomOptions = null ;

	ConnectionOptionsCollection()
	{
		zoomOptions = yaz4jlib.ZOOM_options_create();
	}

	public void finalize()
	{
		Dispose();
	}
	
	public void Dispose()
	{
		yaz4jlib.ZOOM_options_destroy( zoomOptions );
		zoomOptions = null ;
	}
	
	SWIGTYPE_p_ZOOM_connection_p CreateConnection()
	{
		return yaz4jlib.ZOOM_connection_create(zoomOptions);
	}
	
	public String get(String key)
	{
		return yaz4jlib.ZOOM_options_get(zoomOptions, key) ;
	}
	
	public void set(String key, String value)
	{
		yaz4jlib.ZOOM_options_set(zoomOptions, key, value) ;
	}
	
}

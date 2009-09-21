package org.yaz4j;

import org.yaz4j.jni.SWIGTYPE_p_ZOOM_connection_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_record_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_resultset_p;
import org.yaz4j.jni.yaz4jlib;

public class ResultSet
{
	private SWIGTYPE_p_ZOOM_resultset_p resultSet;	
	private SWIGTYPE_p_ZOOM_connection_p connection;
	private long size = 0 ;
	private Record[] records = null ;
	private boolean disposed = false;	
	
	ResultSet(SWIGTYPE_p_ZOOM_resultset_p resultSet, SWIGTYPE_p_ZOOM_connection_p connection)
	{
		this.resultSet = resultSet ;
		this.connection = connection ;
		size = yaz4jlib.ZOOM_resultset_size(this.resultSet);
		records = new Record[(int)size];
	}
	
	public void finalize()
	{
		this.Dispose();		
	}	

    ResultSetOptionsCollection getResultSetOptions()
    {
    	return new ResultSetOptionsCollection(resultSet);
    }
	
	public Record getRecord(int index)
	{
		if ( records[index] == null)
		{
			SWIGTYPE_p_ZOOM_record_p recordTemp = yaz4jlib.ZOOM_resultset_record(resultSet, index);
			records[index] = new Record(recordTemp, this);
		}
	
		return this.records[index];
	}
	
	public int getSize()
	{
		return (int)size ;
	}
	
	public void Dispose()
	{
		if (! disposed )
		{
			for( int i=0 ; i<records.length ; i++)
			{
				if (records[i] != null)
					records[i].Dispose();
			}

			yaz4jlib.ZOOM_resultset_destroy(resultSet);
			connection = null;
			resultSet = null;
			disposed = true;
		}
	}
}

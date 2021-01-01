package org.yaz4j;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import org.yaz4j.exception.ZoomException;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_record_p;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_resultset_p;
import org.yaz4j.jni.SWIGTYPE_p_p_ZOOM_record_p;
import org.yaz4j.jni.yaz4jlib;

/**
 * This class represents a "buffered handle" to the result set created on the
 * server and thus retrieving records may involve a request to the server if
 * those records are not locally cached. Details on how to configure the retrieval
 * (present) process are available in the YAZ manual
 *
 * @see <a href="http://www.indexdata.com/yaz/doc/zoom.resultsets.html">YAZ ZOOM result sets</a>
 *
 * Because of server misbehavior or errors during retrieval the
 * "getRecord" method may either return null or throw exceptions, even when the
 * index of retrieved records lies within the bounds of the set. Client
 * code should be prepared for such situations.
 *
 * This class implements the iterable interface and as such can be used within
 * foreach loops, it's important to note, however, that in this case the errors
 * during retrieval will be masked with standard NoSuchElementExceptions.
 *
 * @author jakub
 */
public class ResultSet implements Closeable, Iterable<Record> {

  private Connection conn;
  private SWIGTYPE_p_ZOOM_resultset_p resultSet;
  private long size = 0;
  private List<Record> records = new LinkedList<>(); // all records returned by this set

  ResultSet(SWIGTYPE_p_ZOOM_resultset_p resultSet, Connection conn) {
    this.resultSet = resultSet;
    size = yaz4jlib.ZOOM_resultset_size(this.resultSet);
    this.conn = conn;
  }

  /**
   * Read option by name.
   * @param name option name
   * @return option value
   */
  public String option(String name) {
    check();
    return yaz4jlib.ZOOM_resultset_option_get(resultSet, name);
  }

  /**
   * Write option with a given name.
   * @param name option name
   * @param value option value
   * @return result set (self) for chainability
   */
  public ResultSet option(String name, String value) {
    if (name == null)
      throw new IllegalArgumentException("option name cannot be null");
    check();
    yaz4jlib.ZOOM_resultset_option_set(resultSet, name, value);
    return this;
  }

  public Record getRecord(long index) throws ZoomException {
    check();
    SWIGTYPE_p_ZOOM_record_p nativeRecord =
      yaz4jlib.ZOOM_resultset_record(resultSet, index);
    //may be out of range or unsupported syntax
    if (nativeRecord == null) {
      return null;
    }
    int errorCode = yaz4jlib.ZOOM_record_error(nativeRecord, null, null, null);
    if (errorCode != 0) {
      throw new ZoomException("Record exception, code " + errorCode);
    }
    Record record = new Record(nativeRecord);
    records.add(record);
    return record;
  }
  
  /**
   * Retrieve a collection of records at once. If a record cannot be retrieved,
   * it is omitted from the list (thus the list size may be smaller than 'count').
   * @param start start index within the result set
   * @param count number of records to retrieve
   * @return
   * @throws ZoomException raised in case of protocol errors
   */
  public List<Record> getRecords(long start, int count) throws ZoomException {
    check();
    List<Record> out = new ArrayList<Record>(count);
    SWIGTYPE_p_p_ZOOM_record_p recs = yaz4jlib.new_zoomRecordArray(count);
    yaz4jlib.ZOOM_resultset_records(resultSet, recs, start, count);
    ZoomException err = conn.getZoomException();
    if (err != null) { 
      throw err;
    }
    for (int i = 0; i < count; i++) {
      SWIGTYPE_p_ZOOM_record_p nativeRecord =
        yaz4jlib.zoomRecordArray_getitem(recs, i);
      if (nativeRecord == null) {
        continue;
      }
      int errorCode = yaz4jlib.ZOOM_record_error(nativeRecord, null, null, null);
      if (errorCode != 0) {
        throw new ZoomException("Record exception, code " + errorCode);
      }
      Record record = new Record(nativeRecord);
      records.add(record);
      out.add(record);
    }
    return out;
  }

  @Override
  public Iterator<Record> iterator() {
    return new Iterator<Record>() {
      private long cur;
      @Override
      public boolean hasNext() {
        return cur < size;
      }

      @Override
      public Record next() {
        try {
          return getRecord(cur++);
        } catch (ZoomException ze) {
          throw new NoSuchElementException(ze.getMessage());
        }
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("remove operation not supported");
      }
    };
  }

  /**
   * 
   * @param type
   * @param spec
   * @return
   * @throws ZoomException 
   */
  public ResultSet sort(String type, String spec) throws ZoomException {
    if (type == null)
      throw new IllegalArgumentException("sort type cannot be null");
    if (spec == null)
      throw new IllegalArgumentException("sort spec cannot be null");
    check();
    int ret = yaz4jlib.ZOOM_resultset_sort1(resultSet, type, spec);
    if (ret != 0) throw new ZoomException("Sorting resultset failed");
    return this;
  }

  public long getHitCount() {
    check();
    return size;
  }

  protected void check() {
    if (resultSet == null) {
      throw new IllegalStateException("resultSet is closed");
    }
  }

  @Override
  public void close() {
    for (Record record : records) {
      record.close();
    }
    records.clear();
    if (resultSet != null) {
      yaz4jlib.ZOOM_resultset_destroy(resultSet);
      resultSet = null;
      conn = null;
    }
  }
}

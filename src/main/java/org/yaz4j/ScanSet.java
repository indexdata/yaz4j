package org.yaz4j;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.yaz4j.exception.ZoomException;
import org.yaz4j.jni.SWIGTYPE_p_ZOOM_scanset_p;
import org.yaz4j.jni.SWIGTYPE_p_size_t;
import org.yaz4j.jni.yaz4jlib;

public class ScanSet implements Iterable<ScanTerm> {

  private SWIGTYPE_p_ZOOM_scanset_p scanSet;
  private long size = 0;

  ScanSet(SWIGTYPE_p_ZOOM_scanset_p scanSet) {
    this.scanSet = scanSet;
    size = yaz4jlib.ZOOM_scanset_size(scanSet);
  }

  public ScanTerm get(long index) {
    if (scanSet == null)
      throw new IllegalStateException("ScanSet is closed");
    SWIGTYPE_p_size_t occ = yaz4jlib.new_size_tp();
    SWIGTYPE_p_size_t length = yaz4jlib.new_size_tp();
    String term = yaz4jlib.ZOOM_scanset_term(scanSet, (long) index, occ, length);
    long occurences = yaz4jlib.size_tp_value(occ);
    yaz4jlib.delete_size_tp(occ);
    yaz4jlib.delete_size_tp(length);
    return new ScanTerm(term, occurences);
  }

  public long getSize() {
    return size;
  }

  public void close() {
    if (scanSet != null) {
      yaz4jlib.ZOOM_scanset_destroy(scanSet);
      scanSet = null;
    }
  }

  @Override
  public Iterator<ScanTerm> iterator() {
    return new Iterator<ScanTerm>() {
      private long cur;
      @Override
      public boolean hasNext() {
        return cur < size;
      }

      @Override
      public ScanTerm next() {
        return get(cur++);
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException("remove operation not supported");
      }
    };
  }
}

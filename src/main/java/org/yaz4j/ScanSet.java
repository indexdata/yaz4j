package org.yaz4j;

import java.io.Closeable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.yaz4j.exception.ZoomException;
import java.lang.foreign.MemorySegment;

public class ScanSet implements Closeable, Iterable<ScanTerm> {

  private MemorySegment scanSet;
  private long size = 0;

  ScanSet(MemorySegment scanSet) {
    this.scanSet = scanSet;
    size = ZoomNative.scansetSize(scanSet);
  }

  public ScanTerm get(long index) {
    check();
    long[] occ = new long[1];
    String term = ZoomNative.scansetTerm(scanSet, index, occ);
    long occurences = occ[0];
    return new ScanTerm(term, occurences);
  }

  public long getSize() {
    return size;
  }

  protected void check() {
    if (scanSet == null) {
      throw new IllegalStateException("scanSet is closed");
    }
  }

  @Override
  public void close() {
    if (scanSet != null) {
      ZoomNative.scansetDestroy(scanSet);
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

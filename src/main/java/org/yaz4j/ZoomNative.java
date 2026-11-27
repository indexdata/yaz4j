/*
 * Copyright (c) 1995-2026, Index Data
 * All rights reserved.
 * See the file LICENSE for details.
 */
package org.yaz4j;

import static java.lang.foreign.ValueLayout.ADDRESS;
import static java.lang.foreign.ValueLayout.JAVA_BYTE;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

import java.io.File;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Bindings for the YAZ ZOOM C API using the Java Foreign Function and Memory
 * API. ZOOM handles (connections, result sets, records, ...) are represented
 * as zero-length {@link MemorySegment}s wrapping the native pointer.
 *
 * <p>The YAZ shared library is located by trying, in order, the path or name
 * given by the {@code yaz4j.library} system property, or the platform's usual
 * YAZ library names in each {@code java.library.path} directory, a few common
 * install locations, and finally the system's dynamic linker search path.
 */
final class ZoomNative {

  static final int ZOOM_ERROR_NONE = 0;
  static final int ZOOM_ERROR_CONNECT = 10000;
  static final int ZOOM_ERROR_MEMORY = 10001;
  static final int ZOOM_ERROR_ENCODE = 10002;
  static final int ZOOM_ERROR_DECODE = 10003;
  static final int ZOOM_ERROR_CONNECTION_LOST = 10004;
  static final int ZOOM_ERROR_INIT = 10005;
  static final int ZOOM_ERROR_INTERNAL = 10006;
  static final int ZOOM_ERROR_TIMEOUT = 10007;
  static final int ZOOM_ERROR_UNSUPPORTED_PROTOCOL = 10008;
  static final int ZOOM_ERROR_UNSUPPORTED_QUERY = 10009;
  static final int ZOOM_ERROR_INVALID_QUERY = 10010;

  private static final Linker LINKER = Linker.nativeLinker();
  private static final SymbolLookup YAZ = loadYaz();

  static {
    MemoryLayout sizeT = LINKER.canonicalLayouts().get("size_t");
    if (sizeT.byteSize() != JAVA_LONG.byteSize()) {
      throw new UnsupportedOperationException(
        "yaz4j requires a 64-bit platform (size_t is " + sizeT.byteSize() + " bytes)");
    }
  }

  private static final MethodHandle CONNECTION_CREATE = handle("ZOOM_connection_create",
    FunctionDescriptor.of(ADDRESS, ADDRESS));
  private static final MethodHandle CONNECTION_CONNECT = handle("ZOOM_connection_connect",
    FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, JAVA_INT));
  private static final MethodHandle CONNECTION_DESTROY = handle("ZOOM_connection_destroy",
    FunctionDescriptor.ofVoid(ADDRESS));
  private static final MethodHandle CONNECTION_OPTION_GET = handle("ZOOM_connection_option_get",
    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));
  private static final MethodHandle CONNECTION_OPTION_SET = handle("ZOOM_connection_option_set",
    FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, ADDRESS));
  private static final MethodHandle CONNECTION_ERRCODE = handle("ZOOM_connection_errcode",
    FunctionDescriptor.of(JAVA_INT, ADDRESS));
  private static final MethodHandle CONNECTION_ERRMSG = handle("ZOOM_connection_errmsg",
    FunctionDescriptor.of(ADDRESS, ADDRESS));
  private static final MethodHandle CONNECTION_SEARCH = handle("ZOOM_connection_search",
    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));
  private static final MethodHandle CONNECTION_SCAN = handle("ZOOM_connection_scan",
    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));
  private static final MethodHandle CONNECTION_SCAN1 = handle("ZOOM_connection_scan1",
    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));
  private static final MethodHandle CONNECTION_PACKAGE = handle("ZOOM_connection_package",
    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));

  private static final MethodHandle RESULTSET_DESTROY = handle("ZOOM_resultset_destroy",
    FunctionDescriptor.ofVoid(ADDRESS));
  private static final MethodHandle RESULTSET_OPTION_GET = handle("ZOOM_resultset_option_get",
    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));
  private static final MethodHandle RESULTSET_OPTION_SET = handle("ZOOM_resultset_option_set",
    FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, ADDRESS));
  private static final MethodHandle RESULTSET_SIZE = handle("ZOOM_resultset_size",
    FunctionDescriptor.of(JAVA_LONG, ADDRESS));
  private static final MethodHandle RESULTSET_RECORDS = handle("ZOOM_resultset_records",
    FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, JAVA_LONG, JAVA_LONG));
  private static final MethodHandle RESULTSET_RECORD = handle("ZOOM_resultset_record",
    FunctionDescriptor.of(ADDRESS, ADDRESS, JAVA_LONG));
  private static final MethodHandle RESULTSET_SORT1 = handle("ZOOM_resultset_sort1",
    FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, ADDRESS));

  private static final MethodHandle RECORD_GET = handle("ZOOM_record_get",
    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS, ADDRESS));
  private static final MethodHandle RECORD_DESTROY = handle("ZOOM_record_destroy",
    FunctionDescriptor.ofVoid(ADDRESS));
  private static final MethodHandle RECORD_CLONE = handle("ZOOM_record_clone",
    FunctionDescriptor.of(ADDRESS, ADDRESS));
  private static final MethodHandle RECORD_ERROR = handle("ZOOM_record_error",
    FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, ADDRESS, ADDRESS));

  private static final MethodHandle QUERY_CREATE = handle("ZOOM_query_create",
    FunctionDescriptor.of(ADDRESS));
  private static final MethodHandle QUERY_DESTROY = handle("ZOOM_query_destroy",
    FunctionDescriptor.ofVoid(ADDRESS));
  private static final MethodHandle QUERY_CQL = handle("ZOOM_query_cql",
    FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS));
  private static final MethodHandle QUERY_PREFIX = handle("ZOOM_query_prefix",
    FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS));
  private static final MethodHandle QUERY_SORTBY2 = handle("ZOOM_query_sortby2",
    FunctionDescriptor.of(JAVA_INT, ADDRESS, ADDRESS, ADDRESS));

  private static final MethodHandle SCANSET_TERM = handle("ZOOM_scanset_term",
    FunctionDescriptor.of(ADDRESS, ADDRESS, JAVA_LONG, ADDRESS, ADDRESS));
  private static final MethodHandle SCANSET_SIZE = handle("ZOOM_scanset_size",
    FunctionDescriptor.of(JAVA_LONG, ADDRESS));
  private static final MethodHandle SCANSET_DESTROY = handle("ZOOM_scanset_destroy",
    FunctionDescriptor.ofVoid(ADDRESS));

  private static final MethodHandle PACKAGE_DESTROY = handle("ZOOM_package_destroy",
    FunctionDescriptor.ofVoid(ADDRESS));
  private static final MethodHandle PACKAGE_SEND = handle("ZOOM_package_send",
    FunctionDescriptor.ofVoid(ADDRESS, ADDRESS));
  private static final MethodHandle PACKAGE_OPTION_GET = handle("ZOOM_package_option_get",
    FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS));
  private static final MethodHandle PACKAGE_OPTION_SET = handle("ZOOM_package_option_set",
    FunctionDescriptor.ofVoid(ADDRESS, ADDRESS, ADDRESS));

  private static final MethodHandle OPTIONS_CREATE = handle("ZOOM_options_create",
    FunctionDescriptor.of(ADDRESS));

  private ZoomNative() {
  }

  // ---------------------------------------------------------------- loading

  private static SymbolLookup loadYaz() {
    List<String> tried = new ArrayList<>();
    String override = System.getProperty("yaz4j.library");
    List<String> names = override != null ? List.of(override) : defaultLibraryNames();
    for (String name : names) {
      if (name.contains("/") || name.contains(File.separator)) {
        SymbolLookup lookup = tryPath(Path.of(name), tried);
        if (lookup != null)
          return lookup;
        continue;
      }
      for (String dir : searchDirs()) {
        Path file = Path.of(dir, name);
        if (Files.isRegularFile(file)) {
          SymbolLookup lookup = tryPath(file, tried);
          if (lookup != null)
            return lookup;
        }
      }
      // fall back to the dynamic linker's own search path
      try {
        return SymbolLookup.libraryLookup(name, Arena.global());
      } catch (IllegalArgumentException e) {
        tried.add(name);
      }
    }
    throw new UnsatisfiedLinkError("Unable to load the YAZ library (tried " + tried
      + "). Install YAZ or set -Dyaz4j.library=/path/to/libyaz");
  }

  private static SymbolLookup tryPath(Path path, List<String> tried) {
    try {
      if (isWindows())
        loadWithDependencies(path);
      return SymbolLookup.libraryLookup(path, Arena.global());
    } catch (IllegalArgumentException e) {
      tried.add(path.toString());
      return null;
    }
  }

  private static boolean isWindows() {
    return System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows");
  }

  private static final int LOAD_WITH_ALTERED_SEARCH_PATH = 0x00000008;

  /**
   * Windows does not look in the directory of a DLL loaded by path for the
   * DLLs it depends on, such as libxml2.dll and iconv-2.dll next to
   * yaz5.dll. Load the DLL with LoadLibraryExW and
   * LOAD_WITH_ALTERED_SEARCH_PATH, which does. The libraryLookup that follows
   * then reuses the already loaded module, and reports any failure.
   */
  private static void loadWithDependencies(Path path) {
    SymbolLookup kernel32 = SymbolLookup.libraryLookup("kernel32", Arena.global());
    MethodHandle loadLibraryExW = LINKER.downcallHandle(
      kernel32.find("LoadLibraryExW").orElseThrow(),
      FunctionDescriptor.of(ADDRESS, ADDRESS, ADDRESS, JAVA_INT));
    try (Arena arena = Arena.ofConfined()) {
      MemorySegment file = arena.allocateFrom(path.toAbsolutePath().toString(),
        StandardCharsets.UTF_16LE);
      MemorySegment ignored = (MemorySegment) loadLibraryExW.invokeExact(file,
        MemorySegment.NULL, LOAD_WITH_ALTERED_SEARCH_PATH);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  private static List<String> defaultLibraryNames() {
    String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
    if (os.startsWith("windows"))
      return List.of("yaz5.dll", "yaz.dll");
    if (os.startsWith("mac"))
      return List.of("libyaz.5.dylib", "libyaz.dylib");
    return List.of("libyaz.so.5", "libyaz.so");
  }

  private static List<String> searchDirs() {
    List<String> dirs = new ArrayList<>();
    String libPath = System.getProperty("java.library.path", "");
    for (String dir : libPath.split(File.pathSeparator)) {
      if (!dir.isEmpty())
        dirs.add(dir);
    }
    String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
    if (os.startsWith("mac")) {
      // Homebrew locations are not on the default dlopen search path
      dirs.add("/opt/homebrew/lib");
      dirs.add("/usr/local/lib");
    } else if (os.startsWith("windows")) {
      dirs.add("C:\\Program Files\\YAZ\\bin");
    }
    return dirs;
  }

  private static MethodHandle handle(String name, FunctionDescriptor descriptor) {
    MemorySegment symbol = YAZ.find(name).orElseThrow(
      () -> new UnsatisfiedLinkError("YAZ library does not export " + name));
    return LINKER.downcallHandle(symbol, descriptor);
  }

  // ---------------------------------------------------------------- helpers

  static boolean isNull(MemorySegment p) {
    return p == null || p.address() == 0;
  }

  private static MemorySegment cstr(Arena arena, String s) {
    return s == null ? MemorySegment.NULL : arena.allocateFrom(s);
  }

  private static String jstr(MemorySegment p) {
    return isNull(p) ? null : p.reinterpret(Long.MAX_VALUE).getString(0);
  }

  private static RuntimeException rethrow(Throwable t) {
    if (t instanceof RuntimeException)
      return (RuntimeException) t;
    if (t instanceof Error)
      throw (Error) t;
    return new IllegalStateException(t);
  }

  // ---------------------------------------------------------------- connection

  static MemorySegment connectionCreate() {
    try {
      return (MemorySegment) CONNECTION_CREATE.invokeExact(MemorySegment.NULL);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static void connectionConnect(MemorySegment c, String host, int port) {
    try (Arena arena = Arena.ofConfined()) {
      CONNECTION_CONNECT.invokeExact(c, cstr(arena, host), port);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static void connectionDestroy(MemorySegment c) {
    try {
      CONNECTION_DESTROY.invokeExact(c);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static String connectionOptionGet(MemorySegment c, String key) {
    try (Arena arena = Arena.ofConfined()) {
      return jstr((MemorySegment) CONNECTION_OPTION_GET.invokeExact(c, cstr(arena, key)));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static void connectionOptionSet(MemorySegment c, String key, String value) {
    try (Arena arena = Arena.ofConfined()) {
      CONNECTION_OPTION_SET.invokeExact(c, cstr(arena, key), cstr(arena, value));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static int connectionErrcode(MemorySegment c) {
    try {
      return (int) CONNECTION_ERRCODE.invokeExact(c);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static String connectionErrmsg(MemorySegment c) {
    try {
      return jstr((MemorySegment) CONNECTION_ERRMSG.invokeExact(c));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static MemorySegment connectionSearch(MemorySegment c, MemorySegment query) {
    try {
      return (MemorySegment) CONNECTION_SEARCH.invokeExact(c, query);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static MemorySegment connectionScan(MemorySegment c, String startTerm) {
    try (Arena arena = Arena.ofConfined()) {
      return (MemorySegment) CONNECTION_SCAN.invokeExact(c, cstr(arena, startTerm));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static MemorySegment connectionScan1(MemorySegment c, MemorySegment query) {
    try {
      return (MemorySegment) CONNECTION_SCAN1.invokeExact(c, query);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static MemorySegment connectionPackage(MemorySegment c, MemorySegment options) {
    try {
      return (MemorySegment) CONNECTION_PACKAGE.invokeExact(c, options);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  // ---------------------------------------------------------------- result set

  static void resultsetDestroy(MemorySegment r) {
    try {
      RESULTSET_DESTROY.invokeExact(r);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static String resultsetOptionGet(MemorySegment r, String key) {
    try (Arena arena = Arena.ofConfined()) {
      return jstr((MemorySegment) RESULTSET_OPTION_GET.invokeExact(r, cstr(arena, key)));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static void resultsetOptionSet(MemorySegment r, String key, String value) {
    try (Arena arena = Arena.ofConfined()) {
      RESULTSET_OPTION_SET.invokeExact(r, cstr(arena, key), cstr(arena, value));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static long resultsetSize(MemorySegment r) {
    try {
      return (long) RESULTSET_SIZE.invokeExact(r);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  /**
   * Retrieves {@code count} records starting at {@code start}. Entries for
   * records that could not be retrieved are null segments.
   */
  static MemorySegment[] resultsetRecords(MemorySegment r, long start, int count) {
    try (Arena arena = Arena.ofConfined()) {
      MemorySegment recs = arena.allocate(ADDRESS, count);
      RESULTSET_RECORDS.invokeExact(r, recs, start, (long) count);
      MemorySegment[] out = new MemorySegment[count];
      for (int i = 0; i < count; i++) {
        out[i] = recs.getAtIndex(ADDRESS, i);
      }
      return out;
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static MemorySegment resultsetRecord(MemorySegment r, long pos) {
    try {
      return (MemorySegment) RESULTSET_RECORD.invokeExact(r, pos);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static int resultsetSort1(MemorySegment r, String type, String spec) {
    try (Arena arena = Arena.ofConfined()) {
      return (int) RESULTSET_SORT1.invokeExact(r, cstr(arena, type), cstr(arena, spec));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  // ---------------------------------------------------------------- record

  /**
   * Returns the record in the requested form, or an empty array if YAZ
   * returns no data or a nonpositive length (as it does for "ext").
   */
  static byte[] recordGet(MemorySegment rec, String type) {
    try (Arena arena = Arena.ofConfined()) {
      MemorySegment len = arena.allocate(JAVA_INT);
      MemorySegment data = (MemorySegment) RECORD_GET.invokeExact(rec, cstr(arena, type), len);
      int length = len.get(JAVA_INT, 0);
      if (isNull(data) || length <= 0)
        return new byte[0];
      return data.reinterpret(length).toArray(JAVA_BYTE);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static void recordDestroy(MemorySegment rec) {
    try {
      RECORD_DESTROY.invokeExact(rec);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static MemorySegment recordClone(MemorySegment rec) {
    try {
      return (MemorySegment) RECORD_CLONE.invokeExact(rec);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static int recordError(MemorySegment rec) {
    try {
      return (int) RECORD_ERROR.invokeExact(rec, MemorySegment.NULL, MemorySegment.NULL,
        MemorySegment.NULL);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  // ---------------------------------------------------------------- query

  static MemorySegment queryCreate() {
    try {
      return (MemorySegment) QUERY_CREATE.invokeExact();
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static void queryDestroy(MemorySegment q) {
    try {
      QUERY_DESTROY.invokeExact(q);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static int queryCql(MemorySegment q, String str) {
    try (Arena arena = Arena.ofConfined()) {
      return (int) QUERY_CQL.invokeExact(q, cstr(arena, str));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static int queryPrefix(MemorySegment q, String str) {
    try (Arena arena = Arena.ofConfined()) {
      return (int) QUERY_PREFIX.invokeExact(q, cstr(arena, str));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static int querySortby2(MemorySegment q, String strategy, String criteria) {
    try (Arena arena = Arena.ofConfined()) {
      return (int) QUERY_SORTBY2.invokeExact(q, cstr(arena, strategy), cstr(arena, criteria));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  // ---------------------------------------------------------------- scan set

  /** Returns the term at {@code pos} and stores its occurrence count in {@code occ[0]}. */
  static String scansetTerm(MemorySegment scan, long pos, long[] occ) {
    try (Arena arena = Arena.ofConfined()) {
      MemorySegment occSeg = arena.allocate(JAVA_LONG);
      MemorySegment lenSeg = arena.allocate(JAVA_LONG);
      MemorySegment term = (MemorySegment) SCANSET_TERM.invokeExact(scan, pos, occSeg, lenSeg);
      occ[0] = occSeg.get(JAVA_LONG, 0);
      return jstr(term);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static long scansetSize(MemorySegment scan) {
    try {
      return (long) SCANSET_SIZE.invokeExact(scan);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static void scansetDestroy(MemorySegment scan) {
    try {
      SCANSET_DESTROY.invokeExact(scan);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  // ---------------------------------------------------------------- package

  static void packageDestroy(MemorySegment p) {
    try {
      PACKAGE_DESTROY.invokeExact(p);
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static void packageSend(MemorySegment p, String type) {
    try (Arena arena = Arena.ofConfined()) {
      PACKAGE_SEND.invokeExact(p, cstr(arena, type));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static String packageOptionGet(MemorySegment p, String key) {
    try (Arena arena = Arena.ofConfined()) {
      return jstr((MemorySegment) PACKAGE_OPTION_GET.invokeExact(p, cstr(arena, key)));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static void packageOptionSet(MemorySegment p, String key, String value) {
    try (Arena arena = Arena.ofConfined()) {
      PACKAGE_OPTION_SET.invokeExact(p, cstr(arena, key), cstr(arena, value));
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }

  static MemorySegment optionsCreate() {
    try {
      return (MemorySegment) OPTIONS_CREATE.invokeExact();
    } catch (Throwable t) {
      throw rethrow(t);
    }
  }
}

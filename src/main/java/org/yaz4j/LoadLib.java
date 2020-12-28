package org.yaz4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class LoadLib {

  static Logger logger = Logger.getLogger("LoadLib");

  public static void load(String name) throws IOException {
    try {
      System.loadLibrary(name);
    } catch (UnsatisfiedLinkError e1) {
      String osArch = System.getProperty("os.arch");
      String osName = System.getProperty("os.name");

      String fname = "native/" + osName + "/" + osArch + "/" + System.mapLibraryName(name);
      logger.fine("Reading " + fname);
      InputStream in = LoadLib.class.getClassLoader().getResourceAsStream(fname);
      if (in == null) {
        logger.warning("Cannot find " + fname);
        return;
      }
      File file = File.createTempFile("xxx", System.mapLibraryName(name));
      file.deleteOnExit();
      OutputStream out = new FileOutputStream(file);

      byte [] buf = new byte[16384];
      int cnt;
      while ((cnt = in.read(buf)) > 0) {
        out.write(buf, 0, cnt);
      }
      in.close();
      out.close();
      logger.fine("Loading " + file.getAbsolutePath());
      System.load(file.getAbsolutePath());
    }
  }
}

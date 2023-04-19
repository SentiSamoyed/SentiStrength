package uk.ac.wlv.utilities;

import lombok.extern.log4j.Log4j2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

@Log4j2
public class FileOps {
  public static boolean backupFileAndDeleteOriginal(String sFileName, int iMaxBackups) {
    int iLastBackup;
    File f;
    for (iLastBackup = iMaxBackups; iLastBackup >= 0; --iLastBackup) {
      f = new File(sFileName + iLastBackup + ".bak");
      if (f.exists()) {
        break;
      }
    }

    if (iLastBackup < 1) {
      f = new File(sFileName);
      if (f.exists()) {
        f.renameTo(new File(sFileName + "1.bak"));
        return true;
      } else {
        return false;
      }
    } else {
      if (iLastBackup == iMaxBackups) {
        f = new File(sFileName + iLastBackup + ".bak");
        f.delete();
        --iLastBackup;
      }

      for (int i = iLastBackup; i > 0; --i) {
        f = new File(sFileName + i + ".bak");
        f.renameTo(new File(sFileName + (i + 1) + ".bak"));
      }

      f = new File(sFileName);
      f.renameTo(new File(sFileName + "1.bak"));
      return true;
    }
  }

  public static int i_CountLinesInTextFile(String sFileLocation) {
    try (Stream<String> s = getFileStream(sFileLocation, false)) {
      return (int) s.count();
    } catch (IOException e) {
      log.error(e.getLocalizedMessage());
      return -1;
    }
  }

  public static Stream<String> getFileStream(String filename, boolean forceUtf8) throws IOException {
    BufferedReader br = new BufferedReader(
        forceUtf8 ?
            new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8)
            : new FileReader(filename));
    return br.lines();
  }

  public static String getNextAvailableFilename(String sFileNameStart, String sFileNameEnd) {
    for (int i = 0; i <= 1000; ++i) {
      String sFileName = sFileNameStart + i + sFileNameEnd;
      File f = new File(sFileName);
      if (!f.isFile()) {
        return sFileName;
      }
    }

    return "";
  }

  public static String s_ChopFileNameExtension(String sFilename) {
    if (sFilename != null && sFilename != "") {
      int iLastDotPos = sFilename.lastIndexOf(".");
      if (iLastDotPos > 0) {
        sFilename = sFilename.substring(0, iLastDotPos);
      }
    }

    return sFilename;
  }
}

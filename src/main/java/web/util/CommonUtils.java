package web.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class CommonUtils {

  public static String timeParser(Long timestamp) {
    if (Objects.isNull(timestamp)) {
      return "尚未分析";
    }
    return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).format(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME
    );
  }
}

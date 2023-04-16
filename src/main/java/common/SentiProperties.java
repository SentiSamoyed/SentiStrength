package common;

import lombok.extern.log4j.Log4j2;

import java.util.Objects;

/**
 * @author tanziyue
 * @date 2023/4/14
 * @description SentiStrength properties
 */
@Log4j2
public class SentiProperties {

  public static final String
      SERVER_MODE = "server-mode" // 是否开启服务端模式
      ;

  public static void setProperty(String key, String value) {
    System.setProperty(key, value);
  }

  public static void setProperty(String key, boolean value) {
    setProperty(key, String.valueOf(value));
  }

  public static String getProperty(String key) {
    return System.getProperty(key);
  }

  public static boolean getBooleanProperty(String key) {
    String s = getProperty(key);
    if (Objects.nonNull(s)) {
      return Boolean.parseBoolean(s);
    } else {
      return false;
    }
  }
}

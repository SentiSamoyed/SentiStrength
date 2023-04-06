package common;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * @author tanziyue
 * @date 2023/4/6
 * @description SentiStrength 运行需要数据的存放路径
 */
@Log4j2
public class SentiData {
  /**
   * SentiData 文件夹路径
   */
  public static String SENTI_DATA_DIR_PATH;

  static {
    try {
      SENTI_DATA_DIR_PATH = new ClassPathResource("SentStrength_Data").getFile().getAbsolutePath();
    } catch (IOException e) {
      log.fatal("获取 SentiData 路径失败：" + e.getLocalizedMessage());
      throw new RuntimeException(e);
    }
  }
}

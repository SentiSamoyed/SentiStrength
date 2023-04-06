package common;

import lombok.extern.log4j.Log4j2;

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
  public static String SENTI_DATA_DIR_PATH = System.getProperty("user.dir") + "/src/SentiStrength_Data/";
}

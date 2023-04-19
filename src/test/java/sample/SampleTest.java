package sample;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author tanziyue
 * @date 2023/3/28
 * @description 一个测试样例类，展示了测试支持的基本功能
 */
@Log4j2
public class SampleTest {
  @Test
  @Disabled
  void sampleTest() {
    String text = "并发不能靠直觉";
    // 日志可以用 log4j 输出
    log.info(text);
    log.warn(text);
    log.debug(text);
  }
}

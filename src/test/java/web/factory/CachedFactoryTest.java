package web.factory;

import common.SentiProperties;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import uk.ac.wlv.sentistrength.SentiStrength;
import web.data.vo.AnalysisOptionsVO;
import web.enums.AnalysisModeEnum;
import web.factory.impl.CachedSentiStrengthFactory;
import web.util.TestTimer;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author tanziyue
 * @date 2023/4/16
 * @description 与 {@link CachedSentiStrengthFactory} 相关的测试
 */
@SpringBootTest
@Log4j2
public class CachedFactoryTest {
  @Qualifier("CachedSentiStrengthFactory")
  @Autowired
  SentiStrengthFactory cachedFactory;
  @Qualifier("SimpleSentiStrengthFactory")
  @Autowired
  SentiStrengthFactory simpleFactory;

  /**
   * 性能测试
   */
  @Test
  void perfTest() {
    String fmt = "Cached / Simple = %.2f%n";

    /* 不启用 Resource 缓存时 */
    System.out.println("==== Without resource cache ====");
    SentiProperties.setProperty(SentiProperties.SERVER_MODE, false);
    long tSimple = TestTimer.run("Simple", () -> concurrentTest(simpleFactory));
    long tCached = TestTimer.run("Cached", () -> concurrentTest(cachedFactory));
    System.out.printf(fmt, (double) tCached / tSimple);

    /* 启用 Resource 缓存时，先预热一次 */
    SentiProperties.setProperty(SentiProperties.SERVER_MODE, true);
    Assertions.assertNotNull(simpleFactory.build());
    System.out.println("==== With resource cache ====");
    tSimple = TestTimer.run("Simple", () -> concurrentTest(simpleFactory));
    tCached = TestTimer.run("Cached", () -> concurrentTest(cachedFactory));
    System.out.printf(fmt, (double) tCached / tSimple);
  }

  void concurrentTest(SentiStrengthFactory factory) {
    try {
      ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
      List<Future<SentiStrength>> futures = new LinkedList<>();
      for (var mode : AnalysisModeEnum.values()) {
        for (var explain : new Boolean[]{true, false}) {
          for (var f : AnalysisOptionsVO.class.getDeclaredFields()) {
            if (Boolean.class.isAssignableFrom(f.getType())) {
              var options = new AnalysisOptionsVO();
              f.setAccessible(true);
              f.set(options, true);

              futures.add(executor.submit(
                  () -> factory.build(mode, explain, options)
              ));
            }
          }
        }
      }

      for (var f : futures) {
        f.get();
      }
    } catch (ExecutionException | InterruptedException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}

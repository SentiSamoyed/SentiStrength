package uk.ac.wlv.sentistrength.factory;

import common.SentiProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.ac.wlv.sentistrength.classification.ClassificationOptions;
import uk.ac.wlv.sentistrength.classification.ClassificationResources;
import uk.ac.wlv.sentistrength.classification.resource.Resource;
import uk.ac.wlv.sentistrength.classification.resource.factory.CachedResourceFactory;
import web.util.TestTimer;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author tanziyue
 * @date 2023/4/14
 * @description 测试 {@link uk.ac.wlv.sentistrength.classification.resource.factory.CachedResourceFactory} 的正确性
 */
public class CachedFactoryTest {


  @BeforeEach
  void setUp() {
    SentiProperties.setProperty(SentiProperties.SERVER_MODE, true);
  }

  @AfterEach
  void tearDown() {
    SentiProperties.setProperty(SentiProperties.SERVER_MODE, false);
    CachedResourceFactory.getInstance().clear();
  }

  /**
   * 测试在 option 变动时有生成新的资源对象
   */
  @Test
  void testChanges() {
    ClassificationOptions options = new ClassificationOptions();
    ClassificationResources resources;
    options.bgCorrectSpellingsUsingDictionary = true;

    resources = new ClassificationResources();
    resources.initialise(options);
    Resource correctSpelling = resources.correctSpellings;

    resources = new ClassificationResources();
    resources.initialise(options);
    Assertions.assertEquals(correctSpelling, resources.correctSpellings);

    options.bgCorrectSpellingsUsingDictionary = false;
    resources = new ClassificationResources();
    resources.initialise(options);
    Assertions.assertNotEquals(correctSpelling, resources.correctSpellings);
  }

  /**
   * 测试并发情况下 {@link uk.ac.wlv.sentistrength.classification.resource.factory.CachedResourceFactory} 的正确性
   */
  @Test
  void testConcurrency() throws InterruptedException, ExecutionException {
    ClassificationOptions options = new ClassificationOptions();

    ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    runConcur(16, options, executor);
  }

  private static void runConcur(int n, ClassificationOptions options, ThreadPoolExecutor executor) {
    try {
      var futures = IntStream.range(0, n)
          .mapToObj(i -> (Runnable) () -> {
            ClassificationResources r = new ClassificationResources();
            r.initialise(options);
          })
          .map(executor::submit)
          .toList();

      for (var f : futures) {
        f.get();
      }
    } catch (InterruptedException | ExecutionException e) {
      Assertions.fail(e.getLocalizedMessage());
    }
  }

  /**
   * 对工厂方法的性能 profiling
   */
  @Test
  void perfTest() {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    ClassificationOptions options = new ClassificationOptions();

    final int n = 32;

    CachedResourceFactory.getInstance().clear();
    System.out.printf("== Scene 1: Processing %d requests concurrently%n", n);

    SentiProperties.setProperty(SentiProperties.SERVER_MODE, false);
    var delta1 = TestTimer.run("Simple", () -> runConcur(n, options, executor));

    SentiProperties.setProperty(SentiProperties.SERVER_MODE, true);
    var delta2 = TestTimer.run("Cached", () -> runConcur(n, options, executor));

    System.out.printf("> Cached / Simple = %.2f%n", (double) delta2 / delta1);

    CachedResourceFactory.getInstance().clear();


    System.out.printf("== Scene 2: Run exactly once%n");

    final int n1 = 10; // 各跑 10 次
    SentiProperties.setProperty(SentiProperties.SERVER_MODE, false);
    delta1 = TestTimer.run("Simple", () -> {
      for (int i = 0; i < n1; i++) {
        new ClassificationResources().initialise(options);
      }
    });

    // 用魔法先把单例删了，模拟重建的情况
    SentiProperties.setProperty(SentiProperties.SERVER_MODE, true);
    delta2 = TestTimer.run("Cached", () -> {
      for (int i = 0; i < n1; i++) {
        Field f;
        try {
          f = CachedResourceFactory.class.getDeclaredField("instance");
          f.setAccessible(true);
          f.set(null, null);
          new ClassificationResources().initialise(options);
        } catch (NoSuchFieldException | IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    });

    System.out.printf("> Cached / Simple = %.2f%n", (double) delta2 / delta1);
  }
}

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

import java.time.Duration;
import java.time.Instant;
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

  private static void runConcur(int n, ClassificationOptions options, ThreadPoolExecutor executor) throws InterruptedException, ExecutionException {
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
  }

  /**
   * 对工厂方法的性能 profiling
   */
  @Test
  void timeProfiling() throws ExecutionException, InterruptedException, NoSuchFieldException, IllegalAccessException {
    ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>());
    ClassificationOptions options = new ClassificationOptions();

    int n = 32;

    CachedResourceFactory.getInstance().clear();
    System.out.printf("== Scene 1: Processing %d requests concurrently%n", n);

    SentiProperties.setProperty(SentiProperties.SERVER_MODE, false);
    var start = Instant.now();
    runConcur(n, options, executor);
    var delta1 = Duration.between(start, Instant.now());
    System.out.printf("> Simple: %d ms%n", delta1.toMillis());

    SentiProperties.setProperty(SentiProperties.SERVER_MODE, true);
    start = Instant.now();
    runConcur(n, options, executor);
    var delta2 = Duration.between(start, Instant.now());
    System.out.printf("> Cached: %d ms%n", delta2.toMillis());

    System.out.printf("> Cached / Simple = %.2f%n", (double) delta2.toMillis() / delta1.toMillis());

    CachedResourceFactory.getInstance().clear();
    System.out.printf("== Scene 2: Run exactly once%n");

    n = 10; // 各跑 10 次
    SentiProperties.setProperty(SentiProperties.SERVER_MODE, false);
    start = Instant.now();
    for (int i = 0; i < n; i++) {
      new ClassificationResources().initialise(options);
    }
    delta1 = Duration.between(start, Instant.now());
    System.out.printf("> Simple: %d ms%n", delta1.toMillis());

    // 用魔法先把单例删了，模拟重建的情况
    SentiProperties.setProperty(SentiProperties.SERVER_MODE, true);
    start = Instant.now();
    for (int i = 0; i < n; i++) {
      var f = CachedResourceFactory.class.getDeclaredField("instance");
      f.setAccessible(true);
      f.set(null, null);
      new ClassificationResources().initialise(options);
    }
    delta2 = Duration.between(start, Instant.now());
    System.out.printf("> Cached: %d ms%n", delta2.toMillis());

    System.out.printf("> Cached / Simple = %.2f%n", (double) delta2.toMillis() / delta1.toMillis());
  }
}

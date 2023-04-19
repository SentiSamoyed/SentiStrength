package web.util;

import java.time.Duration;
import java.time.Instant;

public class TestTimer {
  public static long run(String prompt, Runnable runnable) {
    var start = Instant.now();
    runnable.run();
    var delta = Duration.between(start, Instant.now());

    long time = delta.toMillis();
    System.out.printf("> %s: %d ms%n", prompt, time);

    return time;
  }
}

package uk.ac.wlv.sentistrength;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import web.factory.SentiStrengthFactory;
import web.factory.impl.SimpleSentiStrengthFactory;

import java.io.*;

/**
 * @author tanziyue
 * @date 2023/4/10
 * @description 测试 SentiStrength 的各类执行方法
 */
public class ExecutionTest {

  SentiStrengthFactory sentiStrengthFactory;

  public ExecutionTest() {
    sentiStrengthFactory = new SimpleSentiStrengthFactory();
  }

  @Test
  void testCmdInput() {
    String testCase = """
        i love your dog
        @end
        """;

    InputStream stdin = System.in;
    PrintStream stdout = System.out;
    try (PipedInputStream is = new PipedInputStream();
         PipedOutputStream os = new PipedOutputStream(is)) {
      System.setIn(new ByteArrayInputStream(testCase.getBytes()));
      System.setOut(new PrintStream(os));

      SentiStrength sentiStrength = new SentiStrength();
      sentiStrength.initialiseAndRun(new String[]{"cmd"});

      String outcome = new BufferedReader(new InputStreamReader(is)).readLine();
      Assertions.assertNotNull(outcome);
      Assertions.assertFalse(outcome.isEmpty());
      Assertions.assertEquals(2, outcome.split(" ").length);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      System.setIn(stdin);
      System.setOut(stdout);
    }
  }
}

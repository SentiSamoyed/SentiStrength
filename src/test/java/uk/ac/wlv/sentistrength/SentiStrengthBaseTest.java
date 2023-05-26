package uk.ac.wlv.sentistrength;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import web.entity.vo.AnalysisOptionsVO;
import web.enums.AnalysisModeEnum;
import web.factory.SentiStrengthFactory;
import web.factory.impl.SimpleSentiStrengthFactory;

/**
 * @author tanziyue
 * @date 2023/3/28
 * @description SentiStrength 总体功能的测试
 */
public class SentiStrengthBaseTest {

  SentiStrengthFactory sentiStrengthFactory;

  public SentiStrengthBaseTest() {
    sentiStrengthFactory = new SimpleSentiStrengthFactory();
  }

  /**
   * 检验不同模式之间输出有区别
   */
  @Test
  void testDifferentModes() {
    final String text = "I missy miss missy mii!!";
    int val1 = 2, val2 = -2;
    int[] val3 = new int[]{1, -1, 0};
    AnalysisModeEnum[] modes = AnalysisModeEnum.values();
    for (int i = 1; i < modes.length; i++) {
      var mode = modes[i];
      SentiStrength sentiStrength = sentiStrengthFactory.build(mode, false, new AnalysisOptionsVO());
      String result = sentiStrength.computeSentimentScores(text);
      String expected = "%d %d %d".formatted(val1, val2, val3[i - 1]);
      Assertions.assertEquals(expected, result);
    }
  }

  /**
   * 检验不同 mood 间输出有区别
   */
  @Test
  void testMoods() {
    final String text = "miss!!";
    String[] expected = new String[]{
        "2 -3", "2 -2", "3 -2"
    };
    int[] moods = new int[]{-1, 0, 1};
    for (int i = 0; i < moods.length; i++) {
      int mood = moods[i];
      AnalysisOptionsVO analysisOptionsVO = new AnalysisOptionsVO();
      analysisOptionsVO.setMood(mood);
      SentiStrength sentiStrength = sentiStrengthFactory.build(AnalysisModeEnum.DEFAULT, false, analysisOptionsVO);
      Assertions.assertEquals(
          expected[i],
          sentiStrength.computeSentimentScores(text)
      );
    }
  }
}

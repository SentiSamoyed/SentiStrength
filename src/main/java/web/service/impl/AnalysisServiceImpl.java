package web.service.impl;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import uk.ac.wlv.sentistrength.SentiStrength;
import web.data.vo.AnalysisOptionsVO;
import web.data.vo.TextAnalysisVO;
import web.enums.AnalysisModeEnum;
import web.factory.SentiStrengthFactory;
import web.service.AnalysisService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AnalysisServiceImpl implements AnalysisService {
  @Setter
  private SentiStrengthFactory sentiStrengthFactory;

  @Autowired
  public AnalysisServiceImpl(SentiStrengthFactory sentiStrengthFactory) {
    this.sentiStrengthFactory = sentiStrengthFactory;
  }

  @Override
  public List<TextAnalysisVO> textAnalysis(String text, AnalysisModeEnum mode, Boolean explain, AnalysisOptionsVO options) {
    SentiStrength sentiStrength = sentiStrengthFactory.build(mode, explain, options);

    return Arrays.stream(text.split("\n"))
        .map(line -> analyzeOneLine(sentiStrength, line, mode, explain))
        .collect(Collectors.toList());
  }

  private TextAnalysisVO analyzeOneLine(SentiStrength sentiStrength, String text, AnalysisModeEnum mode, boolean explain) {
    String result = sentiStrength.computeSentimentScores(text);

    String[] lines = result.split("\n");
    String[] es = lines[0].split(" ");

    TextAnalysisVO analysisVO = new TextAnalysisVO();
    switch (mode) {
      case TRINARY:
      case SCALE:
      case BINARY:
        Assert.isTrue(es.length == 3, "返回结果长度错误.");
        analysisVO.setVal3(Integer.parseInt(es[2]));
      case DEFAULT:
        analysisVO.setVal1(Integer.parseInt(es[0]));
        analysisVO.setVal2(Integer.parseInt(es[1]));
    }

    if (explain) {
      analysisVO.setExplain(lines[1]);
    }

    return analysisVO;
  }
}

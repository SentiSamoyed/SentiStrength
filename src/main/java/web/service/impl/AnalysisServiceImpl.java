package web.service.impl;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.wlv.sentistrength.SentiStrength;
import web.entity.vo.AnalysisOptionsVO;
import web.entity.vo.TextAnalysisVO;
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
    SentiStrength.SentimentScoreResult result = sentiStrength.computeSentimentScoresSeparate(text);

    TextAnalysisVO analysisVO = new TextAnalysisVO();
    analysisVO.setVal1(result.pos());
    analysisVO.setVal2(result.neg());
    analysisVO.setVal3(
        switch (mode) {
          case TRINARY, BINARY -> result.trinary();
          case SCALE -> result.scale();
          default -> 0;
        }
    );

    if (explain) {
      analysisVO.setExplain(result.rationale());
    }

    return analysisVO;
  }
}

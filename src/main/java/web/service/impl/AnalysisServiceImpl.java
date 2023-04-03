package web.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import uk.ac.wlv.sentistrength.SentiStrength;
import web.data.vo.AnalysisOptionsVO;
import web.data.vo.TextAnalysisVO;
import web.enums.AnalysisModeEnum;
import web.service.AnalysisService;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class AnalysisServiceImpl implements AnalysisService {

  private final String sentiDataPath;

  public AnalysisServiceImpl() throws IOException {
    sentiDataPath = new ClassPathResource("SentStrength_Data").getFile().getAbsolutePath();
  }

  @Override
  public List<TextAnalysisVO> textAnalysis(String text, AnalysisModeEnum mode, Boolean explain, AnalysisOptionsVO options) {
    SentiStrength sentiStrength = initSentiStrength(mode, explain, options);

    return Arrays.stream(text.split("\n"))
        .map(line -> analyzeOneLine(sentiStrength, line, mode, explain))
        .collect(Collectors.toList());
  }

  private SentiStrength initSentiStrength(AnalysisModeEnum mode, Boolean explain, AnalysisOptionsVO options) {
    SentiStrength sentiStrength = new SentiStrength();
    List<String> args = getArgs(mode, explain, options);

    args.add("sentidata");
    args.add(sentiDataPath);

    sentiStrength.initialise(args.toArray(new String[0]));
    return sentiStrength;
  }

  private TextAnalysisVO analyzeOneLine(SentiStrength sentiStrength, String text, AnalysisModeEnum mode, boolean explain) {
    String result = sentiStrength.computeSentimentScores(text);

    String[] lines = result.split("\n");
    String[] es = lines[0].split(" ");

    TextAnalysisVO analysisVO = new TextAnalysisVO();
    switch (mode) {
      case TRINARY:
      case SCALE:
        analysisVO.setVal3(Integer.parseInt(es[2]));
      case BINARY:
      case DEFAULT:
        analysisVO.setVal1(Integer.parseInt(es[0]));
        analysisVO.setVal2(Integer.parseInt(es[1]));
    }

    if (explain) {
      analysisVO.setExplain(lines[1]);
    }

    return analysisVO;
  }

  private List<String> getArgs(AnalysisModeEnum mode, Boolean explain, AnalysisOptionsVO options) {
    List<String> args = convertOptions(options);
    if (!mode.equals(AnalysisModeEnum.DEFAULT)) {
      args.add(mode.getVal());
    }

    if (explain) {
      args.add("explain");
    }

    return args;
  }

  private List<String> convertOptions(AnalysisOptionsVO optionsVO) {
    List<String> args = new LinkedList<>();

    Method[] methods = optionsVO.getClass().getMethods();
    for (Method method : methods) {
      // For each getter
      String name = method.getName();
      if (name.startsWith("get") && !"getClass".equals(name)) {
        try {
          Object val = method.invoke(optionsVO);
          String key = name.substring("get".length());
          args.add(key);

          if (!(val instanceof Boolean)) {
            args.add(val.toString());
          }
        } catch (Exception e) {
          log.fatal(e.getLocalizedMessage());
          throw new RuntimeException(e);
        }
      }
    }

    return args;
  }
}

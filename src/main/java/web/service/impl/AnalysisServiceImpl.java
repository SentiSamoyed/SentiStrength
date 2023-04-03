package web.service.impl;

import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import uk.ac.wlv.sentistrength.SentiStrength;
import web.data.vo.AnalysisOptionsVO;
import web.data.vo.FileAnalysisVO;
import web.data.vo.TextAnalysisVO;
import web.service.AnalysisService;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

@Service
@Log4j2
public class AnalysisServiceImpl implements AnalysisService {

  @Override
  public TextAnalysisVO textAnalysis(String text, String mode, Boolean explain, AnalysisOptionsVO options) {
    try {
      SentiStrength sentiStrength = new SentiStrength();
      List<String> args = getArgs(mode, explain, options);
      args.add("sentidata");
      args.add(
          new ClassPathResource("SentStrength_Data").getFile().getAbsolutePath()
      );
      sentiStrength.initialise(args.toArray(new String[0]));
      String result = sentiStrength.computeSentimentScores(text);
      log.info(result);
    } catch (IOException e) {
      log.fatal(e.getLocalizedMessage());
    }

    return null;
  }

  @Override
  public FileAnalysisVO fileAnalysis(String file, String mode, Boolean explain, AnalysisOptionsVO options) {
    return null;
  }

  private List<String> getArgs(String mode, Boolean explain, AnalysisOptionsVO options) {
    List<String> args = convertOptions(options);
    if (!"default".equals(mode)) {
      args.add(mode);
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

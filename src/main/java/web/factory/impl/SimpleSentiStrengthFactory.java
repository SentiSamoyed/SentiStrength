package web.factory.impl;

import common.SentiData;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import uk.ac.wlv.sentistrength.SentiStrength;
import web.data.vo.AnalysisOptionsVO;
import web.enums.AnalysisModeEnum;
import web.factory.SentiStrengthFactory;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * @author tanziyue
 * @date 2023/4/6
 * @description 最简单的 SentiStrength 构建策略
 */
@Component
@Log4j2
public class SimpleSentiStrengthFactory implements SentiStrengthFactory {

  @Override
  public SentiStrength build(AnalysisModeEnum mode, Boolean explain, AnalysisOptionsVO options) {
    SentiStrength sentiStrength = new SentiStrength();
    List<String> args = getArgs(mode, explain, options);

    args.add("sentidata");
    args.add(SentiData.SENTI_DATA_DIR_PATH);

    sentiStrength.initialise(args.toArray(new String[0]));
    return sentiStrength;
  }

  @Override
  public SentiStrength build() {
    return build(AnalysisModeEnum.DEFAULT, false, new AnalysisOptionsVO());
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

          if (val instanceof Boolean b) {
            if (b) {
              args.add(key);
            }
          } else {
            args.add(key);
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

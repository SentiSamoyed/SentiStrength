package web.factory.impl;

import common.SentiData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.ac.wlv.sentistrength.SentiStrength;
import web.entity.vo.AnalysisOptionsVO;
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
@Qualifier("SimpleSentiStrengthFactory")
@Component
@Log4j2
public class SimpleSentiStrengthFactory implements SentiStrengthFactory {

  @Override
  public SentiStrength build(AnalysisModeEnum mode, Boolean explain, AnalysisOptionsVO options) {
    String[] args = getArgs(mode, explain, options);
    return build(args);
  }

  @Override
  public SentiStrength build() {
    return build(AnalysisModeEnum.DEFAULT, false, new AnalysisOptionsVO());
  }

  protected SentiStrength build(String[] args) {
    SentiStrength sentiStrength = new SentiStrength();
    sentiStrength.initialise(args);
    return sentiStrength;
  }

  protected String[] getArgs(AnalysisModeEnum mode, Boolean explain, AnalysisOptionsVO options) {
    List<String> args = convertOptions(options);
    if (!mode.equals(AnalysisModeEnum.DEFAULT)) {
      args.add(mode.getVal());
    }

    if (explain) {
      args.add("explain");
    }

    args.add("sentidata");
    args.add(SentiData.SENTI_DATA_DIR_PATH);

    return args.toArray(new String[0]);
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

package web.factory;

import uk.ac.wlv.sentistrength.SentiStrength;
import web.entity.vo.AnalysisOptionsVO;
import web.enums.AnalysisModeEnum;

/**
 * @author tanziyue
 * @date 2023/4/6
 * @description SentiStrength 的初始化工厂接口
 */
public interface SentiStrengthFactory {
  /**
   * 构建并初始化 SentiStrength 对象
   *
   * @param mode    分析模式
   * @param explain 是否显示解释
   * @param options 分析选项
   * @return SentiStrength 对象
   */
  SentiStrength build(AnalysisModeEnum mode, Boolean explain, AnalysisOptionsVO options);

  /**
   * 创建最简单的 SentiStrength 对象，模式为默认，不解释，所有参数均为默认。
   *
   * @return SentiStrength 对象
   */
  SentiStrength build();
}
